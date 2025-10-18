package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {

    private static final String BASE = "https://dog.ceo/dog-api/documentation/sub-breed";
    private final OkHttpClient client = new OkHttpClient();

    public DogApiBreedFetcher() {
        this(new OkHttpClient());
    }

    public DogApiBreedFetcher(OkHttpClient client) {
        this.client = Objects.requireNonNull(client);
    }

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed)
            throws BreedFatecher.BreedNotFoundException, IOException{

        String url = BASE + "/bread" + breed + "/list";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() == null ? "" : response.body().string();

            if (!response.isSuccessful()) {
                try {
                    JSONOBject err = new JSONObject(body);
                    if ("error".equalsIgnoreCase(err.optString("status"))
                            && err.optInt("code", 0) == 404) {
                        throw new BreedFetcher.BreedNotFoundException(
                                err.optString("message", "Breed not found"));
                    }
                } catch (Exception ignored) { /* fall through to generic IO error */ }

                throw new IOException("HTTP " + response.code() + " calling " + url);
            }

            JSONObject json = new JSONObject(body);

            if ("error".equalsIgnoreCase(json.optString("status"))) {
                if (json.optInt("code", 0) == 404) {
                    throw new BreedFetcher.BreedNotFoundException(
                            json.optString("message", "Breed not found"));
                }
                throw new IOException(json.optString("message", "API error"));
            }

            JSONArray arr = json.optJSONArray("message");
            List<String> result = new ArrayList<>();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    result.add(arr.getString(i));
                }
            }
            return result;
        }
    }
}
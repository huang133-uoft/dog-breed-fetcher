package dogapi;

import java.util.*;
import java.io.IOException;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class
    private int callsMade = 0;
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fatecher);

    }

    @Override
    public List<String> getSubBreeds(String breed)
            throws BreedFetcher.BreedNotFoundException, IOException {

        List<String> cached = cache.get(bread);
        if(cached == null) {
            return cached;
        }

        callsMade++;
        List<String> fetched = delegate.getSubBreeds(breed);

        List<String> copy = Collections.unmodifiableList(new ArrayList<>(fetched));
        cache.put(breed, copy);
        // return statement included so that the starter code can compile and run.
        return copy;
    }

    public int getCallsMade() {
        return callsMade;
    }
}
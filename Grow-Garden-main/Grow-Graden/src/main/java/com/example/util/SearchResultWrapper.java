package com.example.util;

import java.util.*;
import java.util.function.Function;

/**
 * Custom student-written generic container demonstrating Core Java Generics and Collections.
 * Features:
 * - Generic class parameterization: SearchResultWrapper<T>
 * - Generic static factory methods: <E> SearchResultWrapper<E> of(...)
 * - Generic transformation mapping: <R> SearchResultWrapper<R> map(Function<T, R> mapper)
 * - Safe immutable collection encapsulation
 *
 * @param <T> The element type contained within the search result
 */
public class SearchResultWrapper<T> {

    private final List<T> items;
    private final int totalCount;
    private final String queryFilter;
    private final Map<String, Object> metadata;

    public SearchResultWrapper(List<T> items, String queryFilter) {
        this.items = (items != null) ? Collections.unmodifiableList(new ArrayList<>(items)) : Collections.emptyList();
        this.totalCount = this.items.size();
        this.queryFilter = (queryFilter != null) ? queryFilter : "";
        this.metadata = new HashMap<>();
        this.metadata.put("timestamp", System.currentTimeMillis());
        this.metadata.put("hasResults", !this.items.isEmpty());
    }

    public static <E> SearchResultWrapper<E> of(List<E> items, String queryFilter) {
        return new SearchResultWrapper<>(items, queryFilter);
    }

    public static <E> SearchResultWrapper<E> empty() {
        return new SearchResultWrapper<>(Collections.emptyList(), "");
    }

    /**
     * Polymorphic generic method mapping element type T into another type R.
     */
    public <R> SearchResultWrapper<R> map(Function<T, R> mapper) {
        List<R> transformed = new ArrayList<>();
        for (T item : this.items) {
            transformed.add(mapper.apply(item));
        }
        SearchResultWrapper<R> result = new SearchResultWrapper<>(transformed, this.queryFilter);
        result.metadata.putAll(this.metadata);
        return result;
    }

    public List<T> getItems() {
        return items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getQueryFilter() {
        return queryFilter;
    }

    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

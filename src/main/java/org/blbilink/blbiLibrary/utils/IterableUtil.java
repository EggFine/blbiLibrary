package org.blbilink.blbiLibrary.utils;

import java.util.Optional;
import java.util.function.Predicate;

public class IterableUtil {
    public static <T> Optional<T> getIf(Iterable<T> iterable, Predicate<T> filter) {
        for (T item : iterable) {
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }
}

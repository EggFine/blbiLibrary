package org.blbilink.blbiLibrary.utils;
import java.util.function.Predicate;
import java.util.Optional;

public class IterableUtil {
    public static <T> Optional<T> getIf(Iterable<T> iterable, Predicate<T> filter){
        for (T item : iterable){
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }
}

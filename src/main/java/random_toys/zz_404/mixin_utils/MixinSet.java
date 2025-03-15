package random_toys.zz_404.mixin_utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

public class MixinSet<T> implements Iterable<T> {
    private final HashSet<T> set;

    public MixinSet() {
        set = new HashSet<>();
    }

    public void add(T t) {
        set.add(t);
    }

    public boolean check(T t) {
        return set.contains(t);
    }

    public boolean anyMatch(Predicate<T> predicate) {
        return set.stream().anyMatch(predicate);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return set.iterator();
    }
}

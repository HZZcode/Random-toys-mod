package random_toys.zz_404.mixin_utils;

import java.util.HashSet;
import java.util.function.Predicate;

public class MixinSet<T> {
    private final HashSet<T> set;

    public MixinSet() {
        set = new HashSet<>();
    }

    public MixinSet(HashSet<T> set) {
        this.set = set;
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
}

package random_toys.zz_404.mixin_utils;

import java.util.HashSet;

public class MixinSet<T> extends HashSet<T> {
    public MixinSet() {
        super();
    }
} // God knows why I wrote this class instead of simply use HashSet

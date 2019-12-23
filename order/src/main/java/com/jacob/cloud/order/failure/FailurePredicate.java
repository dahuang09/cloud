package com.jacob.cloud.order.failure;

import java.util.function.Predicate;

public class FailurePredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) {
        return true;
    }
}

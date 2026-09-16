package com.ourstory.oscobblemon.event;

import com.cobblemon.mod.common.api.reactive.ObservableSubscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns a group of Cobblemon event subscriptions and can dispose them together.
 *
 * <p>This is useful for feature lifecycles, reloadable systems and tests where
 * duplicate subscriptions would otherwise accumulate.</p>
 */
public final class EventSubscriptionGroup implements AutoCloseable {
    private final List<ObservableSubscription<?>> subscriptions = new ArrayList<>();

    /**
     * Tracks a subscription and returns the same instance for convenient use.
     */
    public synchronized <T> ObservableSubscription<T> track(ObservableSubscription<T> subscription) {
        Objects.requireNonNull(subscription, "subscription");
        subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Returns the number of currently tracked subscriptions.
     */
    public synchronized int size() {
        return subscriptions.size();
    }

    /**
     * Returns whether no subscriptions are currently tracked.
     */
    public synchronized boolean isEmpty() {
        return subscriptions.isEmpty();
    }

    /**
     * Unsubscribes all tracked subscriptions and clears the group.
     */
    public void unsubscribeAll() {
        List<ObservableSubscription<?>> snapshot;

        synchronized (this) {
            if (subscriptions.isEmpty()) {
                return;
            }
            snapshot = List.copyOf(subscriptions);
            subscriptions.clear();
        }

        for (ObservableSubscription<?> subscription : snapshot) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void close() {
        unsubscribeAll();
    }
}

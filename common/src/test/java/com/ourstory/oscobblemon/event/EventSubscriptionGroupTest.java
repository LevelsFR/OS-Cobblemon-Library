package com.ourstory.oscobblemon.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.reactive.Observable;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventSubscriptionGroupTest {
    @Test
    void closeUnsubscribesEveryTrackedSubscriptionAndCanBeRepeated() {
        TestObservable<Integer> observable = new TestObservable<>();
        List<Integer> received = new ArrayList<>();
        ObservableSubscription<Integer> first = observable.subscribe(
                Priority.NORMAL,
                value -> {
                    received.add(value);
                    return Unit.INSTANCE;
                }
        );
        ObservableSubscription<Integer> second = observable.subscribe(
                Priority.NORMAL,
                value -> {
                    received.add(value * 10);
                    return Unit.INSTANCE;
                }
        );
        EventSubscriptionGroup group = new EventSubscriptionGroup();

        assertSame(first, group.track(first));
        group.track(second);
        assertEquals(2, group.size());
        observable.emit(3);
        assertEquals(List.of(3, 30), received);

        group.close();
        assertTrue(group.isEmpty());
        assertFalse(first.getAlive());
        assertFalse(second.getAlive());
        observable.emit(4);
        assertEquals(List.of(3, 30), received);

        group.unsubscribeAll();
        assertTrue(group.isEmpty());
    }

    private static final class TestObservable<T> implements Observable<T> {
        private final List<ObservableSubscription<T>> subscriptions = new ArrayList<>();

        @Override
        public ObservableSubscription<T> subscribe(
                Priority priority,
                Function1<? super T, Unit> handler
        ) {
            ObservableSubscription<T> subscription = new ObservableSubscription<>(this, handler);
            subscriptions.add(subscription);
            return subscription;
        }

        @Override
        public void unsubscribe(ObservableSubscription<T> subscription) {
            subscriptions.remove(subscription);
        }

        private void emit(T value) {
            for (ObservableSubscription<T> subscription : List.copyOf(subscriptions)) {
                if (subscription.getAlive()) {
                    subscription.handle(value);
                }
            }
        }
    }
}

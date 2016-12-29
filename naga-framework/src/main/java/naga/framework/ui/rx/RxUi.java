package naga.framework.ui.rx;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import naga.fx.spi.Toolkit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * @author Bruno Salmon
 */
public class RxUi {

    public static <T> Observable<T> observe(ObservableValue<T> observableValue) {
        return Observable.create(subscriber -> {
            subscriber.onNext(observableValue.getValue());
            ChangeListener<T> listener = (value, prev, current) -> subscriber.onNext(current);
            observableValue.addListener(listener);
            subscriber.add(unsubscribeInUiThread(() -> observableValue.removeListener(listener)));
        });
    }

    static <T> Observable<T> observeIf(Observable<T> observable, Observable<Boolean> conditionObservable) {
        return Observable.combineLatest(observable, conditionObservable, (value, condition) -> condition == Boolean.TRUE ? value : null);
    }

    public static <T> Observable<T> observeIf(Observable<T> observable, Property<Boolean> conditionProperty) {
        return observeIf(observable, observe(conditionProperty));
    }

    /**
     * Create an Subscription that always runs <code>unsubscribe</code> in the event dispatch thread.
     *
     * @param unsubscribe the action to be performed in the ui thread at un-subscription
     * @return an Subscription that always runs <code>unsubscribe</code> in the event dispatch thread.
     */
    static Subscription unsubscribeInUiThread(Action0 unsubscribe) {
        return Subscriptions.create(() -> {
            if (Toolkit.isUiThread())
                unsubscribe.call();
            else {
                Scheduler.Worker worker = RxScheduler.UI_SCHEDULER.createWorker();
                worker.schedule(() -> {
                    unsubscribe.call();
                    worker.unsubscribe();
                });
            }
        });
    }
}


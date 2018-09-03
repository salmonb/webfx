package webfx.fxkits.core.properties.conversion;

import emul.javafx.beans.InvalidationListener;
import emul.javafx.beans.value.ChangeListener;
import emul.javafx.beans.value.ObservableValue;
import webfx.platforms.core.util.function.Converter;

/**
 * @author Bruno Salmon
 */
public class ConvertedObservableValue<A, B> implements ObservableValue<A> {

    protected final ObservableValue<B> observableValue;
    protected final Converter<B,A> bToAConverter;

    public ConvertedObservableValue(ObservableValue<B> observableValue, Converter<B, A> bToAConverter) {
        this.observableValue = observableValue;
        this.bToAConverter = bToAConverter;
    }

    @Override
    public void addListener(ChangeListener<? super A> listener) {
        observableValue.addListener((observable, oldValue, newValue) -> {
            listener.changed(ConvertedObservableValue.this, bToAConverter.convert(oldValue), bToAConverter.convert(newValue));
        });
    }

    @Override
    public void removeListener(ChangeListener<? super A> listener) {
        System.out.println("ConvertedProperty.removeListener() not yet implemented");
    }

    @Override
    public A getValue() {
        return bToAConverter.convert(observableValue.getValue());
    }


    @Override
    public void addListener(InvalidationListener listener) {
        observableValue.addListener(observable -> {
            listener.invalidated(ConvertedObservableValue.this);
        });
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        System.out.println("ConvertedProperty.removeListener() not yet implemented");
    }
}
package naga.framework.ui.i18n.impl;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.framework.ui.i18n.Dictionary;
import naga.framework.ui.i18n.I18n;
import naga.fx.spi.Toolkit;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author Bruno Salmon
 */
public class I18nImpl implements I18n {

    private final Map<Object, Reference<Property<String>>> translations = new HashMap<>();
    private boolean dictionaryLoadRequired;
    private DictionaryLoader dictionaryLoader;
    private Set<Object> unloadedKeys;

    public I18nImpl(DictionaryLoader dictionaryLoader) {
        this(dictionaryLoader, "en");
    }

    public I18nImpl(DictionaryLoader dictionaryLoader, Object initialLanguage) {
        this.dictionaryLoader = dictionaryLoader;
        languageProperty.addListener((observable, oldValue, newValue) -> onLanguageChanged());
        setLanguage(initialLanguage);
    }

    private Property<Object> languageProperty = new SimpleObjectProperty<>();
    @Override
    public Property<Object> languageProperty() {
        return languageProperty;
    }

    private Property<Dictionary> dictionaryProperty = new SimpleObjectProperty<>();
    @Override
    public Property<Dictionary> dictionaryProperty() {
        return dictionaryProperty;
    }

    @Override
    public Property<String> translationProperty(Object key) {
        Property<String> translationProperty = getTranslationProperty(key);
        if (translationProperty == null)
            synchronized (translations) {
                translations.put(key, new WeakReference<>(translationProperty = createTranslationProperty(key)));
            }
        return translationProperty;
    }

    private Property<String> getTranslationProperty(Object key) {
        Reference<Property<String>> ref = translations.get(key);
        return ref == null ? null : ref.get();
    }

    private Property<String> createTranslationProperty(Object key) {
        return updateTranslation(new SimpleObjectProperty<>(), key);
    }

    private void onLanguageChanged() {
        dictionaryLoadRequired = true;
        updateTranslations();
    }

    private synchronized void updateTranslations() {
        synchronized (translations) {
            for (Iterator<Map.Entry<Object, Reference<Property<String>>>> it = translations.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Object, Reference<Property<String>>> entry = it.next();
                Reference<Property<String>> value = entry.getValue();
                Property<String> translationProperty = value == null ? null : value.get();
                if (translationProperty == null)
                    it.remove();
                else
                    updateTranslation(translationProperty, entry.getKey());
            }
        }
    }

    private Property<String> updateTranslation(Property<String> translationProperty, Object key) {
        if (dictionaryLoadRequired && dictionaryLoader != null) {
            if (unloadedKeys != null)
                unloadedKeys.add(key);
            else {
                unloadedKeys = new HashSet<>();
                unloadedKeys.add(key);
                Toolkit.get().scheduler().scheduleDeferred(() -> {
                    dictionaryLoader.loadDictionary(getLanguage(), unloadedKeys).setHandler(asyncResult -> {
                        dictionaryProperty.setValue(asyncResult.result());
                        dictionaryLoadRequired = false;
                        updateTranslations();
                    });
                    unloadedKeys = null;
                });
            }
        } else
            translationProperty.setValue(instantTranslate(key));
        return translationProperty;
    }
}

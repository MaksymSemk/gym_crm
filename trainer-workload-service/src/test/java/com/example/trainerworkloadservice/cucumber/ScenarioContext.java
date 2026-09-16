package com.example.trainerworkloadservice.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ScenarioContext {

    private final Map<String, Object> storage = new HashMap<>();

    public void set(String key, Object value) {
        storage.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) storage.get(key);
    }

    public <T> T get(String key, Class<T> targetClass) {
        return targetClass.cast(storage.get(key));
    }

    public boolean contains(String key) {
        return storage.containsKey(key);
    }

    public void clear() {
        storage.clear();
    }
}
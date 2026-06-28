package com.example.gym_crm.config.storage;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training_type.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class StorageInitializationPostProcessor implements BeanPostProcessor {

    @Value("${storage.trainer-file-path}")
    private String trainerFilePath;

    @Value("${storage.trainee-file-path}")
    private String traineeFilePath;

    @Value("${storage.training-type-file-path}")
    private String trainingTypeFilePath;

    @Value("${storage.training-file-path}")
    private String trainingFilePath;

    private final ObjectMapper objectMapper;

    public StorageInitializationPostProcessor() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof Map)) {
            return bean;
        }

        switch (beanName) {
            case "trainerStorage" ->
                    loadStorageData((Map<UUID, Trainer>) bean, trainerFilePath, Trainer.class, Trainer::getId);

            case "traineeStorage" ->
                    loadStorageData((Map<UUID, Trainee>) bean, traineeFilePath, Trainee.class, Trainee::getId);

            case "trainingStorage" ->
                    loadStorageData((Map<TrainingId, Training>) bean, trainingFilePath, Training.class, Training::getId);

            case "trainingTypeStorage" ->
                    loadStorageData((Map<String, TrainingType>) bean, trainingTypeFilePath, TrainingType.class, TrainingType::getId);
        }
        log.info("StorageInitializationPostProcessor: Initialized storage for bean: {}", beanName);
        return bean;
    }

    private <K, V> void loadStorageData(Map<K, V> storage, String path, Class<V> valueType, Function<V, K> keyExtractor) {
        if (path == null || path.isBlank()) {
            return;
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return;
            }

            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, valueType);

            List<V> items = objectMapper.readValue(is, listType);

            items.forEach(item -> storage.put(keyExtractor.apply(item), item));
            log.debug("StorageInitializationPostProcessor: Loaded {} items into storage from file: {}", items.size(), path);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Jackson failed to parse initialization file [%s] for type [%s]", path, valueType.getSimpleName()), e
            );
        }
    }
}
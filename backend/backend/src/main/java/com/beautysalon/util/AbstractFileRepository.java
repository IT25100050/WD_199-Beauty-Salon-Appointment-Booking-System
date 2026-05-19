package com.beautysalon.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Common abstract repository for file-based CRUD operations.
 *
 * OOP Concepts Applied:
 * - Abstraction: Common file handling logic is hidden inside this abstract class.
 * - Inheritance: Module repositories extend this class and reuse common CRUD methods.
 * - Encapsulation: File read/write logic is kept in one place instead of exposing it to controllers/services.
 *
 * @param <T>  Entity type
 * @param <ID> ID type
 */
public abstract class AbstractFileRepository<T, ID> {

    protected abstract String getFilePath();

    protected abstract String serialize(T entity);

    protected abstract T deserialize(String record);

    protected abstract String getIdPrefix(String id);

    protected abstract String getEntityIdPrefix(T entity);

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        Path path = Path.of(getFilePath());

        if (!Files.exists(path)) {
            return entities;
        }

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    entities.add(deserialize(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + getFilePath(), e);
        }

        return entities;
    }

    public Optional<T> findById(ID id) {
        Path path = Path.of(getFilePath());

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        String prefix = getIdPrefix(String.valueOf(id));

        try {
            return Files.readAllLines(path).stream()
                    .filter(line -> line != null && line.startsWith(prefix))
                    .findFirst()
                    .map(this::deserialize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to find record by ID: " + id, e);
        }
    }

    public T save(T entity) {
        Path path = Path.of(getFilePath());

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.writeString(
                    path,
                    serialize(entity) + System.lineSeparator(),
                    StandardOpenOption.APPEND
            );

            return entity;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save record to file: " + getFilePath(), e);
        }
    }

    public T update(T entity) {
        Path path = Path.of(getFilePath());

        if (!Files.exists(path)) {
            return save(entity);
        }

        String entityPrefix = getEntityIdPrefix(entity);

        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(path));
            boolean updated = false;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(entityPrefix)) {
                    lines.set(i, serialize(entity));
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                lines.add(serialize(entity));
            }

            Files.write(path, lines);
            return entity;
        } catch (IOException e) {
            throw new RuntimeException("Failed to update record in file: " + getFilePath(), e);
        }
    }

    public void deleteById(ID id) {
        Path path = Path.of(getFilePath());

        if (!Files.exists(path)) {
            return;
        }

        String prefix = getIdPrefix(String.valueOf(id));

        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(path));
            lines.removeIf(line -> line != null && line.startsWith(prefix));
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete record with ID: " + id, e);
        }
    }

    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}

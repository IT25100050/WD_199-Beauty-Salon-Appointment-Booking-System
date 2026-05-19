package com.beautysalon.util;

import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> {

    protected abstract String getFilePath();
    protected abstract String serialize(T entity);
    protected abstract T deserialize(String record);
    protected abstract String getIdPrefix(ID id);
    protected abstract String getEntityIdPrefix(T entity);

    public void save(T entity) {
        FileHandlerUtil.saveRecord(getFilePath(), serialize(entity));
    }

    public List<T> findAll() {
        return FileHandlerUtil.readAll(getFilePath()).stream()
                .map(this::deserialize)
                .toList();
    }

    public Optional<T> findById(ID id) {
        return FileHandlerUtil.findById(getFilePath(), getIdPrefix(id))
                .map(this::deserialize);
    }

    public void update(T entity) {
        FileHandlerUtil.updateRecord(getFilePath(), getEntityIdPrefix(entity), serialize(entity));
    }

    public void deleteById(ID id) {
        FileHandlerUtil.deleteRecord(getFilePath(), getIdPrefix(id));
    }
}

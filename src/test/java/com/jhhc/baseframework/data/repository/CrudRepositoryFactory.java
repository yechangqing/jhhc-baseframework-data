package com.jhhc.baseframework.data.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 *
 * @author yecq
 */
@Component
public class CrudRepositoryFactory {

    @Autowired
    private JhhcJdbcMapCrudRepository repo;

    @Autowired
    private JhhcJdbcCrudRepository reg;

    @Autowired
    private JhhcMongoCrudRepository mongo;

    public CrudRepository getJdbcRepository(String tableName) {
        this.repo.setTable(tableName);
        return this.repo;
    }

    public CrudRepository getRepository(String table, Class cls) {
        this.reg.setTable(table);
        this.reg.setClass(cls);
        return this.reg;
    }

    public CrudRepository getRepository(String table) {
        return getRepository(table, null);
    }

    public CrudRepository getRepository(Class cls) {
        return getRepository(null, cls);
    }

    public CrudRepository getRepository() {
        return getRepository(null, null);
    }

    public CrudRepository getMongoRepository(String collection, Class cls) {
        this.mongo.setCollection(collection);
        this.mongo.setClass(cls);
        return this.mongo;
    }

    public CrudRepository getMongoRepository(String collection) {
        return getMongoRepository(collection, null);
    }

    public CrudRepository getMongoRepository() {
        return getMongoRepository(null, null);
    }

    public void changeMongoCollection(String collection) {
        this.mongo.setCollection(collection);
    }
}

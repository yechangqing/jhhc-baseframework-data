package com.jhhc.baseframework.data.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

/**
 *
 * @author yecq
 */
@Component
public class RepositoryFactory {

    @Autowired
    private JhhcJdbcMapRepository repo;

    @Autowired
    private JhhcJdbcRepository reg;

    @Autowired
    private JhhcMongoRepository mongo;

    public PagingAndSortingRepository getMapPagingAndSortingRepository(String table) {
        this.repo.setTable(table);
        return this.repo;
    }

    public PagingAndSortingRepository getMapPagingAndSortingRepository() {
        return this.repo;
    }

    public PagingAndSortingRepository getPagingAndSortingRepository(String table, Class cls) {
        this.reg.setTable(table);
        this.reg.setClass(cls);
        return this.reg;
    }

    public PagingAndSortingRepository getPagingAndSortingRepository(String table) {
        return getPagingAndSortingRepository(table, null);
    }

    public PagingAndSortingRepository getPagingAndSortingRepository(Class cls) {
        return getPagingAndSortingRepository(null, cls);
    }

    public PagingAndSortingRepository getPagingAndSortingRepository() {
        return getPagingAndSortingRepository(null, null);
    }

    public PagingAndSortingRepository getMongoPagingAndSortingRepository(String collection, Class cls) {
        this.mongo.setCollection(collection);
        this.mongo.setClass(cls);
        return this.mongo;
    }

    public PagingAndSortingRepository getMongoPagingAndSortingRepository(String collection) {
        return getMongoPagingAndSortingRepository(collection, null);
    }

    public CrudRepository getMongoPagingAndSortingRepository() {
        return getMongoPagingAndSortingRepository(null, null);
    }

    public void changeMongoCollection(String collection) {
        this.mongo.setCollection(collection);
    }
}

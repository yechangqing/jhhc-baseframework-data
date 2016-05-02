package com.jhhc.baseframework.data.repository;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 只实现了Crud，暂未实现分页
 *
 * @author yecq
 */
@Repository
@Scope("prototype")
public class JhhcMongoCrudRepository implements PagingAndSortingRepository<Object, String> {

    @Autowired
    private MongoTemplate mongo;
    private String collection;
    private Class cls;

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setClass(Class cls) {
        this.cls = cls;
    }

    protected void checkCollection() {
        if (this.collection == null || this.collection.trim().equals("")) {
            throw new IllegalStateException("collection为空");
        }
    }

    private boolean isCollection() {
        return this.collection != null && !this.collection.trim().equals("");
    }

    @Override
    public <S> S save(S s) {
        if (s == null) {
            throw new IllegalArgumentException("save参数为空");
        }
        // 看是否已经选定了名字
        if (isCollection()) {
            this.mongo.save(s, this.collection);
        } else {
            this.mongo.save(s);
        }
        return s;
    }

    @Override
    public <S> Iterable<S> save(Iterable<S> itrbl) {
        if (itrbl == null) {
            throw new IllegalArgumentException("save参数为空");
        }
        Iterator<S> ite = itrbl.iterator();
        while (ite.hasNext()) {
            save(ite.next());
        }
        return itrbl;
    }

    @Override
    public Object findOne(String id) {
        checkCollection();
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("find参数为空");
        }
        if (this.cls == null) {
            return this.mongo.findById(id, Object.class, this.collection);  // 可能有问题，找不到的话要返回null
        } else {
            return this.mongo.findById(id, cls, this.collection);
        }
    }

    @Override
    public boolean exists(String id) {
        return findOne(id) != null;
    }

    @Override
    public Iterable<Object> findAll() {
        checkCollection();
        return this.mongo.findAll(Object.class, this.collection);
    }

    @Override
    public Iterable<Object> findAll(Iterable<String> itrbl) {
        checkCollection();
        if (itrbl == null) {
            throw new IllegalArgumentException("find参数为空");
        }
        Iterator<String> ite = itrbl.iterator();
        List ret = new LinkedList();
        while (ite.hasNext()) {
            Object o = findOne(ite.next());
            if (o != null) {
                ret.add(o);
            }
        }
        return ret;
    }

    @Override
    public long count() {
        checkCollection();
        return this.mongo.findAll(Object.class, this.collection).size();

    }

    @Override
    public void delete(String id) {
        checkCollection();
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("delete参数为空");
        }
        this.mongo.remove(new Query(where("_id").is(id)), this.collection);
    }

    @Override
    public void delete(Object t) {
        if (t == null) {
            throw new IllegalArgumentException("delete参数为空");
        }
        if (this.collection != null) {
            this.mongo.remove(t, this.collection);
        } else {
            // 根据Object获得类名
            String clsname = t.getClass().getSimpleName().toLowerCase();
            this.mongo.remove(t, clsname);
        }
    }

    @Override
    public void delete(Iterable<? extends Object> itrbl) {
        if (itrbl == null) {
            throw new IllegalArgumentException("delete参数为空");
        }
        Iterator<? extends Object> ite = itrbl.iterator();
        while (ite.hasNext()) {
            delete(ite.next());
        }
    }

    @Override
    public void deleteAll() {
        checkCollection();
        this.mongo.dropCollection(this.collection);
    }

    @Override
    public Iterable<Object> findAll(Sort sort) {
        throw new UnsupportedOperationException("排序功能待开发");
    }

    @Override
    public Page<Object> findAll(Pageable pgbl) {
        throw new UnsupportedOperationException("分页功能待开发");
    }
}

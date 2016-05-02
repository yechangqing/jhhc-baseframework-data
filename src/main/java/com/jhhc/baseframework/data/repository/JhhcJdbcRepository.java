package com.jhhc.baseframework.data.repository;

import com.google.gson.Gson;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

/**
 *
 * @author yecq
 */
@Component
@Scope("prototype")
public class JhhcJdbcRepository implements PagingAndSortingRepository<Object, String> {

    @Autowired
    private JhhcJdbcMapRepository mprepo;
    private String table;
    private Class cls;

    public void setTable(String table) {
        this.table = table;
    }

    public void setClass(Class cls) {
        this.cls = cls;
    }

    private void checkTable() {
        if (this.table == null || this.table.trim().equals("")) {
            throw new IllegalStateException("表名称为空");
        }
    }

    private void checkClass() {
        if (this.cls == null) {
            throw new IllegalStateException("Class为空");
        }
    }

    private Map object2Map(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return gson.fromJson(json, Map.class);
    }

    private <T> T map2Object(Map map, Class<T> cls) {
        String json = new Gson().toJson(map);
        return new Gson().fromJson(json, cls);
    }

    private <S> void initTable(S s) {
        if (this.table == null) {
            this.mprepo.setTable(s.getClass().getSimpleName());
        } else {
            this.mprepo.setTable(this.table);
        }
    }

    @Override
    public <S> S save(S s) {
        if (s == null) {
            throw new IllegalArgumentException("save参数为空");
        }
        initTable(s);
        Map map = object2Map(s);
        this.mprepo.save(map);
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
        checkTable();
        checkClass();
        this.mprepo.setTable(this.table);
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("find参数为空");
        }
        Map map = this.mprepo.findOne(id);
        return map == null ? null : map2Object(map, this.cls);
    }

    @Override
    public boolean exists(String id) {
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("exists参数为空");
        }
        return findOne(id) != null;
    }

    @Override
    public Iterable<Object> findAll() {
        checkTable();
        checkClass();
        this.mprepo.setTable(this.table);
        List ret = new LinkedList();
        Iterator<Map<String, Object>> ite = this.mprepo.findAll().iterator();
        while (ite.hasNext()) {
            Map<String, Object> mp = ite.next();
            ret.add(map2Object(mp, this.cls));
        }
        return ret;
    }

    @Override
    public Iterable<Object> findAll(Iterable<String> itrbl) {
        if (itrbl == null) {
            throw new IllegalArgumentException("find参数为空");
        }
        checkTable();
        checkClass();
        this.mprepo.setTable(this.table);
        List ret = new LinkedList();
        Iterator<Map<String, Object>> ite = this.mprepo.findAll(itrbl).iterator();
        while (ite.hasNext()) {
            Map<String, Object> mp = ite.next();
            ret.add(map2Object(mp, this.cls));
        }
        return ret;
    }

    @Override
    public long count() {
        checkTable();
        this.mprepo.setTable(this.table);
        return this.mprepo.count();
    }

    @Override
    public void delete(String id) {
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("delete参数为空");
        }
        checkTable();
        this.mprepo.setTable(this.table);
        this.mprepo.delete(id);
    }

    @Override
    public void delete(Object t) {
        if (t == null) {
            throw new IllegalArgumentException("delete参数为空");
        }
        initTable(t);
        this.mprepo.delete(object2Map(t));
    }

    @Override
    public void delete(Iterable<? extends Object> itrbl) {
        if (itrbl == null) {
            throw new IllegalArgumentException("delete参数为空");
        }
        Iterator ite = itrbl.iterator();
        while (ite.hasNext()) {
            delete(ite.next());
        }
    }

    @Override
    public void deleteAll() {
        checkTable();
        this.mprepo.setTable(this.table);
        this.mprepo.deleteAll();
    }

    @Override
    public Iterable<Object> findAll(Sort sort) {
        throw new UnsupportedOperationException("Not 排序功能待开发");
    }

    @Override
    public Page<Object> findAll(Pageable pgbl) {
        if (pgbl == null) {
            throw new IllegalArgumentException("分页参数为空");
        }
        checkTable();
        checkClass();
        this.mprepo.setTable(this.table);
        List<Map<String, Object>> ret1 = this.mprepo.findAll(pgbl).getContent();
        // 转为class
        List ret2 = new LinkedList();
        Iterator<Map<String, Object>> ite2 = ret1.iterator();
        while (ite2.hasNext()) {
            ret2.add(map2Object(ite2.next(), cls));
        }
        return new PageImpl(ret2);
    }

}

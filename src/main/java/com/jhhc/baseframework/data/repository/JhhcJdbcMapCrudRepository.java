package com.jhhc.baseframework.data.repository;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 利用Map<String,Object>作为对象，String作为id
 *
 * @author yecq
 */
@Repository
@Scope("prototype")
public class JhhcJdbcMapCrudRepository implements PagingAndSortingRepository<Map<String, Object>, String> {

    @Autowired
    protected JdbcTemplate jdbc;
    protected String table;

    public void setTable(String table) {
        this.table = table;
        checkTable();
        this.table = this.table.trim();
    }

    protected void checkTable() {
        if (this.table == null || this.table.trim().equals("")) {
            throw new IllegalStateException("表名称为空");
        }
    }

    @Override
    public <S extends Map<String, Object>> S save(S hv) {
        checkTable();
        if (hv == null || hv.isEmpty()) {
            // 暂不支持空的插入
            throw new IllegalArgumentException("save的数据为空");
        }
        if (hv.containsKey("id")) {
            // 表示修改
            if (hv.size() == 1) {
                return hv;
            }
            Object id = hv.remove("id");
            String stmt = "";
            List data = new LinkedList();
            Iterator<Entry<String, Object>> ite = hv.entrySet().iterator();
            while (ite.hasNext()) {
                Entry<String, Object> ent = ite.next();
                stmt += ent.getKey() + " = ?, ";
                data.add(ent.getValue());
            }
            data.add(id);
            stmt = stmt.substring(0, stmt.length() - 2);
            stmt = "update " + this.table + " set " + stmt + " where id = ?";
            this.jdbc.update(stmt, data.toArray());
            hv.put("id", id);
        } else {
//            表示插入
            String h = "";
            String v = "";
            List data = new LinkedList();
            Iterator<Entry<String, Object>> ite = hv.entrySet().iterator();
            while (ite.hasNext()) {
                Entry<String, Object> ent = ite.next();
                h += ent.getKey() + ",";
                v += "?,";
                data.add(ent.getValue());
            }
            h = h.substring(0, h.length() - 1);
            v = v.substring(0, v.length() - 1);
            String stmt = "insert into " + this.table + "(" + h + ") values(" + v + ")";
            this.jdbc.update(stmt, data.toArray());
        }
        return hv;
    }

    @Override
    public <S extends Map<String, Object>> Iterable<S> save(Iterable<S> itrbl) {
        checkTable();
        if (itrbl == null) {
            throw new IllegalArgumentException("save的列表参数为空");
        }
        Iterator<S> ite = itrbl.iterator();
        while (ite.hasNext()) {
            save(ite.next());
        }
        return itrbl;
    }

    @Override
    public Map<String, Object> findOne(String id) {
        checkTable();
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("find参数为空");
        }
        try {
            Map<String, Object> map = this.jdbc.queryForMap("select * from " + this.table + " where id = " + id);
            return map;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean exists(String id) {
        checkTable();
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("find参数为空");
        }
        return findOne(id) != null;
    }

    @Override
    public Iterable<Map<String, Object>> findAll() {
        checkTable();
        return this.jdbc.queryForList("select * from " + this.table);
    }

    @Override
    public Iterable<Map<String, Object>> findAll(Iterable<String> itrbl) {
        checkTable();
        if (itrbl == null) {
            throw new IllegalArgumentException("find的列表参数为空");
        }
        Iterator<String> ite = itrbl.iterator();
        String stmt = "";
        List<Object> args = new LinkedList();
        while (ite.hasNext()) {
            stmt += "?,";
            args.add(ite.next());
        }
        stmt = stmt.substring(0, stmt.length() - 1);
        stmt = "select * from " + this.table + " where id in (" + stmt + ")";
        return this.jdbc.queryForList(stmt, args.toArray());
    }

    @Override
    public long count() {
        checkTable();
        return this.jdbc.queryForObject("select count(id) from " + this.table, Long.class);
    }

    @Override
    public void delete(String id) {
        checkTable();
        if (id == null || id.trim().equals("")) {
            throw new IllegalArgumentException("delete参数为空");
        }
        this.jdbc.update("delete from " + this.table + " where id = ?", id);
    }

    @Override
    public void delete(Map<String, Object> t) {
        checkTable();
        if (t == null || t.isEmpty()) {
            return;
        }

        String stmt = "";
        List<Object> args = new LinkedList();
        Iterator<Entry<String, Object>> ite = t.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, Object> ent = ite.next();
            stmt += ent.getKey() + "=? and ";
            args.add(ent.getValue());
        }
        stmt = stmt.substring(0, stmt.length() - 5);
        stmt = "delete from " + this.table + " where " + stmt;
        this.jdbc.update(stmt, args.toArray());
    }

    @Override
    public void delete(Iterable<? extends Map<String, Object>> itrbl) {
        checkTable();
        if (itrbl == null) {
            throw new IllegalArgumentException("delete参数为空");
        }
        Iterator<? extends Map<String, Object>> ite = itrbl.iterator();
        while (ite.hasNext()) {
            delete(ite.next());
        }
    }

    @Override
    public void deleteAll() {
        checkTable();
        this.jdbc.update("delete from " + this.table + " where true");
    }

    @Override
    public Iterable<Map<String, Object>> findAll(Sort sort) {
        // 这个暂时还没做
        throw new UnsupportedOperationException("排序功能待开发");
    }

    // 实际的Pagable接口实现类为PageRequest，Page接口实现类为PageImpl
    @Override
    public Page<Map<String, Object>> findAll(Pageable pgbl) {
        checkTable();
        if (pgbl == null) {
            throw new IllegalArgumentException("分页参数为空");
        }
        int current = pgbl.getPageNumber();
        int size = pgbl.getPageSize();
        if (current < 1) {
            return new PageImpl(new LinkedList());
        }

        List<Map<String, Object>> ret = this.jdbc.queryForList("select * from " + this.table + " limit ?,?", (current - 1) * size, size);
        return new PageImpl(ret);
    }
}

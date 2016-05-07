package com.jhhc.baseframework.data.repository;

import com.jhhc.baseframework.test.Base;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author yecq
 */
public class JhhcJdbcMapRepositoryTest extends Base {

    @Autowired
    private RepositoryFactory factory;

    @Autowired
    private JdbcTemplate jdbc;
    
    @Autowired
    private ApplicationContext context;

    @Test
    public void test_save() {
        this.expectedEx.expect(IllegalArgumentException.class);
        this.expectedEx.expectMessage("save的数据为空");
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.save(new HashMap());
    }

    @Test
    public void test_save1() {
        this.expectedEx.expect(IllegalArgumentException.class);
        this.expectedEx.expectMessage("save的数据为空");
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.save(new HashMap());
    }

    @Test
    public void test_save2() {
        CrudRepository<Map, String> repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map = new HashMap();
        map.put("name", "京巴");
        map.put("age", 2);
        map = repo.save(map);
        int count = this.jdbc.queryForList("select * from user").size();
        assertThat(count, is(5));
        assertThat(map.get("id"), nullValue());
    }

    @Test
    public void test_save3() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map = new HashMap();
        map.put("id", 1);
        map.put("name", "ABCD#");
        map.put("age", 200);
        repo.save(map);
        map = this.jdbc.queryForMap("select * from user where id=1");
        assertThat(map.get("id") + "", is("1"));
        assertThat(map.get("name") + "", is("ABCD#"));
        assertThat(Integer.parseInt(map.get("age") + ""), is(200));
    }

    @Test
    public void test_save_multi() {
        this.expectedEx.expect(IllegalArgumentException.class);
        this.expectedEx.expectMessage("save的数据为空");
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.save(new HashMap());
    }

    @Test
    public void test_save_multi1() {
        CrudRepository<Map, String> repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map1 = new HashMap();
        map1.put("name", "同学1");
        map1.put("age", 13);
        Map map2 = new HashMap();
        map2.put("name", "甲乙丙丁");
        map2.put("age", 23);
        Map map3 = new HashMap();
        map3.put("id", 2);
        map3.put("name", "ZXDF");
        map3.put("age", 1000);
        repo.save(asList(map1, map2, map3));

        int count = this.jdbc.queryForList("select * from user").size();
        assertThat(count, is(6));
        Map map = this.jdbc.queryForMap("select * from user where id=2");
        assertThat(map.get("id") + "", is("2"));
        assertThat(map.get("name") + "", is("ZXDF"));
        assertThat(Integer.parseInt(map.get("age") + ""), is(1000));
    }

    @Test
    public void test_findOne() {
        CrudRepository<Map, String> repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map = (Map) repo.findOne("3");
        assertThat(map, notNullValue());
        assertThat(map.get("id") + "", is("3"));
        assertThat(map.get("name") + "", is("qindeyu"));
        assertThat(Integer.parseInt(map.get("age") + ""), is(21));

        map = repo.findOne("14");  // 这里的设计是找不到就返回null
        assertThat(map, nullValue());
    }

    @Test
    public void test_exists() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        assertThat(repo.exists("2"), is(true));
        assertThat(repo.exists("32"), is(false));
    }

    @Test
    public void test_findAll() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Iterable<Map<String, Object>> data = repo.findAll();
        Iterator<Map<String, Object>> ite = data.iterator();
        int i = 0;
        while (ite.hasNext()) {
            i++;
            ite.next();
        }
        assertThat(i, is(4));
    }

    @Test
    public void test_findAll1() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("info");
        Iterable<Map<String, Object>> data = repo.findAll();
        assertThat(((List) data).size(), is(0));
    }

    @Test
    public void test_findAll2() {
        this.expectedEx.expect(IllegalArgumentException.class);
        this.expectedEx.expectMessage("find的列表参数为空");
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.findAll(null);
    }

    @Test
    public void test_findAll3() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Iterable<Map<String, Object>> tmp = repo.findAll(asList("2", "4"));
        List<Map<String, Object>> data = (List) tmp;
        assertThat(data.size(), is(2));
    }

    @Test
    public void test_findAll4() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Iterable<Map<String, Object>> tmp = repo.findAll(asList("11", "13"));
        assertThat(((List) tmp).size(), is(0));
    }

    @Test
    public void test_count() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        assertThat(repo.count(), is(4L));

        repo = this.factory.getMapPagingAndSortingRepository("info");
        assertThat(repo.count(), is(0L));
    }

    @Test
    public void test_delete() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.delete("3");
        List<Map<String, Object>> list = this.jdbc.queryForList("select * from user where id=3");
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_delete1() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map = new HashMap();
        map.put("name", "qindeyu");
        map.put("age", 21);
        repo.delete(map);
        List<Map<String, Object>> list = this.jdbc.queryForList("select * from user where name=? and age=?", "qindeyu", 21);
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_delete2() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map = new HashMap();
        map.put("name", "qindeyu");
        map.put("age", 23);
        repo.delete(map);
        List<Map<String, Object>> list = this.jdbc.queryForList("select * from user where name=? and age=?", "qindeyu", 21);
        assertThat(list.size(), is(1));
    }

    @Test
    public void test_delete3() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        Map map1 = new HashMap();
        map1.put("name", "qindeyu");
        map1.put("age", 21);

        Map map2 = new HashMap();
        map2.put("name", "abcd");
        map2.put("age", 11);
        repo.delete(asList(map1, map2));
        List<Map<String, Object>> list = this.jdbc.queryForList("select * from user where name=? and age=?", "qindeyu", 21);
        assertThat(list.size(), is(0));
        list = this.jdbc.queryForList("select * from user where name=? and age=?", "abcd", 11);
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_deleteAll() {
        CrudRepository repo = this.factory.getMapPagingAndSortingRepository("user");
        repo.deleteAll();
        List<Map<String, Object>> list = this.jdbc.queryForList("select * from user where true");
        assertThat(list.size(), is(0));
    }

    // 测试分页
    @Test
    public void test_p0_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(0, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List list = data.getContent();
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_p1_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(1, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List<Map<String, Object>> list = data.getContent();
        assertThat(list.size(), is(5));
        assertThat(list.get(0).get("id") + "", is("1"));
        assertThat(list.get(0).get("value") + "", is("item_1"));
        assertThat(list.get(1).get("id") + "", is("2"));
        assertThat(list.get(1).get("value") + "", is("item_2"));
        assertThat(list.get(2).get("id") + "", is("3"));
        assertThat(list.get(2).get("value") + "", is("item_3"));
        assertThat(list.get(3).get("id") + "", is("4"));
        assertThat(list.get(3).get("value") + "", is("item_4"));
        assertThat(list.get(4).get("id") + "", is("5"));
        assertThat(list.get(4).get("value") + "", is("item_5"));
    }

    @Test
    public void test_p2_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(2, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List<Map<String, Object>> list = data.getContent();
        assertThat(list.size(), is(5));
        assertThat(list.get(0).get("id") + "", is("6"));
        assertThat(list.get(0).get("value") + "", is("item_6"));
        assertThat(list.get(1).get("id") + "", is("7"));
        assertThat(list.get(1).get("value") + "", is("item_7"));
        assertThat(list.get(2).get("id") + "", is("8"));
        assertThat(list.get(2).get("value") + "", is("item_8"));
        assertThat(list.get(3).get("id") + "", is("9"));
        assertThat(list.get(3).get("value") + "", is("item_9"));
        assertThat(list.get(4).get("id") + "", is("10"));
        assertThat(list.get(4).get("value") + "", is("item_10"));
    }

    @Test
    public void test_p4_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(4, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List<Map<String, Object>> list = data.getContent();
        assertThat(list.size(), is(5));
        assertThat(list.get(0).get("id") + "", is("16"));
        assertThat(list.get(0).get("value") + "", is("item_16"));
        assertThat(list.get(1).get("id") + "", is("17"));
        assertThat(list.get(1).get("value") + "", is("item_17"));
        assertThat(list.get(2).get("id") + "", is("18"));
        assertThat(list.get(2).get("value") + "", is("item_18"));
        assertThat(list.get(3).get("id") + "", is("19"));
        assertThat(list.get(3).get("value") + "", is("item_19"));
        assertThat(list.get(4).get("id") + "", is("20"));
        assertThat(list.get(4).get("value") + "", is("item_20"));
    }

    @Test
    public void test_p5_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(5, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List<Map<String, Object>> list = data.getContent();
        assertThat(list.size(), is(2));
        assertThat(list.get(0).get("id") + "", is("21"));
        assertThat(list.get(0).get("value") + "", is("item_21"));
        assertThat(list.get(1).get("id") + "", is("22"));
        assertThat(list.get(1).get("value") + "", is("item_22"));
    }

    @Test
    public void test_p6_findAll() {
        PagingAndSortingRepository repo = this.factory.getMapPagingAndSortingRepository("pageobj");
        Pageable pg = new PageRequest(6, 5);
        Page<Map<String, Object>> data = repo.findAll(pg);
        List<Map<String, Object>> list = data.getContent();
        assertThat(list.size(), is(0));
    }
}

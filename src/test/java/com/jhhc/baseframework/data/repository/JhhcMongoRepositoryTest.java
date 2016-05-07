package com.jhhc.baseframework.data.repository;

import com.jhhc.baseframework.test.Base;
import java.sql.SQLException;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import org.dbunit.DatabaseUnitException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author yecq
 */
public class JhhcMongoRepositoryTest extends Base {

    @Autowired
    private RepositoryFactory fac;

    @Autowired
    private MongoTemplate mongo;

    @Override
    public void before() throws DatabaseUnitException, SQLException {
        // 清空数据库
//        System.out.println("清空数据库...");
        if (this.mongo.collectionExists("have")) {
            this.mongo.dropCollection("have");
        }
        if (this.mongo.collectionExists("empty")) {
            this.mongo.dropCollection("empty");
        }

        // 插入数据
//        System.out.println("插入测试数据...");
        this.mongo.createCollection("have");
        this.mongo.createCollection("empty");

        Have hv1 = new Have();
        hv1.setId("1");
        hv1.setName("叶小怜");
        hv1.setAge(23);
        Have hv2 = new Have();
        hv2.setId("2");
        hv2.setName("abcd");
        hv2.setAge(90);
        Have hv3 = new Have();
        hv3.setId("3");
        hv3.setName("user");
        hv3.setAge(23);
        this.mongo.insert(hv1);
        this.mongo.insert(hv2);
        this.mongo.save(hv3);
    }

    @Override
    public void after() throws DatabaseUnitException, SQLException {
        this.mongo.dropCollection("have");
        this.mongo.dropCollection("empty");
    }

    @Test
    public void test_save() {
        Have hv = new Have();
        hv.setName("asdf");
        hv.setAge(100);
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        repo.save(hv);
        hv = this.mongo.findOne(new Query(where("name").is("asdf")), Have.class);
        assertThat(hv.getId(), notNullValue());
        System.out.println("test_save新插入id: " + hv.getId());
    }

    @Test
    public void test_save1() {
        Have hv = new Have();
        hv.setName("asdf");
        hv.setAge(100);
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("empty");    // 用另外一个名字存
        repo.save(hv);  // 取了名字的，以名字为准
        this.fac.changeMongoCollection("empty");
        hv = this.mongo.findOne(new Query(where("name").is("asdf")), Have.class, "empty");
        assertThat(hv.getId(), notNullValue());
        System.out.println("test_save1新插入id: " + hv.getId());
    }

    @Test
    public void test_save2() {
        Have hv1 = new Have();
        hv1.setName("a1");
        hv1.setAge(11);
        Have hv2 = new Have();
        hv2.setName("a2");
        hv2.setAge(34);
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository(null);
        repo.save(asList(hv1, hv2));
        List ret = this.mongo.findAll(Have.class);
        assertThat(ret.size(), is(5));
    }

    @Test
    public void test_findOne() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        Map hv = (Map) repo.findOne("2"); // 默认的返回是Map
        assertThat(hv.get("_id") + "", is("2"));
        assertThat(hv.get("name") + "", is("abcd"));
        assertThat(hv.get("age") + "", is("90"));
    }

    @Test
    public void test_findOne1() {
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("collection为空");
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository(null);
        Map hv = (Map) repo.findOne("2"); // 默认的返回是Map
    }

    @Test
    public void test_findOne2() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have", Have.class);
        Have hv = (Have) repo.findOne("2");
        assertThat(hv.getId(), is("2"));
        assertThat(hv.getName(), is("abcd"));
        assertThat(hv.getAge(), is(90));
    }

    @Test
    public void test_findOne3() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        Have hv = (Have) repo.findOne("101");
        assertThat(hv, nullValue());
    }

    @Test
    public void test_findOne4() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have", Have.class);
        Have hv = (Have) repo.findOne("99");
        assertThat(hv, nullValue());
    }

    @Test
    public void test_exists() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        assertThat(repo.exists("3"), is(true));
        assertThat(repo.exists("33"), is(false));

        repo = this.fac.getMongoPagingAndSortingRepository("have", Have.class);
        assertThat(repo.exists("3"), is(true));
        assertThat(repo.exists("33"), is(false));
    }

    @Test
    public void test_findAll() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        List list = (List) repo.findAll();
        assertThat(list.size(), is(3));

        repo = this.fac.getMongoPagingAndSortingRepository("empty");
        list = (List) repo.findAll();
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_findAll1() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        List list = (List) repo.findAll(asList("2", "3", "16"));
        assertThat(list.size(), is(2));

        repo = this.fac.getMongoPagingAndSortingRepository("empty");
        list = (List) repo.findAll(asList("11", "12"));
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_count() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        assertThat(repo.count(), is(3L));

        repo = this.fac.getMongoPagingAndSortingRepository("empty");
        assertThat(repo.count(), is(0L));
    }

    @Test
    public void test_delete() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        repo.delete("1");
        assertThat(repo.count(), is(2L));
    }

    @Test
    public void test_delete1() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        repo.delete("11");
        assertThat(repo.count(), is(3L));
    }

    @Test
    public void test_delete2() {
        Have hv = new Have();
        hv.setId("2");  // 必须加上id才会删
        hv.setName("abcd1");  // 加上这些也没用
        hv.setAge(92);
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        repo.delete(hv);
        assertThat(repo.count(), is(2L));
        assertThat(this.mongo.findById("2", Have.class), nullValue());
    }

    @Test
    public void test_delete3() {
        Have hv = new Have();
        hv.setId("2");  // 必须加上id才会删
        hv.setName("abcd1");  // 加上这些也没用
        hv.setAge(92);
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository(null);
        repo.delete(hv);
        repo = this.fac.getMongoPagingAndSortingRepository("have");
        assertThat(repo.count(), is(2L));
        assertThat(this.mongo.findById("2", Have.class), nullValue());
    }

    @Test
    public void test_delete_multi() {
        Have hv = new Have();
        hv.setId("2");  // 必须加上id才会删
        hv.setName("abcd1");  // 加上这些也没用
        hv.setAge(92);
        Have hv1 = new Have();
        hv1.setId("1");  // 必须加上id才会删
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository();
        repo.delete(asList(hv, hv1));
        repo = this.fac.getMongoPagingAndSortingRepository("have");
        assertThat(repo.count(), is(1L));
    }

    @Test
    public void test_deleteAll() {
        CrudRepository repo = this.fac.getMongoPagingAndSortingRepository("have");
        repo.deleteAll();
        assertThat(repo.count(), is(0L));
    }
}

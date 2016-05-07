package com.jhhc.baseframework.data.repository;

import com.jhhc.baseframework.test.Base;
import java.sql.SQLException;
import org.dbunit.DatabaseUnitException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.CrudRepository;

/**
 * 测试Repository的Prototype属性
 *
 * @author yecq
 */
public class PrototypeTest extends Base {

    @Autowired
    private ApplicationContext context;

    @Test
    public void test_context() {
        assertThat(this.context, notNullValue());
    }

    @Test
    public void test_jdbc_map_prototype() {
        CrudRepository repo = this.context.getBean(JhhcJdbcMapRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        repo.count();
    }

    @Test
    public void test1_jdbc_map_prototype() {
        CrudRepository repo = this.context.getBean(JhhcJdbcMapRepository.class);
        ((JhhcJdbcMapRepository) repo).setTable("user");
        assertThat(repo.count(), is(4L));

        repo = this.context.getBean(JhhcJdbcMapRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        assertThat(repo.count(), is(4L));
    }

    @Test
    public void test_jdbc_object_prototype() {
        CrudRepository repo = this.context.getBean(JhhcJdbcRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        repo.count();
    }

    @Test
    public void test1_jdbc_object_prototype() {
        CrudRepository repo = this.context.getBean(JhhcJdbcRepository.class);
        ((JhhcJdbcRepository) repo).setTable("user");
        assertThat(repo.count(), is(4L));

        repo = this.context.getBean(JhhcJdbcRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        assertThat(repo.count(), is(4L));
    }

    @Test
    public void test_mongo_prototype() {
        CrudRepository repo = this.context.getBean(JhhcMongoRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("collection为空");
        assertThat(repo.count(), is(3L));
    }

    @Test
    public void test1_mongo_prototype() {
        CrudRepository repo = this.context.getBean(JhhcMongoRepository.class);
        ((JhhcMongoRepository) repo).setCollection("have");
        assertThat(repo.count(), is(3L));

        repo = this.context.getBean(JhhcMongoRepository.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("collection为空");
        assertThat(repo.count(), is(3L));
    }

    @Autowired
    private MongoTemplate mongo;

    @Override
    public void before() throws DatabaseUnitException, SQLException {
        super.before();
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
        super.after();

        this.mongo.dropCollection("have");
        this.mongo.dropCollection("empty");
    }
}

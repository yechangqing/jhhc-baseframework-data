package com.jhhc.baseframework.data.repository;

import com.jhhc.baseframework.test.Base;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.repository.CrudRepository;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author yecq
 */
public class JhhcJdbcCrudRepositoryTest extends Base {

    @Autowired
    private CrudRepositoryFactory factory;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    public void test_object2Map() {
        CrudRepository repo = this.factory.getRepository();
        User user = new User();
        user.setName("abcd");
        user.setAge(23);
        try {
            Method con = repo.getClass().getDeclaredMethod("object2Map", Object.class);
            con.setAccessible(true);
            Map map = (Map) con.invoke(repo, user);
            assertThat(map.size(), is(2));
            assertThat(map.get("name") + "", is("abcd"));
            assertThat(Double.parseDouble(map.get("age") + ""), closeTo(23, 0));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JhhcJdbcCrudRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test_save() {
        // 用默认名称
        CrudRepository repo = this.factory.getRepository();
        User user = new User("QWERT", 40);
        repo.save(user);
        List list = this.jdbc.queryForList("select * from user where name=? and age=?", "QWERT", 40);
        assertThat(list.size(), is(1));
    }

    @Test
    public void test_save1() {
        // 用指定名称
        CrudRepository repo = this.factory.getRepository("user");
        Have user = new Have("QWERT", 40);
        repo.save(user);
        List list = this.jdbc.queryForList("select * from user where name=? and age=?", "QWERT", 40);
        assertThat(list.size(), is(1));
    }

    @Test
    public void test_save2() {
        // 用默认名称
        CrudRepository repo = this.factory.getRepository();
        User user1 = new User("QWERT", 40);
        User user2 = new User("poiuy", 31);
        repo.save(asList(user1, user2));
        List list = this.jdbc.queryForList("select * from user where name=? and age=?", "QWERT", 40);
        assertThat(list.size(), is(1));
        list = this.jdbc.queryForList("select * from user where name=? and age=?", "poiuy", 31);
        assertThat(list.size(), is(1));
    }

    @Test
    public void test_save3() {
        //用指定名字
        CrudRepository repo = this.factory.getRepository("user");
        Have have1 = new Have("QWERT", 40);
        Have have2 = new Have("poiuy", 31);
        repo.save(asList(have1, have2));
        List list = this.jdbc.queryForList("select * from user where name=? and age=?", "QWERT", 40);
        assertThat(list.size(), is(1));
        list = this.jdbc.queryForList("select * from user where name=? and age=?", "poiuy", 31);
        assertThat(list.size(), is(1));
    }

    @Test
    public void test_findOne() {
        CrudRepository repo = this.factory.getRepository("user", User.class);
        User user = (User) repo.findOne("2");
        assertThat(user, notNullValue());
        assertThat(user.getId(), is("2"));
        assertThat(user.getName(), is("qwer"));
        assertThat(user.getAge(), is(30));
    }

    @Test
    public void test_findOne1() {
        CrudRepository repo = this.factory.getRepository("user", User.class);
        User user = (User) repo.findOne("13");
        assertThat(user, nullValue());
    }

    @Test
    public void test_findOne2() {
        CrudRepository repo = this.factory.getRepository("user");
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("Class为空");
        repo.findOne("1");
    }

    @Test
    public void test_findOne3() {
        CrudRepository repo = this.factory.getRepository(User.class);
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        repo.findOne("1");
    }

    @Test
    public void test_exists() {
        CrudRepository repo = this.factory.getRepository("user", User.class);
        assertThat(repo.exists("2"), is(true));
        assertThat(repo.exists("23"), is(false));
    }

    @Test
    public void test_findAll() {
        CrudRepository repo = this.factory.getRepository("user", User.class);
        List<User> ret = (List) repo.findAll();
        assertThat(ret.size(), is(4));
        assertThat(ret.get(0).getName(), is("abcd"));
        assertThat(ret.get(1).getName(), is("qwer"));
        assertThat(ret.get(2).getName(), is("qindeyu"));
        assertThat(ret.get(3).getName(), is("sunwenqin"));
    }

    @Test
    public void test_findAll1() {
        this.jdbc.update("delete from user where id>0");
        CrudRepository repo = this.factory.getRepository("user", User.class);
        List<User> ret = (List) repo.findAll();
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_findAll2() {
        CrudRepository repo = this.factory.getRepository();
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        repo.findAll();
    }

    @Test
    public void test_findAll3() {
        CrudRepository repo = this.factory.getRepository("user");
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("Class为空");
        repo.findAll();
    }

    @Test
    public void test_findAll4() {
        CrudRepository repo = this.factory.getRepository("user", User.class);
        List<User> list = (List) repo.findAll(asList("2", "4"));
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getName(), is("qwer"));
        assertThat(list.get(1).getName(), is("sunwenqin"));
    }

    @Test
    public void test_findAll5() {
        this.jdbc.update("delete from user where id>0");
        CrudRepository repo = this.factory.getRepository("user", User.class);
        List<User> ret = (List) repo.findAll(asList("2", "4"));
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_count() {
        CrudRepository repo = this.factory.getRepository("user");
        assertThat(repo.count(), is(4L));
    }

    @Test
    public void test_count1() {
        this.jdbc.update("delete from user where id>0");
        CrudRepository repo = this.factory.getRepository("user");
        assertThat(repo.count(), is(0L));
    }

    @Test
    public void test_delete() {
        CrudRepository repo = this.factory.getRepository();
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        repo.delete("2");
    }

    @Test
    public void test_delete1() {
        CrudRepository repo = this.factory.getRepository("user");
        this.expectedEx.expect(IllegalArgumentException.class);
        this.expectedEx.expectMessage("delete参数为空");
        repo.delete("");
    }

    @Test
    public void test_delete2() {
        CrudRepository repo = this.factory.getRepository("user");
        repo.delete("2");
        List list = this.jdbc.queryForList("select * from user where id=2");
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_delete3() {
        CrudRepository repo = this.factory.getRepository("user");
        repo.delete("23");
        List list = this.jdbc.queryForList("select * from user where id>0");
        assertThat(list.size(), is(4));
    }

    @Test
    public void test_delete4() {
        // 使用默认名称
        CrudRepository repo = this.factory.getRepository();
        List ret = this.jdbc.queryForList("select * from user where name=? and age=?", "qwer", 30);
        assertThat(ret.size(), is(1));
        User user = new User("qwer", 30);
        repo.delete(user);
        ret = this.jdbc.queryForList("select * from user where name=? and age=?", "qwer", 30);
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_delete5() {
        // 使用指定名称
        CrudRepository repo = this.factory.getRepository("user");
        List ret = this.jdbc.queryForList("select * from user where name=? and age=?", "qwer", 30);
        assertThat(ret.size(), is(1));
        Have have = new Have("qwer", 30);
        repo.delete(have);
        ret = this.jdbc.queryForList("select * from user where name=? and age=?", "qwer", 30);
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_delete6() {
        List ret = this.jdbc.queryForList("select * from user where name=? or name=?", "qwer", "sunwenqin");
        assertThat(ret.size(), is(2));
        CrudRepository repo = this.factory.getRepository();
        User user1 = new User("qwer", 30);
        User user2 = new User("sunwenqin", 20);
        repo.delete(asList(user1, user2));
        ret = this.jdbc.queryForList("select * from user where name=? or name=?", "qwer", "sunwenqin");
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_delete7() {
        List ret = this.jdbc.queryForList("select * from user where name=? or name=?", "qwer", "sunwenqin");
        assertThat(ret.size(), is(2));
        CrudRepository repo = this.factory.getRepository("user");
        User user1 = new User("qwer", 30);
        Have user2 = new Have("sunwenqin", 20);
        repo.delete(asList(user1, user2));
        ret = this.jdbc.queryForList("select * from user where name=? or name=?", "qwer", "sunwenqin");
        assertThat(ret.size(), is(0));
    }

    @Test
    public void test_delete8() {
        List ret = this.jdbc.queryForList("select * from user where name=? or name=?", "qwer", "sunwenqin");
        assertThat(ret.size(), is(2));
        CrudRepository repo = this.factory.getRepository();
        User user1 = new User("qwer", 30);
        Have user2 = new Have("sunwenqin", 20);
        this.expectedEx.expect(DataAccessException.class);
        repo.delete(asList(user1, user2));
    }

    @Test
    public void test_deleteAll() {
        List list = this.jdbc.queryForList("select * from user where id>0");
        assertThat(list.size(), is(4));
        CrudRepository repo = this.factory.getRepository("user");
        repo.deleteAll();
        list = this.jdbc.queryForList("select * from user where id>0");
        assertThat(list.size(), is(0));
    }

    @Test
    public void test_deleteAll1() {
        this.expectedEx.expect(IllegalStateException.class);
        this.expectedEx.expectMessage("表名称为空");
        CrudRepository repo = this.factory.getRepository();
        repo.deleteAll();
    }
}

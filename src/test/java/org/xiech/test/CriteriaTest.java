package org.xiech.test;

import org.junit.Test;
import org.xiech.mybatis.boost.core.Criteria;
import org.xiech.mybatis.boost.core.LambdaCriteria;

/**
 * @author xiech
 * @date 2020-07-17 17:14
 */
public class CriteriaTest {

    @Test
    public void testAndOrFunc() {
        Criteria sql = new Criteria<User>(User.class).select("id").eq("id", 1).eq("id", 2).eq("id", 3);
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).and().eq("id", 3).eq("id", 4);
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).or().eq("id", 3).eq("id", 4);
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).and();
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).or();
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).and().or().eq("id", 3);
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).and().eq("id", 3).eq("id", 4).or();
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql.select("id").eq("id", 1).eq("id", 2).and().eq("id", 3).eq("id", 4).or().eq("id", 5).eq("id", 6).or().eq("id", 7).eq("id", 8).and().eq("id", 9).eq("id", 10);
        System.out.println(sql);
    }

    @Test
    public void testSelectAndFromFunc() {
        Criteria sql = new Criteria<User>(User.class).select("id").eq("id", 1).and(i -> i.eq("id", 2).or().eq("id", 3).eq("id", 4)).eq("id", 5);
        System.out.println(sql);


        sql.clear();
        System.out.println("");
        sql = new Criteria<User>(User.class).from(User.class, null).from(User.class, null).select("t0.id").eq("id", 1).or(i -> i.eq("id", 2).or().eq("id", 3).eq("id", 4)).eq("id", 5);
        System.out.println(sql);

    }

    public static void main(String[] args) {
        Criteria sql = new Criteria<User>(User.class).select("id").eq("id", 1).and(i -> i.eq("id", 2).or().eq("id", 3).eq("id", 4)).eq("id", 5);
        System.out.println(sql);

        sql.clear();
        System.out.println("");
        sql = new Criteria<User>(User.class).from(User.class, null).from(User.class, null).select("t0.id").eq("id", 1).or(i -> i.eq("id", 2).or().eq("id", 3).eq("id", 4)).eq("id", 5);
        System.out.println(sql);

        User u = new User();
        LambdaCriteria<User> s = new LambdaCriteria(User.class);
        s.select(User::getId).select(User::getName);
        System.out.println(s);

    }
}

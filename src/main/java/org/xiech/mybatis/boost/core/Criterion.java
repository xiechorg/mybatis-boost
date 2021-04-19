package org.xiech.mybatis.boost.core;

import org.apache.ibatis.jdbc.SQL;

/**
 * 标准接口
 *
 * @param <T> 实体类的class
 * @param <R> 表列名（字符串），也可以传实体类的属性名（字符串），或者是一个匹配实体类的getter方法的Lambda函数（类似这种：User::getId）。
 * @param <S> 链式调用，限定支持链式调用的方法要返回的类型（子类类型）
 * @author xiech
 * @date 2020-07-18 0:46
 */
public interface Criterion<T, R, S extends Criterion> {

    /**
     * 返回最终的sql，直接sql.toString()即可获取sql字符串
     *
     * @return
     */
    SQL getSql();

    /**
     * 返回链式调用的子类型
     *
     * @return
     */
    S getSelf();

    /**
     * 清除已拼接的sql，返回一个新的对象
     *
     * @return
     */
    S clear();
}

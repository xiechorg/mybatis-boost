package org.xiech.mybatis.boost.annotation;

/**
 * 自定义生成主键值
 *
 * @author xiech
 * @date 2020-07-15 18:17
 */
public interface IdGenerate<T> {
    /**
     * 生成主键值
     *
     * @param table 表名，用于区分
     * @return
     */
    T generator(String table);
}

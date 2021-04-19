package org.xiech.mybatis.boost.annotation;

import org.apache.ibatis.type.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;


/**
 * 字段的映射
 *
 * @author xiech
 * @date 2020-07-15 13:44:25
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 列名
     *
     * @return
     */
    String name() default "";

    /**
     * 外键关联的目标对象class，默认为null
     *
     * @return
     */
    Class<? extends Object> foreignBeanClass();

    /**
     * 外键关联的目标对象的属性，默认为id
     *
     * @return
     */
    String foreignBeanFieldName() default "id";

    /**
     * 数据长度，表示该字段的有效位数。
     *
     * @return
     */
    int precision() default 0;

    /**
     * 小数长度，表示该字段的小数位数。
     *
     * @return
     */
    int scale() default 0;

    /**
     * 是否带有符号
     *
     * @return
     */
    boolean signed() default true;

    /**
     * 是否唯一性
     *
     * @return
     */
    boolean unique() default false;

    /**
     * 是否可为null
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * 是否可插入
     *
     * @return
     */
    boolean insertable() default true;

    /**
     * 是否可更新
     *
     * @return
     */
    boolean updatable() default true;

    /**
     * 是否可搜索
     *
     * @return
     */
    boolean searchable() default true;

    /**
     * 是否Clob字段
     *
     * @return
     */
    boolean isClob() default false;

    /**
     * 是否Blob字段
     *
     * @return
     */
    boolean isBlob() default false;

    /**
     * 映射到数据库的jdbc类型
     *
     * @return
     */
    JDBCType jdbcType();

    /**
     * mybatis的TypeHandler类型映射
     *
     * @return
     */
    Class<? extends TypeHandler<?>> typeHandler();
}

package org.xiech.mybatis.boost.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库主键的映射
 *
 * @author xiech
 * @date 2020-07-15 13:44:12
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    /**
     * 主键值的生成方式
     *
     * @return
     */
    GenerationType generationType() default GenerationType.AUTO;

    /**
     * 生成器
     * 1、GenerationType.AUTO：不必传
     * 2、GenerationType.SEQUENCE：必传，指定具体的序列名
     * 3、GenerationType.UUID：不必传
     * 4、GenerationType.METHOD：不必传，但必须实现IdGenerate接口。
     *
     * @return
     */
    String generator() default "";
}

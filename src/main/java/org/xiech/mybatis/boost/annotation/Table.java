package org.xiech.mybatis.boost.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 表的映射
 *
 * @author xiech
 * @date 2020-07-15 13:46:35
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * 表名
     *
     * @return
     */
    String name() default "";

}

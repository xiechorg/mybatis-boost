package org.xiech.mybatis.boost.annotation;

public enum GenerationType {
    /**
     * 自动递增
     */
    AUTO,
    /**
     * 序列
     */
    SEQUENCE,
    /**
     * UUID生成
     */
    UUID,
    /**
     * 自定义方法生成
     */
    METHOD
}

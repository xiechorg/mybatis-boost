package org.xiech.mybatis.boost.util.lambda.explain;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function
 *
 * @author xiech
 * @date 2020-07-15 01:20:36
 **/
@FunctionalInterface
public interface SerializeFunction<T, R> extends Function<T, R>, Serializable {

}
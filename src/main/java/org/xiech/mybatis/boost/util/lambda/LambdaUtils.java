package org.xiech.mybatis.boost.util.lambda;


import org.xiech.mybatis.boost.util.lambda.explain.ExplainFieldNameResult;
import org.xiech.mybatis.boost.util.lambda.explain.SerializeFunction;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Lambda表达式的工具类
 *
 * @author xiech
 * @date 2020-07-15 00:56:25
 **/
public class LambdaUtils {
    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");
    private static final String WRITE_REPLACE = "writeReplace";

    /**
     * SerializeFunction解析属性名的缓存，使用弱引用对象存储
     */
    private static final Map<String, WeakReference<ExplainFieldNameResult>> FIELD_NAME_CACHE = new ConcurrentHashMap<>();

    /**
     * 通过Bean::getField()这种无参有返回值的Lambda表达式来获取JavaBean的属性名，获取失败时返回null。
     *
     * @param function 无参有返回值的Lambda表达式，类似Bean::getField()
     * @return
     */
    public static <T, R> String explainFieldName(SerializeFunction<T, R> function) {
        ExplainFieldNameResult result = explainFieldNameResult(function);
        //return ExpressUtils.nvll(result, ExplainFieldNameResult::getFieldName);
        return result == null ? null : result.getFieldName();
    }

    /**
     * 通过Bean::getField()这种无参有返回值的Lambda表达式来获取JavaBean的属性名，获取失败时返回null。
     *
     * @param function 无参有返回值的Lambda表达式，类似Bean::getField()
     * @return
     */
    public static <T, R> ExplainFieldNameResult explainFieldNameResult(SerializeFunction<T, R> function) {
        Class<? extends SerializeFunction> clazz = function.getClass();
        String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(FIELD_NAME_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    ExplainFieldNameResult result = null;
                    try {
                        Method method = clazz.getDeclaredMethod(WRITE_REPLACE);
                        method.setAccessible(true);
                        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
                        String getter = serializedLambda.getImplMethodName();
                        if (GET_PATTERN.matcher(getter).matches()) {
                            getter = getter.substring(3);
                        } else if (IS_PATTERN.matcher(getter).matches()) {
                            getter = getter.substring(2);
                        }

                        int start = 2;
                        int end = serializedLambda.getInstantiatedMethodType().indexOf(";)");
                        String beanClassName = serializedLambda.getInstantiatedMethodType().substring(start, end).replaceAll("/", ".");

                        String fieldName = Introspector.decapitalize(getter);

                        result = new ExplainFieldNameResult(fieldName, beanClassName);
                        FIELD_NAME_CACHE.put(canonicalName, new WeakReference<ExplainFieldNameResult>(result));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
                });
    }

}
package org.xiech.mybatis.boost.util.string;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiech
 * @date 2020-07-16 1:01
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {


    /**
     * 高效格式化文本
     *
     * @param template
     * @param findRegex         获取要动态查找的正则表达式："\\{(.+?)\\}"、"\\{(\\d+?)\\}"、"\\{\\}"
     * @param getReplaceStrFunc 获取要替换的字符串，参数：
     *                          1、表示匹配的分组索引
     *                          2、表示匹配的分组字符串
     *                          3、表示要替换的字符串（如果返回null则不替换查找到的内容，如果方法抛出异常则不会继续往下去查找替换）
     * @return
     */
    public static String formatSuper(String template, String findRegex,
                                     BiFunction<Integer, String, String> getReplaceStrFunc) {
        try {
            StringBuilder newStr = new StringBuilder();
            char[] chars = template.toCharArray();
            Pattern pattern = Pattern.compile(findRegex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(template);
            int begin = 0, end = 0, findGroupIndex = 0;
            //将所有匹配的结果打印输出
            while (end < chars.length) {
                if (matcher.find()) {
                    try {
                        String value = matcher.group();

                        begin = end;
                        end = matcher.start();
                        newStr.append(chars, begin, end - begin);

                        String newValue = getReplaceStrFunc.apply(findGroupIndex++, value);
                        newStr.append(newValue != null ? newValue : value);

                        end = matcher.end();
                        continue;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
                newStr.append(chars, end, chars.length - end);
                end = chars.length;
            }
            return newStr.toString();
        } catch (Exception e) {
            e.printStackTrace();


        }
        return template;
    }

    /**
     * 有序的格式化文本，使用{number}做为占位符<br>
     * 例：<br>
     * 通常使用：format("this is {0} for {1}", "a", "b") =》 this is a for b<br>
     *
     * @param template 文本格式
     * @param params   参数
     * @return 格式化后的文本
     */
    public static String formatIndexed(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (params.length == 0 || isBlank(template)) {
            return template.toString();
        }
        return StringUtils.formatSuper(template.toString(), RegexUtils.REGEX_FORMAL_PARAM_NUMBER, (findGroupIndex, find) -> {
            int paramsIndex = Integer.valueOf(find.substring(1, find.length() - 1));
            return params[paramsIndex].toString();
        });
    }

}

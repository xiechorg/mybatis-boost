package org.xiech.mybatis.boost.util.lambda.explain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xiech
 * @date 2020-07-15 12:25
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExplainFieldNameResult {
    private String fieldName;
    private String beanClassName;
}

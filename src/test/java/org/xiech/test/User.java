package org.xiech.test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author xiech
 * @date 2020-07-15 1:17
 */
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class User extends BaseUser {
    private int id;
    private String name;
}

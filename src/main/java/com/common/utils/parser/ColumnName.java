package com.common.utils.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段对应的列名
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 01/03/2018
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnName {

    /**
     * 当前列对应的转换后列名
     * 
     * @return
     */
    String name();
}

package com.personal.excel.parser;

/**
 * @author zhuangqianliao
 */
public interface Parser<T> {
    T parse(String value);
    String deParse(Object value);
}

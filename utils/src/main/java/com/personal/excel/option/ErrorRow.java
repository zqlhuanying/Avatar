package com.personal.excel.option;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhuangqianliao
 * 原始错误行记录
 */
public class ErrorRow implements Iterable<Map.Entry<Integer, String>> {

    private Map<Integer, String> errorCell = new HashMap<>();

    public ErrorRow addCell(int cellIndex, String cellValue) {
        errorCell.put(cellIndex, cellValue);
        return this;
    }

    @Override
    public Iterator<Map.Entry<Integer, String>> iterator() {
        return errorCell.entrySet().iterator();
    }
}

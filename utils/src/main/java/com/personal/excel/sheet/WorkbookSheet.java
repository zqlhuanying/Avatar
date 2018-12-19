package com.personal.excel.sheet;

import com.personal.excel.option.PoiOptions;

/**
 * @author zhuangqianliao
 */
public interface WorkbookSheet<T> {

    Source<?> getSource();

    int getRows();

    PoiOptions getOptions();
}

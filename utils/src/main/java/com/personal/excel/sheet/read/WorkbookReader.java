package com.personal.excel.sheet.read;

import com.personal.excel.option.PoiOptions;
import com.personal.excel.sheet.Source;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface WorkbookReader<T> {

    /**
     * DataSource
     */
    Source<?> getSource();

    PoiOptions getOptions();

    int getRows();

    /**
     * Read from DataSource to the given type
     * @param type the given type
     * @return the list of the given type instance
     */
    List<T> read(Class<T> type);
}

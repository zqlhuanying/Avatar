package com.personal.excel.sheet;

import com.personal.excel.enums.PoiExcelType;

/**
 * @author qianliao.zhuang
 * Data Source
 * eg. PoiFile or PoiInputStream
 */
public interface Source<T> {

    /**
     * Get Data Source
     */
    T get();

    /**
     * source type
     * @return PoiExcelType
     */
    PoiExcelType type();
}

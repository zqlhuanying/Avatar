package com.personal.excel.sheet.read;

import com.personal.excel.enums.PoiExcelType;
import com.personal.excel.option.PoiOptions;
import com.personal.excel.sheet.PoiInputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookStreamReader<T> extends AbstractWorkbookReader<T> {

    private final PoiInputStream<InputStream> inputStream;

    public WorkbookStreamReader(InputStream inputStream, PoiExcelType excelType) {
        this(inputStream, excelType, PoiOptions.settings().build());
    }

    public WorkbookStreamReader(InputStream inputStream, PoiExcelType excelType, PoiOptions options) {
        this.inputStream = new PoiInputStream<>(inputStream, excelType);
        this.readSheet = new WorkbookReadSheet<>(this.inputStream, options);
    }
}

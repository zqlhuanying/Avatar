package com.personal.excel.sheet.write;

import com.personal.excel.option.PoiOptions;
import com.personal.excel.sheet.Source;
import com.personal.excel.sheet.WorkbookHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author qianliao.zhuang
 */
public class WorkbookSXSSFWriteSheet<T> extends WorkbookWriteSheet<T> {

    private static final int DEFAULT_WINDOW_SIZE = 100;

    private int rowAccessWindowSize = DEFAULT_WINDOW_SIZE;

    public WorkbookSXSSFWriteSheet(Source<?> source, PoiOptions options) {
        super(source, options);
    }

    public int getRowAccessWindowSize() {
        return rowAccessWindowSize;
    }

    public void setRowAccessWindowSize(int rowAccessWindowSize) {
        this.rowAccessWindowSize = rowAccessWindowSize;
    }

    @Override
    public Workbook getWorkbook() {
        if (this.workbook == null) {
            synchronized (this) {
                if (this.workbook == null) {
                    this.workbook = new SXSSFWorkbook(
                            (XSSFWorkbook) WorkbookHelper.createWorkbook(this.source.type()),
                            getRowAccessWindowSize()
                    );
                }
            }
        }
        return this.workbook;
    }
}

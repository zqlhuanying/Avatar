package com.personal.excel.sheet.write;

import com.personal.excel.option.PoiOptions;
import com.personal.excel.sheet.Source;
import com.personal.excel.storage.StorageService;

import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface WorkbookWriter<T, R> {

    Source<?> getSource();

    PoiOptions getOptions();

    R write(final List<T> values, final Class<T> clazz);

    OutputStream getOutputStream();

    StorageService getStorage();

    R save(OutputStream outputStream);
}

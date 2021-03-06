package com.personal.excel.sheet.read;

import com.personal.excel.enums.PoiExcelType;
import com.personal.excel.exception.PoiExcelTypeException;
import com.personal.excel.option.ErrorRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookEventReader<T> extends FilterWorkbookReader<T>
        implements ForkJoin<T, ErrorRow> {

    private WorkbookEventSheet<T> eventSheet;

    public WorkbookEventReader(WorkbookReader<T> reader) {
        super(reader);

        if (PoiExcelType.XLSX != getReader().getSource().type()) {
            throw new PoiExcelTypeException("Event Reader can just be supported .xlsx");
        }

        this.eventSheet = new WorkbookEventSheet<>(getSource(), getOptions());
    }

    @Override
    public List<T> read(Class<T> type) {
        try (OPCPackage pkg = this.eventSheet.getOPCPackage()){
            return this.eventSheet.read(type);
        } catch (IOException e) {
            log.error("can not auto-close OPCPackage", e);
        } catch (Exception e) {
            log.error("read file failed", e);
        }
        return Collections.emptyList();
    }

    public WorkbookBigReader<T, Object> bigReader() {
        return new WorkbookBigReader<>(this);
    }

    @Override
    public List<T> read(int start, int end, Class<T> type) {
        return this.eventSheet.read(start, end, type);
    }

    @Override
    public List<ErrorRow> errors(int start, int end) {
        return this.eventSheet.errors(start, end);
    }

    @Override
    public void release() throws IOException {
        this.eventSheet.getOPCPackage().close();
    }

    @Override
    public int getRows() {
        return this.eventSheet.getRows();
    }
}

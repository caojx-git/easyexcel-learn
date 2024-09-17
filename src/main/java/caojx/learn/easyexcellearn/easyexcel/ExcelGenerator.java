package caojx.learn.easyexcellearn.easyexcel;

import caojx.learn.easyexcellearn.dto.PageQuery;
import caojx.learn.easyexcellearn.dto.PageResult;
import caojx.learn.easyexcellearn.exception.ServiceException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.DataFormatData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * synchronized 保证多线程写数据线程安全
 */
@Slf4j
public class ExcelGenerator implements AutoCloseable {

    private final ExcelWriter excelWriter;
    private final Map<Integer, WriteSheet> sheets;

    public ExcelGenerator(String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = UriUtils.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=utf-8''" + encodedFileName);
        this.excelWriter = EasyExcel.write(response.getOutputStream()).autoCloseStream(false).build();
        this.sheets = new HashMap<>();
    }

    public ExcelGenerator(String fileName, File templateFile, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = UriUtils.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=utf-8''" + encodedFileName);
        this.excelWriter = EasyExcel.write(response.getOutputStream()).needHead(false).withTemplate(templateFile).autoCloseStream(false).build();
        this.sheets = new HashMap<>();
    }

    public ExcelGenerator(String fileName, InputStream templateFileInputStream, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = UriUtils.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=utf-8''" + encodedFileName);
        this.excelWriter = EasyExcel.write(response.getOutputStream()).needHead(false).withTemplate(templateFileInputStream).autoCloseStream(false).build();
        this.sheets = new HashMap<>();
    }

    public synchronized ExcelGenerator generateSheet(String sheetName, List<List<String>> head, Map<Integer, DataFormatData> formatInfos) {
        WriteHandler writeHandler = new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                if (BooleanUtils.isTrue(context.getHead())) {
                    return;
                }
                WriteCellData<?> cellData = context.getFirstCellData();
                WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                Optional.ofNullable(formatInfos.get(cellData.getColumnIndex())).ifPresent(writeCellStyle::setDataFormatData);
            }
        };
        WriteSheet writeSheet = EasyExcel.writerSheet(sheets.size(), sheetName)
                .head(head).registerWriteHandler(writeHandler)
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(20)).build();
        sheets.put(sheets.size(), writeSheet);
        //先真正写入sheet,避免无数据,没有写sheet文件打不开
        excelWriter.write(Collections.emptyList(), writeSheet);
        return this;
    }

    public synchronized ExcelGenerator generateSheet(String sheetName, Class<?> modelClass) {
        WriteSheet writeSheet = EasyExcel.writerSheet(sheets.size(), sheetName).head(modelClass).build();
        sheets.put(sheets.size(), writeSheet);
        //先真正写入sheet,避免无数据,没有写sheet文件打不开
        excelWriter.write(Collections.emptyList(), writeSheet);
        return this;
    }

    public synchronized ExcelGenerator generateSheet(int sheetIdx, String sheetName, Class<?> modelClass) {
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetIdx, sheetName).head(modelClass).build();
        sheets.put(sheetIdx, writeSheet);
        //先真正写入sheet,避免无数据,没有写sheet文件打不开
        excelWriter.write(Collections.emptyList(), writeSheet);
        return this;
    }

    public synchronized ExcelGenerator generateData(int sheetIdx, List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return this;
        }
        WriteSheet writeSheet = sheets.get(sheetIdx);
        if (writeSheet == null) {
            throw new ServiceException("sheet未创建");
        }
        excelWriter.write(dataList, writeSheet);
        return this;
    }

    /**
     * 分页生成数据
     */
    public <T extends PageQuery> void pageGenerateData(int sheetIdx, T pageQueryParam, Function<T, PageResult<?>> function) {
        List<?> dataList;
        int pageNo = 1;
        do {
            pageQueryParam.setPageNum(pageNo);
            PageResult<?> pageResult = function.apply(pageQueryParam);

            dataList = pageResult.getRecords();
            pageNo++;
            if (pageNo > 20000) {
                log.warn("pageGenerateData 翻页过大，不在翻页查询");
                break;
            }
            this.generateData(sheetIdx, dataList);
        } while (CollectionUtils.isNotEmpty(dataList));
    }

    @Override
    public void close() throws Exception {
        excelWriter.close();
    }

}

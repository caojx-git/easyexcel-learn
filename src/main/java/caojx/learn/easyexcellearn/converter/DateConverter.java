package caojx.learn.easyexcellearn.converter;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Date;

/**
 * DateConverter
 *
 * @author trg
 * @className DateConverter
 * @date 2024/1/25 16:09
 **/
public class DateConverter implements Converter<Date> {

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String dateString = cellData.getStringValue();
        return dateString == null ? null : DateUtil.parse(dateString, "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public CellData<Date> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String dateValue = DateUtil.format(value, "yyyy-MM-dd HH:mm:ss");
        return new CellData<>(dateValue);
    }
}

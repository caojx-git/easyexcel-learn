package caojx.learn.easyexcellearn.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户导入错误信息
 *
 * @author caojx created on 2023/2/21 18:32
 */
@Data
public class UserExcelImportErrDto extends UserExcelImportDto{

    @ExcelProperty(index = 4,value = "错误信息")
    private String errMsg;
}

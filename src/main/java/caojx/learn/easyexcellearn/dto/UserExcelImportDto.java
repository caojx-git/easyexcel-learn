package caojx.learn.easyexcellearn.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 用户excel DTO
 *
 * @author caojx created on 2023/2/21 14:40
 */
@Data
public class UserExcelImportDto {

    @ExcelProperty(index = 0, value = "姓名")
    private String name;

    @ExcelProperty(index = 1, value = "性别")
    private String sex;

    @Min(value = 18,message = "年龄最小100")
    @ExcelProperty(index = 2, value = "年龄")
    private Integer age;

    @DateTimeFormat("yyyy/MM/dd")
    @ExcelProperty(index = 3, value = "生日")
    private Date birthday;
}

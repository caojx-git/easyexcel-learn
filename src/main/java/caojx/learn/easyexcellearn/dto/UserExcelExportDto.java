package caojx.learn.easyexcellearn.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.util.Date;

/**
 * 用户excel DTO
 *
 * @author caojx created on 2023/2/21 14:40
 */
@Data
public class UserExcelExportDto {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("年龄")
    private Integer age;

    @DateTimeFormat("yyyy/MM/dd")
    @ExcelProperty("生日")
    private Date birthday;
}

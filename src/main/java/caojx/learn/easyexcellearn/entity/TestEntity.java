package caojx.learn.easyexcellearn.entity;

import caojx.learn.easyexcellearn.converter.DateConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 test
 *
 * @author trg
 * @date Fri Jan 19 14:14:08 CST 2024
 */
@Data
@TableName("test")
public class TestEntity  {

    /**
     * 数字
     */
    @ExcelProperty("数字")
    private BigDecimal num;


    /**
     * 性别
     */
    @ExcelProperty("性别")
    private String sex;


    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;


    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间",converter = DateConverter.class)
    private Date bornDate;
}
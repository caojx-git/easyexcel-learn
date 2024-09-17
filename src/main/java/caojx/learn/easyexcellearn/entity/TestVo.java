package caojx.learn.easyexcellearn.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 类注释，描述 //TODO
 *
 * @author caojx
 * @since 2024/1/31 11:32
 */
@Data
public class TestVo {

    private BigDecimal num;


    /**
     * 性别
     */
    private String sex;


    /**
     * 姓名
     */
    private String baseName;

}
package caojx.learn.easyexcellearn.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体
 *
 * @author caojx created on 2023/2/21 14:35
 */
@Data
public class User {

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 生日
     */
    private Date birthday;
}

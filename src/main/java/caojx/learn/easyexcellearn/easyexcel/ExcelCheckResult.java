package caojx.learn.easyexcellearn.easyexcel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * excel导入错误结果
 *
 * @author caojx created on 2023/2/21 15:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelCheckResult<T> {

    private T t;

    /**
     * 行号
     */
    private Integer rowIndex;

    /**
     * 校验失败信息
     */
    private String errMsg;
}

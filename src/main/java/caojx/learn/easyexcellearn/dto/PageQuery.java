package caojx.learn.easyexcellearn.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
@SuperBuilder
public class PageQuery implements Serializable {

    /**
     * 当前页数，从1开始
     */
    private int pageNum = 1;

    /**
     * 每页数量
     */
    private int pageSize = 20;

}


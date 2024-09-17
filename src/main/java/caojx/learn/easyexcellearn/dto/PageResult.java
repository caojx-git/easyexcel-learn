package caojx.learn.easyexcellearn.dto;

/**
 * 类注释，描述 //TODO
 *
 * @author caojx
 * @since 2024/2/4 11:28
 */

import lombok.Data;

import java.util.List;

/**
 * 分页查询按照当前接口进行封装，整体数据最后封装在ApiResult的data中
 */
@Data
public class PageResult<T> {
    /**
     * 分页查询结果
     */
    private List<T> records;
    /**
     * 分页查询结果总数
     */
    private long total;
    /**
     * 分页查询每页数据条数
     */
    private long size;
    /**
     * 分页查询当前页码，从1开始
     */
    private long current;
}
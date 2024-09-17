package caojx.learn.easyexcellearn.service;

import caojx.learn.easyexcellearn.entity.TestEntity;
import caojx.learn.easyexcellearn.entity.TestVo;

import java.util.List;

/**
 * 类注释，描述 //TODO
 *
 * @author caojx
 * @since 2024/1/31 11:25
 */
public interface TestService {

    void saveBatchTest(List<TestVo> list);

    void saveBatch(List<TestEntity> list);

    List<TestEntity> list();
}

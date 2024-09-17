package caojx.learn.easyexcellearn.service;

import caojx.learn.easyexcellearn.entity.TestEntity;
import caojx.learn.easyexcellearn.entity.TestVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类注释，描述 //TODO
 *
 * @author caojx
 * @since 2024/1/31 11:36
 */
@Service
public class TestServiceImpl implements TestService {
    @Override
    public void saveBatchTest(List<TestVo> list) {
    }

    @Override
    public void saveBatch(List<TestEntity> list) {
    }

    @Override
    public List<TestEntity> list() {
        return null;
    }
}
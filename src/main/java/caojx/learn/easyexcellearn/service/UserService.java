package caojx.learn.easyexcellearn.service;

import caojx.learn.easyexcellearn.entity.User;

import java.util.List;

/**
 * 用户服务
 *
 * @author caojx created on 2023/2/21 14:39
 */
public interface UserService {

    /**
     * 获取所有用户信息
     *
     * @return 用户列表
     */
    List<User> getAllUser();

    /**
     * 批量保存用户信息
     *
     * @param userList 用户列表
     */
    void batchSave(List<User> userList);
}

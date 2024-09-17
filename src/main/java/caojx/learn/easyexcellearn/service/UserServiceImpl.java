package caojx.learn.easyexcellearn.service;

import caojx.learn.easyexcellearn.entity.User;
import caojx.learn.easyexcellearn.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务类
 *
 * @author caojx created on 2023/2/21 14:41
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<User> getAllUser() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("张三" + i);
            user.setSex(i % 2 == 0 ? "男" : "女");
            user.setAge(18 + i);
            user.setBirthday(new Date());
            list.add(user);
        }
        return list;
    }

    @Override
    public void batchSave(List<User> userList) {
        log.info("批量保存用户信息，数量 {}", userList.size());
    }
}

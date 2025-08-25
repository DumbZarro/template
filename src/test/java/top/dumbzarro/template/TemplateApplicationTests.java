package top.dumbzarro.template;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.postgre.UserRepository;

@SpringBootTest
class TemplateApplicationTests {

    @Resource
    UserRepository userRepository;

    @Test
    void contextLoads() {
        UserPo userPo = new UserPo();
        userPo.setAccountStatus(UserPo.AccountStatus.NORMAL);
        userPo.setEmail("serw");
        userRepository.save(userPo);
    }

}

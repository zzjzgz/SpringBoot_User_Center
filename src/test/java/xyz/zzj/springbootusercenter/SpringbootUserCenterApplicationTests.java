package xyz.zzj.springbootusercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import xyz.zzj.springbootusercenter.model.domain.User;
import xyz.zzj.springbootusercenter.service.UserService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;


@SpringBootTest
class SpringbootUserCenterApplicationTests {


    @Resource
    UserService userService;

    @Test
    void testSelect() {
        userService.list().forEach(System.out::println);
    }

}

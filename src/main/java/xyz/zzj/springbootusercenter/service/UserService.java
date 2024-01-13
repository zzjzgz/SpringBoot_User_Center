package xyz.zzj.springbootusercenter.service;

import xyz.zzj.springbootusercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author zeng
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-08 16:50:47
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 新用户的id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount        用户的账号
     * @param userPassword       用户的密码
     * @param httpServletRequest 保存用户的登录态
     * @return 返回用户的信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户信息脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 请求用户注销
     * @param httpServletRequest
     * @return
     */
    int userLogout(HttpServletRequest httpServletRequest);

}

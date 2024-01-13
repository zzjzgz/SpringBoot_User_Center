package xyz.zzj.springbootusercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import xyz.zzj.springbootusercenter.connon.ErrorCode;
import xyz.zzj.springbootusercenter.exception.BusinessException;
import xyz.zzj.springbootusercenter.model.domain.User;
import xyz.zzj.springbootusercenter.service.UserService;
import xyz.zzj.springbootusercenter.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.zzj.springbootusercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author zeng
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-01-08 16:50:47
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    /**
     * 对密码进行加盐，混淆
     */
    private static final String S_ALT = "zzj";



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //判断非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //账户：不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        //密码：不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账户不能包含特殊字符，用正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户中含特殊字符");
        }
        //判断密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不相等");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //2、对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((S_ALT + userPassword).getBytes());
        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        //设置默认头像和昵称
        user.setUsername(userAccount);
        user.setAvatarUrl("https://img.touxiangwu.com/zb_users/upload/2022/10/202210311667198862146079.jpg");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        //判断非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //账户：不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        //密码：不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账户不能包含特殊字符，用正则表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入正确的账号");
        }
        //校验密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((S_ALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);  //通过账号密码去数据库查数据
        if (user == null){
            //记录下日志
            log.info("\"Invalid username or password\" ");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入的账号或密码有误");
        }
        //对用户信息进行脱敏
       User safatyUser = getSafetyUser(user);
        //记录用户的登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safatyUser);
        //返回脱敏的数据
        return safatyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User safatyUser = new User();
        safatyUser.setId(originUser.getId());
        safatyUser.setUsername(originUser.getUsername());
        safatyUser.setAvatarUrl(originUser.getAvatarUrl());
        safatyUser.setGender(originUser.getGender());
        safatyUser.setUserAccount(originUser.getUserAccount());
        safatyUser.setPhone(originUser.getPhone());
        safatyUser.setEmail(originUser.getEmail());
        safatyUser.setUserRole(originUser.getUserRole());
        safatyUser.setUserStatus(originUser.getUserStatus());
        safatyUser.setCreateTime(originUser.getCreateTime());
        return safatyUser;
    }

    /**
     * 注销用户登录态
     * @param httpServletRequest
     */
    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}





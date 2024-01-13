package xyz.zzj.springbootusercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import xyz.zzj.springbootusercenter.connon.BaseResponse;
import xyz.zzj.springbootusercenter.connon.ErrorCode;
import xyz.zzj.springbootusercenter.connon.ResultUtils;
import xyz.zzj.springbootusercenter.exception.BusinessException;
import xyz.zzj.springbootusercenter.model.domain.User;
import xyz.zzj.springbootusercenter.model.domain.request.UserLoginRequest;
import xyz.zzj.springbootusercenter.model.domain.request.UserRegisterRequest;
import xyz.zzj.springbootusercenter.service.UserService;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.zzj.springbootusercenter.constant.UserConstant.*;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.controller
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-09  15:30
 * @Description: TODO
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
//这个是线上用于跨域的，本地请注释其注解
//@CrossOrigin(origins = {"你自己服务器的ip"},allowCredentials = "true")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(l);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    //用户注销
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }



    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest){
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR,"用户未登录");
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User> > userSearch(String username,HttpServletRequest httpServletRequest){
        //判断是否为管理员
        if (!isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //查询数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isAnyBlank(username)){  //存在就查询，不存在就返回空
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> userSearch(long id,HttpServletRequest httpServletRequest){
        //判断是否为管理员
        if (!isAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);//更新逻辑删除字段
        return ResultUtils.success(b);
    }

    private boolean isAdmin(HttpServletRequest httpServletRequest){
        //判断是否为管理员
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}


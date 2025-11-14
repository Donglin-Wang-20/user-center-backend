package com.yupi.usercenter.controller;

import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserLoginRequest;
import com.yupi.usercenter.model.domain.UserRegisterRequest;
import com.yupi.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * @author WangDonglin
 */
// RestController 适用于编写 restful 风格的 api，返回值类型默认为 json 类型
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 用户id
     */
    @PostMapping("/register")
    public Long userResister(@RequestBody UserRegisterRequest userRegisterRequest) {

        // 判断用户注册对象数据不为空
        if (userRegisterRequest == null) {
            return null;
        }

        // 判断每个参数不为空
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }

        // 调用service层注册账号
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        // 判断用户注册对象数据不为空
        if (userLoginRequest == null) {
            return null;
        }

        // 判断每个参数不为空
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            return null;
        }

        // 调用service层注册账号
        return userService.userLogin(userAccount, userPassword, request);
    }
}

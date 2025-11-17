package com.yupi.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.request.UserLoginRequest;
import com.yupi.usercenter.model.domain.request.UserRegisterRequest;
import com.yupi.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.yupi.usercenter.constant.UserConstant.USER_LOGIN_STATE;

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

    /**
     * 查询用户
     *
     * @param username 用户名
     * @param request  网络请求
     * @return 查询出来的用户列表
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        // 仅管理员可查询
        if (extracted(request)) return new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> {
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
    }

    /**
     * 删除用户
     *
     * @param id      用户id
     * @param request 网络请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public Boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!extracted(request)) return false;
        if (id < 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     *
     * @param request 网络请求
     * @return 当前用户是否为管理员
     */
    private static boolean extracted(HttpServletRequest request) {
        // 仅管理员可删除
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}

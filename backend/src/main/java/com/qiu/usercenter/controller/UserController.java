package com.qiu.usercenter.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.qiu.usercenter.common.BaseResponse;
import com.qiu.usercenter.common.ErrorCode;
import com.qiu.usercenter.common.Result;
import com.qiu.usercenter.exception.BusinessException;
import com.qiu.usercenter.model.request.UserLoginRequest;
import com.qiu.usercenter.model.request.UserRegisterRequest;
import com.qiu.usercenter.model.po.User;
import com.qiu.usercenter.model.request.PageRequest;
import com.qiu.usercenter.model.request.UserUpdateRequest;
import com.qiu.usercenter.model.vo.UserListVO;
import com.qiu.usercenter.model.vo.UserVO;
import com.qiu.usercenter.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.qiu.usercenter.constant.UserConstant.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户中心")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * @description: 用户注册
     * @params: [UserRegisterRequest]
     * @return: com.qiu.usercenter.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:33
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public BaseResponse<Boolean> userRegister(@ApiParam(value = "用户注册请求体对象", required = true) @RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String verifyPassword = userRegisterRequest.getVerifyPassword();
        // 1.校验
        if (BeanUtil.isEmpty(userRegisterRequest) || StrUtil.hasEmpty(userAccount, userPassword, verifyPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = userService.userRegister(userRegisterRequest);
        return Result.success(result);
    }

    /**
     * @description: 用户登录
     * @params: [userLoginRequest, request]
     * @return: com.qiu.usercenter.common.BaseResponse<com.qiu.usercenter.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2023/12/16 19:46
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@ApiParam(value = "用户登录请求体对象", required = true) @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1.校验
        if (BeanUtil.isEmpty(userLoginRequest) || StrUtil.hasEmpty(userAccount, userPassword)) {
            return Result.error(ErrorCode.NULL_ERROR);
        }
        UserVO userVO = userService.userLogin(userLoginRequest, request);
        return Result.success(userVO);
    }

    /**
     * @description: 获取用户登录态
     * @params: [request]
     * @return: com.qiu.usercenter.common.BaseResponse<com.qiu.usercenter.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2023/12/16 19:50
     */
    @ApiOperation("获取用户登录态")
    @GetMapping("/current")
    public BaseResponse<UserVO> userCurrent(HttpServletRequest request) {
        // 检验是否登录
        UserVO userVO = userService.getLoginUser(request);
        if (BeanUtil.isEmpty(userVO)) {
            return Result.error(ErrorCode.NOT_LOGIN);
        }
        // 查库检验
        User user = userService.getById(userVO.getId());
        if (BeanUtil.isEmpty(user)) {
            return Result.error(ErrorCode.NULL_ERROR);
        }
        UserVO userInfo = BeanUtil.copyProperties(user, UserVO.class);
        return Result.success(userInfo);
    }

    /**
     * @description: 根据id删除用户
     * @params: [id, request]
     * @return: com.qiu.usercenter.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:57
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@ApiParam(value = "用户id", required = true) @RequestParam("id") Long id, HttpServletRequest request) {
        // 1.鉴权
        if (!userService.isAdmin(request)) {
            return Result.error(ErrorCode.NO_AUTH);
        }
        boolean isDeleted = userService.removeById(id);
        return Result.success(isDeleted);
    }

    /**
     * @description: 用户注销
     * @params: [request]
     * @return: com.qiu.usercenter.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/16 19:58
     */
    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return Result.success(true);
    }

    /**
     * @description: 管理员更新用户信息
     * @params: [request, user]
     * @return: com.qiu.usercenter.common.BaseResponse<java.lang.Boolean>
     * @author: qiu
     * @dateTime: 2023/12/21 10:11
     */
    @ApiOperation("用户更新")
    @PutMapping("/update")
    public BaseResponse<Boolean> updateUserById(@ApiParam(value = "用户更新请求体对象", required = true) @RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Boolean admin = userService.isAdmin(request);
        if (!admin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Boolean result = userService.updateUserById(userUpdateRequest);
        return Result.success(result);
    }

    /**
     * @description: 获取所有用户
     * @params: [pageRequest]
     * @return: com.qiu.usercenter.common.BaseResponse<java.util.List<com.qiu.usercenter.model.vo.UserVO>>
     * @author: qiu
     * @dateTime: 2024/4/15 13:49
     */
    @ApiOperation("获取所有用户")
    @PostMapping("/all")
    public BaseResponse<UserListVO> getAllUser(@ApiParam(value = "分页参数对象", required = true) @RequestBody PageRequest pageRequest) {
        return userService.getAllUser(pageRequest);
    }

}

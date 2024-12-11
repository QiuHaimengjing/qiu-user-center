package com.qiu.usercenter.service;

import com.qiu.usercenter.common.BaseResponse;
import com.qiu.usercenter.model.request.UserLoginRequest;
import com.qiu.usercenter.model.request.UserRegisterRequest;
import com.qiu.usercenter.model.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiu.usercenter.model.request.PageRequest;
import com.qiu.usercenter.model.request.UserUpdateRequest;
import com.qiu.usercenter.model.vo.UserListVO;
import com.qiu.usercenter.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
public interface IUserService extends IService<User> {

    Boolean userRegister(UserRegisterRequest userRegisterRequest);

    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    Boolean updateUserById(UserUpdateRequest userUpdateRequest);

    Boolean isAdmin(HttpServletRequest request);

    UserVO getLoginUser(HttpServletRequest request);

    BaseResponse<UserListVO> getAllUser(PageRequest pageRequest);

}

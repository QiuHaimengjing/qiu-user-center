package com.qiu.usercenter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiu.usercenter.common.BaseResponse;
import com.qiu.usercenter.common.ErrorCode;
import com.qiu.usercenter.common.Result;
import com.qiu.usercenter.exception.BusinessException;
import com.qiu.usercenter.model.request.UserLoginRequest;
import com.qiu.usercenter.model.request.UserRegisterRequest;
import com.qiu.usercenter.model.po.User;
import com.qiu.usercenter.mapper.UserMapper;
import com.qiu.usercenter.model.request.PageRequest;
import com.qiu.usercenter.model.request.UserUpdateRequest;
import com.qiu.usercenter.model.vo.UserListVO;
import com.qiu.usercenter.model.vo.UserVO;
import com.qiu.usercenter.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiu.usercenter.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import static com.qiu.usercenter.constant.UserConstant.*;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiu
 * @since 2023-12-09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * @description: 用户注册
     * @params: [userRegisterRequest]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 22:56
     */
    @Override
    public Boolean userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String verifyPassword = userRegisterRequest.getVerifyPassword();

        // 1.账号密码校验
        Boolean checkRules = sameCheckRules(userRegisterRequest);
        if (!checkRules) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.密码和校验密码不相同
        if (!StrUtil.equals(userPassword, verifyPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "密码和校验密码不相同");
        }
        // 3.账户不能重复
        Long count = this.lambdaQuery().eq(User::getUserAccount, userAccount).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户已注册");
        }
        // 4.对密码进行加密
        SHA256 encryption = SHA256.encryptionPassword(userPassword);
        userRegisterRequest.setUserPassword(encryption.getSha256Password());
        // 5.插入用户数据
        User user = BeanUtil.copyProperties(userRegisterRequest, User.class);
        user.setSalt(encryption.getSalt());
        this.save(user);
        return true;
    }

    /**
     * @description: 用户登录
     * @params: [userLoginRequest, request]
     * @return: com.qiu.usercenter.model.vo.UserVO
     * @author: qiu
     * @dateTime: 2024/3/13 23:02
     */
    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 1.校验
        Boolean checkRules = sameCheckRules(userLoginRequest);
        if (!checkRules) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.查询该用户
        User user = lambdaQuery().eq(User::getUserAccount, userAccount).one();
        if (BeanUtil.isEmpty(user)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码错误");
        }
        // 3.校验密码
        String password = user.getUserPassword();
        String salt = user.getSalt();
        String decryptPassword = SHA256.decryptPassword(salt, userPassword);
        if (!StrUtil.equals(decryptPassword, password)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码错误");
        }
        // 4.脱敏并记录用户登录状态
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * @description: 根据id更新用户信息
     * @params: [userUpdateRequest]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 23:10
     */
    @Override
    public Boolean updateUserById(UserUpdateRequest userUpdateRequest) {
        // 1.判断用户是否为空
        if (BeanUtil.isEmpty(userUpdateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = BeanUtil.copyProperties(userUpdateRequest, User.class);
        boolean isUpdate = this.updateById(user);
        // 2.判断是否更新成功
        if (!isUpdate) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "更新失败，用户不存在");
        }
        // 3.更新成功返回true
        return true;
    }

    /**
     * @description: 用户鉴权
     * @params: [request]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2023/12/9 19:13
     */
    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO userVO = BeanUtil.copyProperties(attribute, UserVO.class);
        Integer userRole = userVO.getUserRole();
        return NumberUtil.equals(userRole, ADMIN);
    }

    /**
     * @description: 获取当前登录用户
     * @params: [request]
     * @return: com.qiu.usercenter.model.vo.UserVO
     * @author: qiu
     * @dateTime: 2024/2/23 23:03
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (BeanUtil.isEmpty(attribute)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return BeanUtil.copyProperties(attribute, UserVO.class);
    }

    /**
     * @description: 获取所有用户
     * @params: [pageRequest]
     * @return: java.util.List<com.qiu.usercenter.model.vo.UserVO>
     * @author: qiu
     * @dateTime: 2024/4/15 13:49
     */
    @Override
    public BaseResponse<UserListVO> getAllUser(PageRequest pageRequest) {
        // 1.校验分页参数
        Integer pageNO = pageRequest.getPageNO();
        Integer pageSize = pageRequest.getPageSize();
        if (pageNO == null || pageSize == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> page = Page.of(pageNO, pageSize);
        Page<User> userPage = this.page(page);
        long total = userPage.getTotal();
        List<User> records = userPage.getRecords();
        UserListVO userListVO = new UserListVO();
        userListVO.setTotal(total);
        if (CollUtil.isEmpty(records)) {
            userListVO.setUsers(Collections.emptyList());
            return Result.success(userListVO);
        }
        List<UserVO> userVOList = BeanUtil.copyToList(records, UserVO.class);
        userListVO.setUsers(userVOList);
        return Result.success(userListVO);
    }

    /**
     * @description: 账号密码校验
     * @params: [object]
     * @return: java.lang.Boolean
     * @author: qiu
     * @dateTime: 2024/3/13 22:54
     */
    private Boolean sameCheckRules(Object object) {
        User user = BeanUtil.copyProperties(object, User.class);
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();

        // 1.非空校验
        if (BeanUtil.isEmpty(user) || StrUtil.hasEmpty(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码为空");
        }
        // 2.账户不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不小于4位");
        }
        // 3.密码不小于8位
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不小于6位");
        }
        // 4.账户不包含特殊字符
        if (!Validator.isGeneral(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不包含特殊字符");
        }
        return true;
    }
}

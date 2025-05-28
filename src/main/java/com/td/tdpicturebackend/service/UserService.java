package com.td.tdpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.td.tdpicturebackend.model.dto.user.UserQueryRequest;
import com.td.tdpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.td.tdpicturebackend.model.vo.LoginUserVO;
import com.td.tdpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author td
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-18 19:43:19
*/
public interface UserService extends IService<User> {

    /**
     * 注册
     * @param userAccount
     * @param userPassword
     * @param checkPasswrod
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPasswrod);

    /**
     * 登录 LoginUserVO脱敏
     * @param userAccount
     * @param userPassword
     * @param request
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前用户
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获得脱敏后的信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获得脱敏后的用户信息列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户登录态注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper (UserQueryRequest userQueryRequest);

    /**
     * 判断是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);
}

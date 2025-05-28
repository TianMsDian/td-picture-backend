package com.td.tdpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.td.tdpicturebackend.constant.UserConstant;
import com.td.tdpicturebackend.exception.BusinessException;
import com.td.tdpicturebackend.exception.ErrorCode;
import com.td.tdpicturebackend.model.dto.user.UserQueryRequest;
import com.td.tdpicturebackend.model.entity.User;
import com.td.tdpicturebackend.model.enums.UserRoleEnum;
import com.td.tdpicturebackend.model.vo.LoginUserVO;
import com.td.tdpicturebackend.model.vo.UserVO;
import com.td.tdpicturebackend.service.UserService;
import com.td.tdpicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author td
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-03-18 19:43:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 注册
     * @param userAccount
     * @param userPassword
     * @param checkPasswrod
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPasswrod) {
        // 1 校验参数
        // todo 待优化
        if (StrUtil.hasBlank(userAccount,userPassword,checkPasswrod)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length()<8 || checkPasswrod.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (!userPassword.equals(checkPasswrod)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        // ThrowUtils.throwIf(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"账号过短");
        // 2 检查用户账号是否与数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        // 3 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4 插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败,数据库错误");
        }
        return user.getId();
    }

    /**
     * 登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 脱敏后的用户登录信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // todo
        // 1 校验
        if (StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号错误");
        }
        if (userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        // 2 对用户传递的密码进行加码
        String encryptPassword = getEncryptPassword(userPassword);
        // 3 判断数据库用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 4 不存在抛异常
        if (user == null) {
            log.info("user login failed  userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"数据不存在");
        }
        // 5 登录成功记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword){
        // DigestUtils.md5DigestAsHex() 字符串转换成 字节数组 此方法转换成md5加密
        // 加点盐,混淆密码
        final String SALT = "tian";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 是否登录了呢
        Object  userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库中查询 (追求性能就注释掉)
        // 如果不去数据库再查一遍 在修改名字的情况下显示的依然还是旧的数据
        Long userId = currentUser.getId();
        currentUser  = this.getById(userId);
        // 以下判断是为了可能存在数据丢失的情况下 没有查到信息就直接报错不会返回一个空壳
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"数据丢失，联系管理员");
        }
        return currentUser;
    }

    /**
     * 脱敏后的数据信息
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        // user的值 全部赋予 loginiUserVo对象
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 获得脱敏后的用户信息
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        // user的值 全部赋予 UserVO
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 获得脱敏后的用户信息 列表
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());

    }

    /**
     * 用户登录态注销
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object  userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //移除
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        // int current = userQueryRequest.getCurrent();
        // int pageSize = userQueryRequest.getPageSize();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id",id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole),"userRole",userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount),"userAccount",userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName),"userName",userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile),"userProfile",userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user !=null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
}





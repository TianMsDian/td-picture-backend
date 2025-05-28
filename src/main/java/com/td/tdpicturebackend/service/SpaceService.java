package com.td.tdpicturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.td.tdpicturebackend.model.dto.space.SpaceAddRequest;
import com.td.tdpicturebackend.model.dto.space.SpaceQueryRequest;
import com.td.tdpicturebackend.model.entity.Space;
import com.td.tdpicturebackend.model.entity.User;
import com.td.tdpicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author td
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-05-22 11:29:07
*/
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest,User loginUser);

    /**
     * 校验空间
     * @param space
     * @param add
     */
    void validSpace(Space space,boolean add);


    /**
     * 获取空间封装类
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间封装类
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询对象
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充控件对象
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);
}

package com.td.tdpicturebackend.controller;

import com.td.tdpicturebackend.common.BaseResponse;
import com.td.tdpicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     */

    @RequestMapping("/health")
    public BaseResponse<String>  health(){
        return ResultUtils.success("ok");
    }


}

package com.td.tdpicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.utils.IOUtils;
import com.td.tdpicturebackend.config.CosClientConfig;
import com.td.tdpicturebackend.exception.BusinessException;
import com.td.tdpicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 图片处理规则列表
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 1.图片压缩 (转成 webp格式)
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule comPressRule = new PicOperations.Rule();
        comPressRule.setFileId(webpKey);
        comPressRule.setBucket(cosClientConfig.getBucket());
        comPressRule.setRule("imageMogr2/format/webp");
        rules.add(comPressRule);
        // 缩略图处理, 仅对 > 20KB 的图片生成缩略图
        if (file.length() > 2 * 1024){
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            // 2.拼接缩略图路径
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            // 缩放规则 /thumbnail/<Width>x<Height>> (如果大于原图宽高,则不处理)
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>",256,256));
            rules.add(thumbnailRule);
        }
        //构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }


    // /**
    //  * 将COS中的文件下载到本地
    //  * @param filepath  文件路径，如folder/picture.jpg
    //  * @param localPath 本地存储路径
    //  */
    // public void downloadPicture(String filepath, String localPath) throws IOException {
    //     File file = new File(localPath);
    //     COSObjectInputStream cosObjectInput = null;
    //     try {
    //         COSObject cosObject = this.getObject(filepath);
    //         cosObjectInput = cosObject.getObjectContent();
    //         // 将输入流转为字节数组
    //         byte[] data = IOUtils.toByteArray(cosObjectInput);
    //         // 将字节数组写入本地文件
    //         FileUtil.writeBytes(data, file);
    //     } catch (Exception e) {
    //         log.error("file download error, filepath = {}", filepath, e);
    //         throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
    //     } finally {
    //         if (cosObjectInput != null) {
    //             cosObjectInput.close();
    //         }
    //     }
    // }
}

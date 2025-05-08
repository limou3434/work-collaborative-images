package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.manager.CosManager;
import cn.com.edtechhub.workcollaborativeimages.mapper.PictureMapper;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-05-04 22:19:43
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    /**
     * 注入 COS 管理器依赖
     */
    @Resource
    CosManager cosManager;

    public Picture pictureAdd(PictureAddRequest pictureAddRequest) {
        return null;
    }

    public Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest) {
        return null;
    }

    public Picture pictureUpdate(PictureUpdateRequest pictureUpdateRequest) {
        return null;
    }

    public List<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest) {
        return null;
    }

    public String pictureTestUpload(String cosFilepath, MultipartFile multipartFile) {
        // 获取要上传的文件名字
        String filename = multipartFile.getOriginalFilename();
        log.debug("获取要上传的文件名字 {}", filename);

        // 获取要上传的目标路径
        String filepath = String.format("%s%s", cosFilepath, filename);
        log.debug("获取要上传的目标路径 {}", filepath);

        // 根据接口要求上传资源
        File file = null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            return filepath; // 返回可访问地址
        } catch (Exception e) {
            log.debug("file upload error, filepath = {}", filepath);
            throw new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    public void pictureTestDownload(String cosFilepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            // 获取 cos 资源内容
            COSObject cosObject = cosManager.getObject(cosFilepath); // 获取 cos 资源
            cosObjectInput = cosObject.getObjectContent(); // 获取 cos 资源流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput); // 转化为字节流

            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + cosFilepath);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.debug("file download error, filepath = {}", cosFilepath);
            throw new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}





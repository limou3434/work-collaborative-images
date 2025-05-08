package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 图片控制层
 * 最简单的方式就是上传到后端项目所在的服务器, 直接使用 Java 自带的文件读写 API 就能实现, 但缺点多
 * (1)不利于扩展: 单个服务器的存储是有限的, 如果存满了, 只能再新增存储空间或者清理文件
 * (2)不利于迁移: 如果后端项目要更换服务器部署, 之前所有的文件都要迁移到新服务器, 非常麻烦
 * (3)不利于安全: 如果忘记控制权限, 图片很有可能通过恶意代码访问服务器上的文件, 而且想控制权限也比较麻烦, 需要自己实现
 * (4)不利于管理: 只能通过一些文件管理器进行简单的管理操作, 但是缺乏数据处理、流量控制等多种高级能力
 * 因此推荐使用对象存储, 对象存储如果需要节约经费则使用 MinIO, 如果需要花少量价格则可以考虑使用腾讯 COS, 这里的采用使用腾讯 COS 来实现, 选购的是 COS 标准存储容量
 * 相关购买文档请查阅 https://cloud.tencent.com/act/cps/redirect?redirect=2446&cps_key=acb1d795fac01f14e7ecbed077b83119&from=console 或 https://cloud.tencent.com/product/cos
 * 然后可以查阅 https://cloud.tencent.com/document/product/436/10199 这个开发文档了解 COS 的 SDK 开发包
 * 熟练的话使用  API Explorer 也就是 https://console.cloud.tencent.com/api/explorer?Product=cos&Version=2018-11-26&Action=PutBucket 来查看开发代码
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@SuppressWarnings("ALL")
@RestController // 返回值默认为 json 类型
@RequestMapping("/picture")
@Slf4j
public class PictureController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /**
     * 图片注入服务实例
     */
    @Resource
    private PictureService pictureService;

    /**
     * 图片添加网络接口
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/add")
//    @SentinelResource(value = "pictureAdd")
    public BaseResponse<Picture> pictureAdd(@RequestBody PictureAddRequest pictureAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureAdd(pictureAddRequest));
    }

    /**
     * 图片删除网络接口
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/delete")
//    @SentinelResource(value = "pictureDelete")
    public BaseResponse<Boolean> pictureDelete(@RequestBody PictureDeleteRequest pictureDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureDelete(pictureDeleteRequest));
    }

    /**
     * 图片修改网络接口
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/update")
//    @SentinelResource(value = "pictureUpdate")
    public BaseResponse<Picture> pictureUpdate(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureUpdate(pictureUpdateRequest));
    }

    /**
     * 图片查询网络接口
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/search")
//    @SentinelResource(value = "pictureSearch")
    public BaseResponse<List<Picture>> pictureSearch(@RequestBody PictureSearchRequest pictureSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureSearch(pictureSearchRequest));
    }

    /**
     * 图片上传网络接口(简单测试)
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/test/upload")
//    @SentinelResource(value = "pictureTestUpload")
    public BaseResponse<String> pictureTestUpload(@RequestPart("file") MultipartFile multipartFile) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureTestUpload("/test/", multipartFile));
    }

    /**
     * 图片下载网络接口(简单测试)
     */
    @SaCheckLogin
    @SaCheckRole("admin")
    @GetMapping("/test/download")
//    @SentinelResource(value = "pictureTestDownload")
    public void pictureTestDownload(String cosFilepath, HttpServletResponse response) {
        try {
            pictureService.pictureTestDownload(cosFilepath, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 图片上传网络接口(无需权限)
     */
    @SaCheckLogin
    @PostMapping("/upload")
//    @SentinelResource(value = "pictureUpload")
    public void pictureUpload() {
    }

    /**
     * 图片下载网络接口(无需权限)
     */
    @SaCheckLogin
    @GetMapping("/download")
//    @SentinelResource(value = "pictureDownload")
    public void pictureDownload() {
    }

}

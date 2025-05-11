package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.vo.PictureVO;
import cn.com.edtechhub.workcollaborativeimages.model.vo.UserVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 注入图片服务依赖
     */
    @Resource
    private PictureService pictureService;

    /**
     * 注入用户服务依赖
     */
    @Resource
    private UserService userService;

    /// 仅管理员 ///
    @Operation(summary = "图片添加网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/add")
//    @SentinelResource(value = "pictureAdd")
    public BaseResponse<Picture> pictureAdd(@RequestBody PictureAddRequest pictureAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureAdd(pictureAddRequest)); // 可以直接绕过 COS 进行添加落库
    }

    @Operation(summary = "图片删除网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/delete")
//    @SentinelResource(value = "pictureDelete")
    public BaseResponse<Boolean> pictureDelete(@RequestBody PictureDeleteRequest pictureDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureDelete(pictureDeleteRequest)); // TODO: 实际上管理员删除接口最重要的一点就是可以直接清理 COS 上的图片, 但是普通用户只是去除数据库中的关联而已
    }

    @Operation(summary = "图片更新网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/update")
//    @SentinelResource(value = "pictureUpdate")
    public BaseResponse<Picture> pictureUpdate(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureUpdate(pictureUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "图片查询网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/search")
//    @SentinelResource(value = "pictureSearch")
    public BaseResponse<Page<Picture>> pictureSearch(@RequestBody PictureSearchRequest pictureSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureSearch(pictureSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///
    @Operation(summary = "获取当前后支持图片类别网络接口")
    @SaCheckLogin
    @GetMapping("/categorys")
//    @SentinelResource(value = "pictureCategorys")
    public BaseResponse<List<String>> pictureCategorys() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureGetCategorys());
    }

    @Operation(summary = "用户上传图片网络接口")
    @SaCheckLogin
    @PostMapping("/upload")
//    @SentinelResource(value = "pictureUpload")
    public BaseResponse<PictureVO> pictureUpload(
            @RequestParam(value = "pictureId", required = false) Long pictureId,
            @RequestParam(value = "pictureCategory", required = false) String pictureCategory,
            @RequestParam(value = "pictureName", required = false) String pictureName,
            @RequestParam(value = "pictureIntroduction", required = false) String pictureIntroduction,
            @RequestParam(value = "pictureTags", required = false) String pictureTags,
            @RequestPart(value = "pictureFile", required = false) MultipartFile multipartFile
    ) {
        PictureVO pictureVO = PictureVO.removeSensitiveData(pictureService.pictureUpload(pictureId, pictureCategory, pictureName, pictureIntroduction, pictureTags, multipartFile));
        pictureVO.setUserVO(UserVO.removeSensitiveData(userService.userGetLoginInfo()));
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureVO);
    }

    @Operation(summary = "用户销毁图片网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
//    @SentinelResource(value = "pictureDestroy")
    public BaseResponse<Boolean> pictureDestroy(@RequestBody PictureDeleteRequest pictureDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureDelete(pictureDeleteRequest));
    }

    @Operation(summary = "脱敏后的图片查询网络接口")
    @SaCheckLogin
    @PostMapping("/search/vo")
//    @SentinelResource(value = "pictureSearchVo")
    public BaseResponse<Page<PictureVO>> pictureSearchVo(@RequestBody PictureSearchRequest pictureSearchRequest) {
        // 先查出所有用户分页和图片分页
        Page<Picture> picturePage = pictureService.pictureSearch(pictureSearchRequest);
        Page<User> userPage = userService.userSearch(new UserSearchRequest());

        // 利用映射机制来减少多次单 SQL 后顺便做脱敏
        List<Picture> pictureList = picturePage.getRecords();
        log.debug("对应查询要求的图片列表 {}", pictureList);
        List<User> userList = userPage.getRecords();
        log.debug("库中的所有用户 {}", userList);

        // 开始映射 TODO: 这里的映射在用户过量时还是有一些问题的
        Map<Long, User> userMap = userList
                .stream()
                .collect(Collectors.toMap(
                        user -> {
                            return user.getId();
                        },
                        user -> {
                            return user;
                        },
                        (user1, user2) -> {
                            return user1;
                        }
                )); // 构建 userId 到 User 的映射避免 N+1 查询
        log.debug("避免多次查询所构建的临时 userMap 的值为 {}", userMap);
        List<PictureVO> pictureVOList = pictureList
                .stream()
                .map(picture -> {
                    PictureVO pictureVO = PictureVO.removeSensitiveData(picture); // 需要脱敏
                    User user = userMap.get(picture.getUserId()); // 需要把用户信息都映射进去, 同时避免重复查询
                    if (user != null) {
                        pictureVO.setUserVO(UserVO.removeSensitiveData(user));
                    }
                    return pictureVO;
                })
                .collect(Collectors.toList());

        log.debug("查询到的图片列表脱敏后为 {}", pictureVOList);
        // 重新构造一个分页对象
        Page<PictureVO> pictureVOPage = new Page<>();
        pictureVOPage.setCurrent(picturePage.getCurrent());
        pictureVOPage.setSize(picturePage.getSize());
        pictureVOPage.setTotal(picturePage.getTotal());
        pictureVOPage.setRecords(pictureVOList);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureVOPage);
    }

}

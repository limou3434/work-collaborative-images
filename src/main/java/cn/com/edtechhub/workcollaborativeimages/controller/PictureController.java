package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.PictureReviewStatusEnum;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.manager.ai.CreateOutPaintingTaskResponse;
import cn.com.edtechhub.workcollaborativeimages.manager.ai.GetOutPaintingTaskResponse;
import cn.com.edtechhub.workcollaborativeimages.model.dto.ImageSearchResult;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.*;
import cn.com.edtechhub.workcollaborativeimages.model.vo.PictureVO;
import cn.com.edtechhub.workcollaborativeimages.model.vo.UserVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.PageUtils;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 图片控制层
 * 1. 基础实现
 * 最简单的方式就是上传到后端项目所在的服务器, 直接使用 Java 自带的文件读写 API 就能实现, 但缺点多
 * (1) 不利于扩展: 单个服务器的存储是有限的, 如果存满了, 只能再新增存储空间或者清理文件
 * (2) 不利于迁移: 如果后端项目要更换服务器部署, 之前所有的文件都要迁移到新服务器, 非常麻烦
 * (3) 不利于安全: 如果忘记控制权限, 图片很有可能通过恶意代码访问服务器上的文件, 而且想控制权限也比较麻烦, 需要自己实现
 * (4) 不利于管理: 只能通过一些文件管理器进行简单的管理操作, 但是缺乏数据处理、流量控制等多种高级能力
 * 因此推荐使用对象存储, 对象存储如果需要节约经费则使用 MinIO, 如果需要花少量价格则可以考虑使用腾讯 COS, 这里的采用使用腾讯 COS 来实现, 选购的是 COS 标准存储容量
 * 相关购买文档请查阅 https://cloud.tencent.com/act/cps/redirect?redirect=2446&cps_key=acb1d795fac01f14e7ecbed077b83119&from=console 或 https://cloud.tencent.com/product/cos
 * 然后可以查阅 https://cloud.tencent.com/document/product/436/10199 这个开发文档了解 COS 的 SDK 开发包
 * 熟练的话使用  API Explorer 也就是 https://console.cloud.tencent.com/api/explorer?Product=cos&Version=2018-11-26&Action=PutBucket 来查看开发代码
 * <p>
 * 2. 优化设计
 * 当然图片还有很多可以优化的地方:
 * (1) 查询优化:
 * 本项目的图片较多, 大量查询的概率很大, 因此就需要考虑使用缓存来加快查询速度, 可以使用 Redis 缓存和 Caffeine 缓存二级缓存机制来提高查询速度
 * 需要缓存数据格式为 {项目缩写}:{查询接口}:{查询条件} -> {查询结果}
 * <p>
 * 其中查询结果:
 * a. 若考虑可读性可选 JSON 结构(本项目选择这一种)
 * b. 若考虑压缩空间可选二进制结构
 * <p>
 * 此处由于查询条件较多, 而且考虑到图片会持续更新, 过期时间设置为 60s ~ 60 min 即可, 并且需要使用二级缓存来提高访问效率, 使用远端缓存大该优化 4 倍, 使用本地缓存再优化一倍
 * 如果可以有时间可以继续优化 Redis 的经典三问, 以及热点探测问题
 * <p>
 * (2) 上传优化:
 * 对于图库网站来说, 图片压缩是图片优化中最基本且最重要的操作, 能够显著减少图片文件的大小, 从而降低带宽使用和流量消耗, 大幅降低成本的同时, 提高图片加载速度, 本项目则选择偏向无压缩的实现
 * a. 格式上: 为了兼容性舍弃使用 AVIF 而使用 WebP(如果使用腾讯云对象存储只需要在上传文件时在 HTTP 中传入 Rules 规则)
 * b. 方案上: 压缩方案, 直接选择腾讯 COS 对应的数据万象压缩为 webp, 可以参考文档 https://cloud.tencent.com/document/product/460/72229, 有时间还可以尝试更大比例的压缩, 比如使用质量变换来处理图片, https://cloud.tencent.com/document/product/436/44884
 * c. 时机上: 压缩的时机有三种 "访问图片时压缩、上传图片时压缩、对已上传图片进行压缩", 这里选择第二种
 * d. 秒传上: 本项目有时间可以支持图片秒传机制, 这里的实现机制其实就是 "欺骗用户", 实际上就是基于数字指纹识别, 识别重复的图片进行快传, 后端接收到文件指纹后, 在存储中查询是否已存在相同文件(这也意味着必须多存储一份图片数字指纹信息), 不过本项目不大, 使用数字指纹的收益不高, 并且由于使用了腾讯对象存储, 没有文件快捷方式(符号链接)的概念, 就必须导致我们要自己维护软连接机制, 不同用户可能使用的是同一张图片可能导致常见的软连接问题
 * <p>
 * (3) 加载优化:
 * a. 缩略图片: 缩略图和压缩图是不同, 压缩图把压缩图片后大部分的元数据还是保留, 但是缩略图是减小的尺寸, 有很多的元数据被修改了, 用来快速查阅图片, 而一般有 "缩略宽/原始宽" 和 "缩略高/原始高" 两种目标规则, 这里选择使用 "宽" 来作为缩略规则, 同样也是在我们的 COS 控制器中添加即可, 不过暂时只对管理员的查询接口来进行缩略, 不然按照当前的效果来说太过于模糊, 影响用户体验
 * b. 懒载策略:
 * (原始实现)虽然可以使用 img 标签的属性 loading="lazy", 但是这种方案不兼容 IE 浏览器
 * (脚本实现)所以考虑使用 JS 的 Intersection Observer API, 这个接口能检查元素是否进入视图, 将图片的真实 src 替换为一个占位属性(如 data-src), 使用 Intersection Observer 监听图片是否进入视口, 当图片进入视口时将 data-src 的值赋给 src 触发加载, 并且还需要知道懒加载的时机是在页面完全挂载后才运行
 * (三库实现)或者直接使用现成的库实现, 比如 lazysizes 库
 * (组件实现)不过其实更加优美的做法是直接使用 a-image 渐进式加载 #placeholder, 和懒加载技术类似, 先加载低分辨率或低质量的占位资源(如模糊的图片缩略图), 在用户访问或等待期间逐步加载高分辨率的完整资源, 加载完成后再替换掉占位资源
 * c. 内容分发: 内容分发网络, 是通过将图片文件分发到全球各地的节点, 用户访问时从离自己最近的节点获取资源的技术, 常用于文件资源或后端动态请求的网络加速, 也能大幅分摊源站的压力, 支持更多请求同时访问, 是性能提升的利器, 其原理实际上就是在 DNS 解析时干预用户的请求 IP, 在没有分发内容之前需要访问 COS 的源站, 如果分发好了访问图片就只需要访问距离用户较近的节点直接获取缓存的图片即可, 在此基础上就可以降低源站的访问压力, 提高图片的访问速度, 不过很贵...有时间再来做, 注意需要设置
 * (缓存策略) 静态资源设置长期缓存时间减少回源的次数和消耗
 * (防止盗链) 配置 Referer 防盗链保护资源比如仅允许自己的域名可以加载图片
 * (地址名单) 根据需要配置 IP 黑白名单限制不必要的访问
 * (配置加密) 提供 HTTPS 可以提高请求的安全性
 * (监控告警) 配置监控告警快速通知开发人员
 * (分发范围) 目前的项目最多最多在国内全开, 没必要全球全开, 会遭受海外攻击
 * d. 前端缓存:
 * (静态资源使用长期缓存) Cache-Control: public, max-age=31536000 表示缓存一年, 适合存储图片等静态资源
 * (动态内容使用验证缓存) Cache-Control: private, no-cache 表示缓存可被客户端存储, 但每次使用前需要与服务器验证有效性, 适合会动态变化内容的页面, 比如用户个人中心
 * (敏感内容需要禁用缓存) Cache-Control: no-store 表示不允许任何形式的缓存, 适合安全性较高的场景, 比如登录页面, 支付页面
 * (厂商支持) 由于本项目使用的时 COS, 厂家支持在控制台设置对图片请求后写回用户浏览器缓存需求, 有时间就可以实现...不过需要注意的是最好在修改图片后在路径中添加版本号来避免图片修改后无法更新缓存的问题(这会导致重新请求), 可以参考 https://cloud.tencent.com/document/product/228/50114
 * <p>
 * (4) 存储优化:
 * a. 数据沉降: 将长时间未访问的数据自动迁移到低频访问存储, 从而降低存储成本, 不过要注意, 虽然低频存储的存储费用更低, 但是当你要访问低频存储的资源时, 会产生数据取回费用, 所以一般只对几乎不访问的资源进行沉降, 尽量减少取回费用, 分析再沉降, 待实现...
 * b. 清理策略: 在用户清理图片时, 需要借用消息队列把图片的 id 存储为列表, 然后异步进行删除落库和清理图床, 并且如果发生了存储资源不够就可以启用高强度的删除策略, 并且还需要提供一个管理接口提供给管理员来进行清理, 待实现...
 * <p>
 * (5) 加锁优化:
 * a. 我们是对字符串常量池(intern)进行加锁的, 数据并不会及时释放, 如果还要使用本地锁, 可以按需选用另一种方式 —— 采用 ConcurrentHashMap 来存储锁对象
 * b. 本地锁也可以改为分布式锁, 可以基于 Redisson 实现
 * <p>
 * 3. 拓展功能
 * 还有一些额外的拓展功能我们也可以实现
 * (1) 多样搜索, 组合多个图片关键字做关键字索引
 * (2) 以图搜图, 可以使用百度的 API 临时顶一顶, 后续再来更换
 * (3) 以色搜图, 在用户上传图片的瞬间就把主色调提取出来, 然后使用相似度算法进行匹配(欧几里得距离法、余弦相似度、曼哈顿距离、Jaccard 相似度、平均颜色差异、哈希算法、色调饱和亮度差异)
 * (4) 图片编辑, 分为基础编辑(依赖前端组件旋转图片、缩放图片、裁剪图片...)和智能编辑(阿里百炼接口实现拓展图片、文字生图...), 不过这里的智能编辑比较耗时, 可以采用异步处理
 * 其中智能编辑是这个项目的亮点之一, 可以考虑使用比较便宜的它是一站式的大模型开发及应用构建平台, 这里选择阿里云百炼平台 https://www.aliyun.com/product/bailian?utm_content=m_1000400273
 * 可以通过简单的界面操作, 在 5 分钟内开发出一款大模型应用, 并在线体验效果, 创建好应用后, 利用官方提供的 API 或 SDK 就可以快速在项目中使用, 相关的功能文档可以查阅 https://help.aliyun.com/zh/model-studio/what-is-model-studio
 * 可以依据文档来获取免费额度 https://help.aliyun.com/zh/model-studio/new-free-quota?utm_content=m_1000400407
 * 另外, 由于这种类型的 API 比较耗时, 仅支持异步调用方式, 并且一般推荐使用前端轮询而不是后端轮询(同步调用会导致服务器线程长时间被单个任务占用, 限制了并发处理能力, 增加了超时和系统崩溃的风险, 对于大型团队来说, 前后端同时对异步进行处理其实才是更好的选择)
 * 开通过程如下:
 * a. 前往控制台开通免费额度, 并且确保您的账户余额不为 ￥0.00 https://bailian.console.aliyun.com/?utm_content=m_1000400275#/home
 * b. 然后需要获取阿里云的 API Key, 可以参考这篇文档 https://help.aliyun.com/zh/model-studio/get-api-key?utm_content=m_1000400408
 * (5) 防止盗链, 图片还需要防止盗链...
 * ）
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@SuppressWarnings("ALL")
@RestController // 返回值默认为 json 类型
@RequestMapping("/picture")
@Slf4j
public class PictureController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /// 依赖注入 ///

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

    /**
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /// 管理接口 ///

    @Operation(summary = "👑添加图片网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<Picture> adminPictureAdd(@RequestBody PictureAddRequest pictureAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureAdd(pictureAddRequest)); // 可以直接绕过 COS 进行添加落库
    }

    @Operation(summary = "👑删除图片网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminPictureDelete(@RequestBody PictureDeleteRequest pictureDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureDelete(pictureDeleteRequest)); // TODO: 实际上管理员删除接口最重要的一点就是可以直接清理 COS 上的图片, 但是普通用户只是去除数据库中的关联而已
    }

    @Operation(summary = "👑更新图片网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<Picture> adminPictureUpdate(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureUpdate(pictureUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "👑查询图片网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<Picture>> adminPictureSearch(@RequestBody PictureSearchRequest pictureSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureSearch(pictureSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息, 同时这个接口也是实时的, 对于管理员修改状态后实时刷新更加友好
    }

    @Operation(summary = "👑审核图片网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/review")
    public BaseResponse<Boolean> adminPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest) {
        log.debug("本次需要审核的报文 {}", pictureReviewRequest);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureReview(pictureReviewRequest.getId(), pictureReviewRequest.getReviewStatus(), pictureReviewRequest.getReviewMessage())); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    @Operation(summary = "👑图片批量爬取网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/batch")
    public BaseResponse<Integer> adminPictureBatch(@RequestBody PictureBatchRequest pictureBatchRequest) {
        return TheResult.success(
                CodeBindMessageEnums.SUCCESS,
                pictureService.pictureBatch(
                        pictureBatchRequest.getSearchText(),
                        pictureBatchRequest.getSearchCount(),
                        pictureBatchRequest.getName(),
                        pictureBatchRequest.getCategory(),
                        pictureBatchRequest.getIntroduction(),
                        pictureBatchRequest.getTags()
                )
        );
    }

    /// 普通接口 ///

    @Operation(summary = "上传图片网络接口")
    @SaCheckLogin
    @SaCheckPermission({"picture:upload"})
    @PostMapping("/upload")
    public BaseResponse<PictureVO> pictureUpload(
            @RequestParam(value = "pictureId", required = false) Long pictureId,
            @RequestParam(value = "spaceId", required = false) Long spaceId,
            @RequestParam(value = "pictureCategory", required = false, defaultValue = PictureConstant.DEFAULT_CATEGORT) String pictureCategory,
            @RequestParam(value = "pictureName", required = false, defaultValue = PictureConstant.DEFAULT_NAME) String pictureName,
            @RequestParam(value = "pictureIntroduction", required = false, defaultValue = PictureConstant.DEFAULT_INTRODUCTION) String pictureIntroduction,
            @RequestParam(value = "pictureTags", required = false) String pictureTags,
            @RequestParam(value = "pictureFileUrl", required = false) String pictureFileUrl,
            @RequestPart(value = "pictureFile", required = false) MultipartFile multipartFile
    ) {
        System.out.println(spaceId);
        // 如果有传递空间标识就需要检查该用户是否有权限上传图片
        if (spaceId != null) {
            Space space = spaceService.spaceSearchById(spaceId); // 获取空间
            ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "不存在该私有空间无法上传图片");

            if (SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF) {
                log.debug("该图片需要上传到私有空间");
                Space selfSpace = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnum.SELF); // 获取私有空间
                ThrowUtils.throwIf(space.getUserId() != userService.userGetCurrentLonginUserId(), CodeBindMessageEnums.NOT_FOUND_ERROR, "该私有空间不属于您");
            } else if (SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.COLLABORATIVE) {
                log.debug("该图片需要上传到协作空间");
                Space collaborativeSpace = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnum.SELF); // 获取协作空间
                // TODO: 利用权限码值集合来判断能否上传到协作空间就可以, 这里无需判断
            } else {
                ThrowUtils.throwIf(true, CodeBindMessageEnums.PARAMS_ERROR, "未知的空间类型");
            }
        }

        // 上传图片
        Picture picture = pictureService.pictureUpload(pictureId, spaceId, pictureCategory, pictureName, pictureIntroduction, pictureTags, pictureFileUrl, multipartFile);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, PictureVO.removeSensitiveData(picture).setUserVO(UserVO.removeSensitiveData(userService.userSearchById(picture.getUserId()))));
    }

    @Operation(summary = "销毁图片网络接口")
    @SaCheckLogin
    @SaCheckPermission({"picture:delete"})
    @PostMapping("/destroy")
    public BaseResponse<Boolean> pictureDestroy(@RequestBody PictureDestroyRequest pictureDestroyRequest) {
        // 如果图片有所属空间则需要检查该用户是否有权限上传图片
        Long pictureId = pictureDestroyRequest.getId();
        Picture picture = pictureService.pictureSearchById(pictureId);
        Long spaceId = picture.getSpaceId();
        if (spaceId != null) {
            Space space = spaceService.spaceSearchById(spaceId);
            if (space != null) {
                ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && space.getUserId() != userService.userGetCurrentLonginUserId(), CodeBindMessageEnums.NO_ROLE_ERROR, "无法销毁图片, 因为该图片属于他人得私有空间");
                // 如果走到这里说明就是协作空间, 这种情况下交给 SpaceUserAuthManger 就可以
            }
        }

        // 删除文件
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureUnLink(pictureId));
    }

    @Operation(summary = "查找图片网络接口")
    @SaCheckLogin
    @SaCheckPermission({"picture:view"})
    // @CacheSearchOptimization(ttl = 60)
    @PostMapping("/query")
    public BaseResponse<Page<PictureVO>> pictureQuery(@RequestBody PictureQueryRequest pictureQueryRequest) {
        // 先把搜索实例请求构建出来
        var pictureSearchRequest = new PictureSearchRequest();
        BeanUtils.copyProperties(pictureQueryRequest, pictureSearchRequest);

        // 如果用户只搜索自己的图片则允许不通过审核就可以查看
        Long id = pictureQueryRequest.getId();
        if (id != null) {
            Picture picture = pictureService.pictureSearchById(id);
            ThrowUtils.throwIf(picture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "图片不存在");

            Long userId = userService.userGetCurrentLonginUserId();
            if (picture.getUserId() == userId) {
                PictureVO pictureVO = PictureVO.removeSensitiveData(picture);
                pictureVO.setUserVO(UserVO.removeSensitiveData(userService.userSearchById(userId)));
                Page<PictureVO> page = PageUtils.singlePage(pictureVO);
                return TheResult.success(CodeBindMessageEnums.SUCCESS, page);
            }
        }

        // 用户可能不传递图片 id 就进行查看, 有可能就会传递 spaceId
        Long spaceId = pictureQueryRequest.getSpaceId();
        if (spaceId == null) { // 用户只能看到审核通过的公共图库图片
            pictureSearchRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getCode());
        } else { // 有可能访问私有空间或协作空间
            // 如果是私有空间就需要检查是不是当前登录用户的
            Space space = spaceService.spaceSearchById(spaceId);
            ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && space.getId() != spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnum.SELF).getId(), CodeBindMessageEnums.PARAMS_ERROR, "这个私有空间不是您的, 您没有访问该私有空间的权力");
            pictureSearchRequest.setSpaceId(spaceId);
        }

        // 最终返回查找后的结果
        return TheResult.success(CodeBindMessageEnums.SUCCESS, PictureVO.removeSensitiveData(pictureService.pictureSearch(pictureSearchRequest)));
    }

    @Operation(summary = "获取当前后端支持图片类别网络接口")
    @SaCheckLogin
    @GetMapping("/categorys")
    public BaseResponse<List<String>> pictureCategorys() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureGetCategorys());
    }

    @Operation(summary = "利用某个图片的唯一标识来搜索相似的图片")
    @SaCheckLogin
    @PostMapping("/search/picture")
    public BaseResponse<List<ImageSearchResult>> pictureSearchPicture(@RequestBody PictureSearchPictureRequest pictureSearchPictureRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureGetSimilarPictureList(pictureSearchPictureRequest.getPictureId()));
    }

    @Operation(summary = "利用某个图片的唯一标识来搜索同色的图片")
    @SaCheckLogin
    @PostMapping("/search/color")
    public BaseResponse<List<PictureVO>> pictureSearchColor(@RequestBody PictureSearchColorRequest pictureSearchColorRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, PictureVO.removeSensitiveData(pictureService.pictureGetSameColorPictureList(pictureSearchColorRequest.getPictureId())));
    }

    @Operation(summary = "创建智能绘画任务")
    @SaCheckLogin
    @PostMapping("/out_painting/create_task")
    public BaseResponse<CreateOutPaintingTaskResponse> pictureOutPaintingCreateTask(@RequestBody PictureCreateOutPaintingTaskRequest pictureCreateOutPaintingTaskRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureCreateOutPaintingTask(pictureCreateOutPaintingTaskRequest.getPictureId(), pictureCreateOutPaintingTaskRequest.getParameters()));
    }

    @Operation(summary = "查询智能绘画任务")
    @SaCheckLogin
    @GetMapping("/out_painting/get_task")
    public BaseResponse<GetOutPaintingTaskResponse> pictureOutPaintingGetTask(@RequestParam("taskId") String taskId) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, pictureService.pictureGetOutPaintingTask(taskId));
    }

}

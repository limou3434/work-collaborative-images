package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnum;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.*;
import cn.com.edtechhub.workcollaborativeimages.model.vo.SpaceVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 空间控制层
 * 1. 基础实现
 * 由于一张图片只能属于一个空间, 因此在图片表 picture 中新增字段 spaceId, 实现图片与空间的关联, 同时增加索引以提高查询性能,
 * (1) 公共图库中的图片无需登录就能查看, 任何人都可以访问, 不需要进行用户认证或成员管理
 * (2) 私有空间则要求用户登录, 且访问权限严格控制, 通常只有拥有者才能查看或修改空间内容
 * (3) 公共图库没有额度限制, 私有空间会有图片大小、数量等方面的限制, 从而管理用户的存储资源和空间配额, 而公共图库完全不受这些限制
 * (4) 公有图库需要经过审核, 但是私有图库没有审核的说法, 只需要是拥有者就可以看到
 * (5) space 私有图库最终会在远端 COS 中使用 space 目录来存储, 和公有图库中的 public 不同
 * <p>
 * 2. 协作空间
 * 协作空间是上述两个功能的衍生, 需要实现以下目标:
 * (1) 数据关联表及管理服务
 * (2) 使用 Sa-token 框架实现 RBAC 角色权限控制以及对应的操作服务
 * a. 基于 用户、角色、权限 三个对象的角色访问控制, 一个用户可以有多个角色, 一个角色可以有多个权限
 * b. 因此正常来说标准实现需要 5 张表: 用户表、角色表、权限表、用户角色关联表、角色权限关联表
 * (3) 使用 Apache ShardingSphere 框架实现动态分表, 对高级用户的协作空间进行单独表维护
 * 3. 同步编辑
 * (1) 同步协作的原理
 * a. 这其实是一种"事件驱动"的架构设计思想
 * b. 协作编辑中的每个用户动作本质上是事件, 执行动作时会产生事件并提交给服务器, 服务器收到事件后, 会转发给其他用户, 其他用户收到事件后, 就要作为事件的消费者来处理事件
 * c. 事件驱动模型的主要优点在于 "解耦、异步、实时、并发" 上
 * d. 但是由于并发的问题, 就会导致实践顺序是乱序的, 容易导致多个用户在一起协作的过程中发生混乱(并发编辑冲突问题), 因此最省的解决方案就是允许多用户实时查看, 但不允许多用户同时编辑
 * e. 最终我们需要设计的事件如下:
 * 事件触发者(A 用户的操作)  	事件类型(发送消息)	    事件消费者(其他用户的处理）
 * 用户 A 建立连接加入编辑    	INFO	            显示 "用户 A 加入编辑" 的通知
 * 用户 A 进入编辑状态      	ENTER_EDIT	        锁定编辑状态, 其他用户界面显示 "当前用户 A 正在编辑图片"
 * 用户 A 执行编辑操作  	    EDIT_ACTION	        "放大 / 缩小 / 左旋 / 右旋" 当前图片
 * 用户 A 退出编辑状态  	    EXIT_EDIT	        解锁编辑状态, 其他用户界面显示 "可以进入编辑状态"
 * 用户 A 断开‌连接离开编辑	    INFO	            显示 "用户 A 离开编辑" 的通知
 * 用户 A 发送错误消息	        ERROR	            显示错误消息的通知 "发生意外的错误"
 * f. 如果要提高协作性, 就可以考虑引入协同算法, 比如 OT 算法就是一种支持分布式系统的多用户实时编辑核心算法, 主要就是通过 "操作、转化、因果一致性" 来解决并发编辑冲突问题, 不过本项目暂时用不到
 * g. 甚至如果您还想解决离线文档问题可以考虑引入 CRDT 算法, 不过本项目暂时用不到
 * (2) 本项目将会使用 WebSocket 实现同步协作服务, 和传统的 HTTP 请求-响应模式不同, WebSocket 是一条 "常开隧道", 连接的双方可以随时发送和接收数据, 而不需要不断建立和关闭连接, 最为形象的说法就是, HTTP 像是 "外卖", WebSocket 像是 "电话", 一般都会用于:
 * a. 即时通讯(聊天软件、实时协作工具)
 * b. 实时数据(股票行情、体育比赛比分)
 * c. 在线游戏(多人实时互动)
 * d. 物联设备(设备状态实时传输)
 * e. 协同编辑(像语雀这样的多人协作编辑)
 * 而实际上 WebSocket 是建立在 HTTP 基础之上的, 因此可以说是一个 HTTP 升级协议,
 * 需要 HTTP 来发起握手建立链接作为前提, 表明希望切换协议, 如果服务器支持 WebSocket 就会放回一个 101 的状态码表示切换协议成功,
 * 握手完毕后 HTTP 的作用就会结束, 通信就会按照 WebSocket 的协议进行通信, 直到断开连接
 * 当然发起 WebSocket 也需要进行权限认证, 只有属于协作空间中的成员才能进行通信
 * 对于 Spring Boot 项目, 主要有 "WebSocket(基于 WebSocketHandler 的原生实现)、WebSocket + STOMP + SockJS、WebFlux + Reactive WebSocket"
 * a. 对于大多数简单实时推送功能, 选用 WebSocket
 * b. 对于复杂的聊天室和协同系统, 选用 WebSocket + STOMP + SockJS
 * c. 对于高并‌发低延迟数据流推送, 选用 WebFlux + Reactive WebSocket
 * 本项目我们选用原生的 WebSocket 实现:
 * a. 定义图片编辑请求消息
 * b. 定义图片编辑响应消息
 * c. 定义图片编辑消息类型枚举
 * d. 定义图片编辑操作类型枚举
 * e. 设置关于鉴权的拦截器
 * d. 实际做消息收发的处理器
 * e. 最后得到 WebSocket 管理器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@RestController // 返回值默认为 json 类型
@RequestMapping("/space")
@Slf4j
public class SpaceController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /// 依赖注入 ///

    /**
     * 注入用户服务依赖
     */
    @Resource
    UserService userService;

    /**
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /// 管理接口 ///

    @Operation(summary = "👑添加空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<Space> adminSpaceAdd(@RequestBody SpaceAddRequest spaceAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceAdd(spaceAddRequest));
    }

    @Operation(summary = "👑删除空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminSpaceDelete(@RequestBody SpaceDeleteRequest spaceDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(spaceDeleteRequest));
    }

    @Operation(summary = "👑更新空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<Space> adminSpaceUpdate(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceUpdate(spaceUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "👑查询空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<Space>> adminSpaceSearch(@RequestBody SpaceSearchRequest spaceSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceSearch(spaceSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///

    @Operation(summary = "获取空间等级描述网络接口")
    @SaCheckLogin
    @PostMapping("/level")
    public BaseResponse<List<SpaceLevelInfo>> spaceLevel() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceGetLevelInfo());
    }

    @Operation(summary = "根据类型来创建当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<SpaceVO> spaceCreate(@RequestBody SpaceCreateRequest spaceCreateRequest) {
        Integer spaceType = spaceCreateRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        ThrowUtils.throwIf(spaceTypeEnum == null, CodeBindMessageEnums.PARAMS_ERROR, "不存在该空间类型");
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum) != null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个当前登录用户仅能为自己创建一个" + spaceTypeEnum.getDescription());

        Space space = spaceService.spaceSetCurrentLoginUserSpace(spaceTypeEnum, SpaceLevelEnum.COMMON, spaceCreateRequest.getSpaceName());
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

    @Operation(summary = "根据类型来销毁当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
    public BaseResponse<Boolean> spaceDestroy(@RequestBody SpaceDestroyRequest spaceDestroyRequest) {
        Integer spaceType = spaceDestroyRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行销毁");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        boolean result = spaceService.spaceDelete(new SpaceDeleteRequest().setId(space.getId()));
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "根据类型来编辑当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<SpaceVO> spaceEdit(@RequestBody SpaceEditRequest spaceEditRequest) {
        Integer spaceType = spaceEditRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行编辑");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        // TODO: 目前最多就修改个名字, 后续可以添加修改空间类型或是修改空间等级, 或者是提供捐赠公共空间模式
        Space myspace = spaceService.spaceUpdate(
                new SpaceUpdateRequest()
                        .setId(space.getId())
                        .setName(spaceEditRequest.getSpaceName())
        );
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(myspace));
    }

    @Operation(summary = "根据类型来查找当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/query")
    public BaseResponse<SpaceVO> spaceQuery(@RequestBody SpaceQueryRequest spaceEditRequest) {
        Integer spaceType = spaceEditRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行查找");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

    @Operation(summary = "根据具体的空间标识值来查找指定的协作空间网络接口, 注意需要这种查询只能查看当前用户加入的协作空间")
    @SaCheckLogin
    @SaCheckPermission({"picture:view"})
    @PostMapping("/query/id")
    public BaseResponse<SpaceVO> spaceQueryById(@RequestBody SpaceQueryByIdRequest spaceQueryByIdRequest) {
        Long spaceId = spaceQueryByIdRequest.getSpaceId();
        Space space = spaceService.spaceSearchById(spaceId);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该空间不存在");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && space.getUserId() != userService.userGetCurrentLonginUserId(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户无法查看该私有空间");
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

}

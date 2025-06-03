package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author ljp
 * @description 针对表【space(空间表)】的数据库操作Service
 * @createDate 2025-05-16 08:17:55
 */
public interface SpaceService extends IService<Space> {

    /// 基础服务(添加/更新请求具有转化方法, 删除只依赖 id 进行删除, 查询依赖构造查询对象) ///

    /**
     * 空间添加服务
     */
    Space spaceAdd(SpaceAddRequest spaceAddRequest);

    /**
     * 空间删除服务
     */
    Boolean spaceDelete(SpaceDeleteRequest spaceDeleteRequest);

    /**
     * 空间更新服务
     */
    Space spaceUpdate(SpaceUpdateRequest spaceUpdateRequest);

    /**
     * 空间查询服务
     */
    Page<Space> spaceSearch(SpaceSearchRequest spaceSearchRequest);

    /// 其他服务 ///

    /**
     * 根据标识查询单个图片服务
     */
    Space spaceSearchById(Long id);

    /**
     * 空间根据图片来增加图库存量服务(若图片有所属空间的话)
     */
    void spaceCheckAndIncreaseCurrent(Picture picture);

    /**
     * 空间根据图片来减少图库存量服务(若图片有所属空间的话)
     */
    void spaceCheckAndDecreaseCurrent(Picture picture);

    /**
     * 空间获取不同等级的元信息服务
     */
    List<SpaceLevelInfo> spaceGetLevelInfo();

    /**
     * 获取当前登陆用户的专属空间(私有空间/专属空间)服务
     */
    Space spaceGetCurrentLoginUserSelfSpace(Integer spaceType);

}

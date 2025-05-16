package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ljp
 * @description 针对表【space(空间表)】的数据库操作Service
 * @createDate 2025-05-16 08:17:55
 */
public interface SpaceService extends IService<Space> {

    /**
     * 空间添加服务
     */
    Space spaceAdd(AdminSpaceAddRequest adminSpaceAddRequest);

    /**
     * 空间删除服务
     */
    Boolean spaceDelete(AdminSpaceDeleteRequest adminSpaceDeleteRequest);

    /**
     * 空间更新服务
     */
    Space spaceUpdate(AdminSpaceUpdateRequest adminSpaceUpdateRequest);

    /**
     * 空间查询服务
     */
    Page<Space> spaceSearch(AdminSpaceSearchRequest adminSpaceSearchRequest);

}

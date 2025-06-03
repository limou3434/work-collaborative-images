package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ljp
 * @description 针对表【space_spaceSpaceUser(空间空间用户关联关联表)】的数据库操作Service
 * @createDate 2025-06-03 13:21:29
 */
public interface SpaceUserService extends IService<SpaceUser> {

    /// 基础服务(添加/更新请求具有转化方法, 删除只依赖 id 进行删除, 查询依赖构造查询对象) ///

    /**
     * 空间用户关联添加服务
     */
    SpaceUser spaceUserAdd(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 空间用户关联删除服务
     */
    Boolean spaceUserDelete(SpaceUserDeleteRequest spaceUserDeleteRequest);

    /**
     * 空间用户关联更新服务
     */
    SpaceUser spaceUserUpdate(SpaceUserUpdateRequest spaceUserUpdateRequest);

    /**
     * 空间用户关联查询服务
     */
    Page<SpaceUser> spaceUserSearch(SpaceUserSearchRequest spaceUserSearchRequest);

    /// 其他服务 ///

    /**
     * 根据标识查询单个空间用户关联服务
     */
    SpaceUser spaceUserSearchById(Long id);

}

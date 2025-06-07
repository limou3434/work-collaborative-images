package cn.com.edtechhub.workcollaborativeimages.model.vo;

import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间用户关联脱敏
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceUserVO implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 非常重要的注解, 只转换 Long, 为字符串, 避免前端 JS 精度不行导致获取到错误的 ID
    private Long id;

    /**
     * 空间 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 非常重要的注解, 只转换 Long, 为字符串, 避免前端 JS 精度不行导致获取到错误的 ID
    private Long spaceId;

    /**
     * 用户 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 非常重要的注解, 只转换 Long, 为字符串, 避免前端 JS 精度不行导致获取到错误的 ID
    private Long userId;

    /**
     * 空间角色: 0-viewer 1-editor 2-manger
     */
    private Integer spaceRole;

    /**
     * 用户信息
     */
    private UserVO userVO;

    /**
     * 空间信息
     */
    private SpaceVO spaceVO;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 脱敏方法
     */
    static public SpaceUserVO removeSensitiveData(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        var spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        return spaceUserVO;
    }

    /**
     * 脱敏方法(列表脱敏)
     */
    public static List<SpaceUserVO> removeSensitiveData(List<SpaceUser> spaceUserList) {
        if (spaceUserList == null) {
            return null;
        }
        return spaceUserList
                .stream()
                .map(SpaceUserVO::removeSensitiveData)
                .collect(Collectors.toList());
    }

    /**
     * 脱敏方法(分页脱敏)
     */
    public static Page<SpaceUserVO> removeSensitiveData(Page<SpaceUser> spaceUserPage) {
        if (spaceUserPage == null) {
            return null;
        }
        var spaceUserList = spaceUserPage
                .getRecords()
                .stream()
                .map(SpaceUserVO::removeSensitiveData)
                .collect(Collectors.toList());
        var newUserPage = new Page<SpaceUserVO>();
        newUserPage.setCurrent(spaceUserPage.getCurrent());
        newUserPage.setSize(spaceUserPage.getSize());
        newUserPage.setTotal(spaceUserPage.getTotal());
        newUserPage.setRecords(spaceUserList);
        return newUserPage;
    }

}


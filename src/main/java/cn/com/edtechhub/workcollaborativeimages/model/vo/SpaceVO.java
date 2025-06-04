package cn.com.edtechhub.workcollaborativeimages.model.vo;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String name;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer level;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间(受时区影响)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间(受时区影响)
     */
    private LocalDateTime updateTime;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 脱敏方法
     */
    static public SpaceVO removeSensitiveData(Space space) {
        if (space == null) {
            return null;
        }
        var spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);
        return spaceVO;
    }

    /**
     * 脱敏方法(列表脱敏)
     */
    public static List<SpaceVO> removeSensitiveData(List<Space> spaceList) {
        if (spaceList == null) {
            return null;
        }
        return spaceList
                .stream()
                .map(SpaceVO::removeSensitiveData)
                .collect(Collectors.toList());
    }

    /**
     * 脱敏方法(分页脱敏)
     */
    public static Page<SpaceVO> removeSensitiveData(Page<Space> spacePage) {
        if (spacePage == null) {
            return null;
        }
        var spaceList = spacePage
                .getRecords()
                .stream()
                .map(SpaceVO::removeSensitiveData)
                .collect(Collectors.toList());
        var newSpacePage = new Page<SpaceVO>();
        newSpacePage.setCurrent(spacePage.getCurrent());
        newSpacePage.setSize(spacePage.getSize());
        newSpacePage.setTotal(spacePage.getTotal());
        newSpacePage.setRecords(spaceList);
        return newSpacePage;
    }

}

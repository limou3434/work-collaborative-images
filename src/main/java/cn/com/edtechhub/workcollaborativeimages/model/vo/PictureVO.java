package cn.com.edtechhub.workcollaborativeimages.model.vo;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
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
 * 图片脱敏
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class PictureVO implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 缩略图
     */
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签(JSON 数组)
     */
    private String tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间 id(为空表示公共空间)
     */
    private Long spaceId;

    /**
     * 创建时间(受时区影响)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间(受时区影响)
     */
    private LocalDateTime updateTime;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 状态: 0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 脱敏方法
     */
    public static PictureVO removeSensitiveData(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        return pictureVO;
    }

    /**
     * 脱敏方法(列表脱敏)
     */
    public static List<PictureVO> removeSensitiveData(List<Picture> pictureList) {
        if (pictureList == null) {
            return null;
        }
        return pictureList
                .stream()
                .map(PictureVO::removeSensitiveData)
                .collect(Collectors.toList());
    }

    /**
     * 脱敏方法(分页脱敏)
     */
    public static Page<PictureVO> removeSensitiveData(Page<Picture> picturePage) {
        if (picturePage == null) {
            return null;
        }
        var pictureList = picturePage
                .getRecords()
                .stream()
                .map(PictureVO::removeSensitiveData)
                .collect(Collectors.toList());
        var newPicturePage = new Page<PictureVO>();
        newPicturePage.setCurrent(picturePage.getCurrent());
        newPicturePage.setSize(picturePage.getSize());
        newPicturePage.setTotal(picturePage.getTotal());
        newPicturePage.setRecords(pictureList);
        return newPicturePage;
    }

}

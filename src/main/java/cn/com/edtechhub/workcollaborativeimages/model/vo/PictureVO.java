package cn.com.edtechhub.workcollaborativeimages.model.vo;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片脱敏
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
public class PictureVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

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
     * 是否删除(0 为未删除, 1 为已删除)
     */
    private Integer deleted;

    /**
     * 创建时间(受时区影响)
     */
    private Date createTime;

    /**
     * 更新时间(受时区影响)
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 脱敏方法
     */
    static public PictureVO removeSensitiveData(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        return pictureVO;
    }

}

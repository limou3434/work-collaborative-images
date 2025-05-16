package cn.com.edtechhub.workcollaborativeimages.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图片表
 *
 * @TableName picture
 */
@TableName(value = "picture")
@Data
public class Picture implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID) // 手动添加雪花算法
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
     * 状态: 0-待审; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 是否删除(0 为未删除, 1 为已删除)
     */
    @TableLogic // 手动修改为逻辑删除
    private Integer deleted;

    /**
     * 创建时间(受时区影响)
     */
    private LocalDateTime createTime; // 更换为更加安全的时间类型

    /**
     * 更新时间(受时区影响)
     */
    private LocalDateTime updateTime; // 更换为更加安全的时间类型

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Picture other = (Picture) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
                && (this.getThumbnailUrl() == null ? other.getThumbnailUrl() == null : this.getThumbnailUrl().equals(other.getThumbnailUrl()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getIntroduction() == null ? other.getIntroduction() == null : this.getIntroduction().equals(other.getIntroduction()))
                && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
                && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
                && (this.getPicSize() == null ? other.getPicSize() == null : this.getPicSize().equals(other.getPicSize()))
                && (this.getPicWidth() == null ? other.getPicWidth() == null : this.getPicWidth().equals(other.getPicWidth()))
                && (this.getPicHeight() == null ? other.getPicHeight() == null : this.getPicHeight().equals(other.getPicHeight()))
                && (this.getPicScale() == null ? other.getPicScale() == null : this.getPicScale().equals(other.getPicScale()))
                && (this.getPicFormat() == null ? other.getPicFormat() == null : this.getPicFormat().equals(other.getPicFormat()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getSpaceId() == null ? other.getSpaceId() == null : this.getSpaceId().equals(other.getSpaceId()))
                && (this.getReviewStatus() == null ? other.getReviewStatus() == null : this.getReviewStatus().equals(other.getReviewStatus()))
                && (this.getReviewMessage() == null ? other.getReviewMessage() == null : this.getReviewMessage().equals(other.getReviewMessage()))
                && (this.getReviewTime() == null ? other.getReviewTime() == null : this.getReviewTime().equals(other.getReviewTime()))
                && (this.getReviewerId() == null ? other.getReviewerId() == null : this.getReviewerId().equals(other.getReviewerId()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getThumbnailUrl() == null) ? 0 : getThumbnailUrl().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIntroduction() == null) ? 0 : getIntroduction().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getPicSize() == null) ? 0 : getPicSize().hashCode());
        result = prime * result + ((getPicWidth() == null) ? 0 : getPicWidth().hashCode());
        result = prime * result + ((getPicHeight() == null) ? 0 : getPicHeight().hashCode());
        result = prime * result + ((getPicScale() == null) ? 0 : getPicScale().hashCode());
        result = prime * result + ((getPicFormat() == null) ? 0 : getPicFormat().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getSpaceId() == null) ? 0 : getSpaceId().hashCode());
        result = prime * result + ((getReviewStatus() == null) ? 0 : getReviewStatus().hashCode());
        result = prime * result + ((getReviewMessage() == null) ? 0 : getReviewMessage().hashCode());
        result = prime * result + ((getReviewTime() == null) ? 0 : getReviewTime().hashCode());
        result = prime * result + ((getReviewerId() == null) ? 0 : getReviewerId().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", url=" + url +
                ", thumbnailUrl=" + thumbnailUrl +
                ", name=" + name +
                ", introduction=" + introduction +
                ", category=" + category +
                ", tags=" + tags +
                ", picSize=" + picSize +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", picScale=" + picScale +
                ", picFormat=" + picFormat +
                ", userId=" + userId +
                ", spaceId=" + spaceId +
                ", reviewStatus" + reviewStatus +
                ", reviewMessage" + reviewMessage +
                ", reviewTime" + reviewTime +
                ", reviewerId" + reviewerId +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", serialVersionUID=" + serialVersionUID +
                "]";
        return sb;
    }

}

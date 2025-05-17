package cn.com.edtechhub.workcollaborativeimages.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 空间表
 *
 * @TableName space
 */
@TableName(value = "space")
@Data
public class Space implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别: 0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

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
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

    /**
     * 是否删除(0 为未删除, 1 为已删除)
     */
    @TableLogic // 手动修改为逻辑删除
    private Integer deleted;

    /**
     * 创建时间(受时区影响)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间(受时区影响)
     */
    private LocalDateTime updateTime;

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
        Space other = (Space) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getSpaceName() == null ? other.getSpaceName() == null : this.getSpaceName().equals(other.getSpaceName()))
                && (this.getSpaceLevel() == null ? other.getSpaceLevel() == null : this.getSpaceLevel().equals(other.getSpaceLevel()))
                && (this.getMaxSize() == null ? other.getMaxSize() == null : this.getMaxSize().equals(other.getMaxSize()))
                && (this.getMaxCount() == null ? other.getMaxCount() == null : this.getMaxCount().equals(other.getMaxCount()))
                && (this.getTotalSize() == null ? other.getTotalSize() == null : this.getTotalSize().equals(other.getTotalSize()))
                && (this.getTotalCount() == null ? other.getTotalCount() == null : this.getTotalCount().equals(other.getTotalCount()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpaceName() == null) ? 0 : getSpaceName().hashCode());
        result = prime * result + ((getSpaceLevel() == null) ? 0 : getSpaceLevel().hashCode());
        result = prime * result + ((getMaxSize() == null) ? 0 : getMaxSize().hashCode());
        result = prime * result + ((getMaxCount() == null) ? 0 : getMaxCount().hashCode());
        result = prime * result + ((getTotalSize() == null) ? 0 : getTotalSize().hashCode());
        result = prime * result + ((getTotalCount() == null) ? 0 : getTotalCount().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", spaceName=").append(spaceName);
        sb.append(", spaceLevel=").append(spaceLevel);
        sb.append(", maxSize=").append(maxSize);
        sb.append(", maxCount=").append(maxCount);
        sb.append(", totalSize=").append(totalSize);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", userId=").append(userId);
        sb.append(", deleted=").append(deleted);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

}

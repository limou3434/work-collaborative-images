package cn.com.edtechhub.workcollaborativeimages.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 空间用户关联表
 *
 * @TableName space_user
 */
@TableName(value ="space_user")
@Data
public class SpaceUser implements Serializable {

    /**
     * 本空间用户关联唯一标识(业务层需要考虑使用雪花算法用户标识的唯一性)
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间标识
     */
    private Long spaceId;

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 空间角色 0-viewer 1-editor 2-manager
     */
    private Integer spaceRole;

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
        SpaceUser other = (SpaceUser) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSpaceId() == null ? other.getSpaceId() == null : this.getSpaceId().equals(other.getSpaceId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getSpaceRole() == null ? other.getSpaceRole() == null : this.getSpaceRole().equals(other.getSpaceRole()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpaceId() == null) ? 0 : getSpaceId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getSpaceRole() == null) ? 0 : getSpaceRole().hashCode());
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
        sb.append(", spaceId=").append(spaceId);
        sb.append(", userId=").append(userId);
        sb.append(", spaceRole=").append(spaceRole);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

}
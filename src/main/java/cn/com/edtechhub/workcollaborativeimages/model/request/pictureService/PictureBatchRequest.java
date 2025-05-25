package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class PictureBatchRequest implements Serializable {

    /**
     * 搜索词语
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer searchCount = 10;

    /**
     * 图片名称
     */
    private String name = PictureConstant.DEFAULT_NAME;

    /**
     * 简介
     */
    private String introduction = PictureConstant.DEFAULT_INTRODUCTION;

    /**
     * 分类
     */
    private String category = PictureConstant.DEFAULT_CATEGORT;

    /**
     * 标签(JSON 数组)
     */
    private String tags;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}

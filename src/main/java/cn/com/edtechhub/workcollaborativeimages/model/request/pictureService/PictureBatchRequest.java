package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import lombok.Data;

import java.io.Serializable;

@Data
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
     * 名称前缀
     */
    private String namePrefix = "默认图片";

    /**
     * 图片类别
     */
    private String category = "默认类别";

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
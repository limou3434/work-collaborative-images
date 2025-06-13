package cn.com.edtechhub.workcollaborativeimages.manager.search;

import lombok.Data;

/**
 * 接受 API 的返回值
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;

}

package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * COS 管理器
 * 主要存放一些快速对 COS 做操作的方法
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class CosManager {

    /**
     * 注入客户端监听配置依赖
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 注入客户端依赖
     */
    @Resource
    private COSClient cosClient;

    /**
     * 上传资源对象
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载资源对象
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

}

package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.PictureReviewStatusEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.manager.CosManager;
import cn.com.edtechhub.workcollaborativeimages.mapper.PictureMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UploadPictureResult;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-05-04 22:19:43
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    /**
     * 注入事务管理依赖
     */
    @Resource
    TransactionTemplate transactionTemplate;

    /**
     * 注入 COS 管理器依赖
     */
    @Resource
    CosManager cosManager;

    /**
     * 注入用户服务依赖
     */
    @Resource
    UserService userService;

    /**
     * 注入空间服务依赖
     */
    @Resource
    SpaceService spaceService;

    @Override
    @LogParams
    public Picture pictureAdd(PictureAddRequest pictureAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        this.checkParameters(pictureAddRequest.getName());

        // 服务实现
        return transactionTemplate.execute(status -> {
            var picture = PictureAddRequest.copyProperties2Entity(pictureAddRequest); // 创建实例
            try {
                boolean result = this.save(picture); // 保存实例的同时利用唯一键约束避免并发问题
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
            } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够因对并发场景
                ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "已经存在该图片, 或者曾经被删除");
            }
            return this.getById(picture.getId());
        });
    }

    @Override
    @LogParams
    public Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "图片删除请求体不能为空");
        Long id = pictureDeleteRequest.getId();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "图片标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "图片标识不合法, 必须是正整数");

        // 操作数据库
        return transactionTemplate.execute(status -> {
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "删除图片失败, 也许该图片不存在或者已经被删除");
            return true;
        });
    }

    @Override
    @LogParams
    public Picture pictureUpdate(PictureUpdateRequest pictureUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureUpdateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = pictureUpdateRequest.getId();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不合法, 必须是正整数");
        this.checkParameters(pictureUpdateRequest.getName());

        // 服务实现
        return transactionTemplate.execute(status -> {
            Picture picture = PictureUpdateRequest.copyProperties2Entity(pictureUpdateRequest);
            this.updateById(picture);
            return this.getById(id);
        });
    }

    @Override
    @LogParams
    public Page<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = pictureSearchRequest.getId();
        String name = pictureSearchRequest.getName();
        ThrowUtils.throwIf(id != null && id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "图片标识不合法");
        ThrowUtils.throwIf(StrUtil.isNotBlank(name) && name.length() > PictureConstant.NAME_LENGTH, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "名字不得小于" + PictureConstant.NAME_LENGTH + "位"));

        // 服务实现
        LambdaQueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureSearchRequest); // 构造查询条件
        Page<Picture> page = new Page<>(pictureSearchRequest.getPageCurrent(), pictureSearchRequest.getPageSize()); // 获取分页对象
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    @LogParams
    public Long pictureGetSpace(Picture picture) {
        return picture.getSpaceId();
    }

    @Override
    @LogParams
    public Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage) {
        // TODO: 简单使用 AI 接口来进行文本审核和图片审核

        // 检查参数
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getStatusDescription(reviewStatus);
        ThrowUtils.throwIf(id == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片 id 不能为空"));
        ThrowUtils.throwIf(reviewStatusEnum == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "需要指定最终的有效审核状态"));

        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "id 所对应的图片不存在"));

        // 已是该状态
        ThrowUtils.throwIf(oldPicture.getReviewStatus().equals(reviewStatus), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "该图片已经处于本次请求的指定状态, 请勿重复审核"));

        // 更新审核状态(注意数据库默认设置为待审状态)
        Picture updatePicture = new Picture();
        updatePicture.setId(id);
        updatePicture.setReviewStatus(reviewStatus);
        updatePicture.setReviewMessage(reviewMessage);
        updatePicture.setReviewerId(Long.valueOf(StpUtil.getLoginId().toString()));
        updatePicture.setReviewTime(LocalDateTime.now());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "审核失败, 无法更新"));
        return true;
    }

    @Override
    @LogParams
    public Integer pictureBatch(String searchText, Integer searchCount, String namePrefix, String category) {
        // 检查参数
        ThrowUtils.throwIf(searchText == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "缺少需要爬取的关键文本"));
        ThrowUtils.throwIf(searchCount == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "缺少需要爬取的图片个数"));
        ThrowUtils.throwIf(searchCount > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "最多 30 条"));

        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document = null;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "获取页面失败"));
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "获取元素失败"));
        }
        Elements imgElementList = div.select("img.mimg");
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.debug("当前链接为空, 已跳过: {}", fileUrl);
                continue;
            }
            // 处理图片上传地址，防止出现转义问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // 上传图片
            try {
                Picture picture = this.pictureUpload(PictureReviewStatusEnum.PASS.getCode(), userService.userGetCurrentLonginUserId(), null, null, category, namePrefix + (uploadCount + 1), null, null, fileUrl, null);
                log.debug("图片上传成功, id = {}", picture.getId());
                uploadCount++;
            } catch (Exception e) {
                log.debug("图片上传失败", e);
                continue;
            }
            if (uploadCount >= searchCount) {
                break;
            }
        }
        return uploadCount;
    }

    @Override
    @LogParams
    public Picture pictureUpload(Integer pictureStatus, Long userId, Long spaceId, Long pictureId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, String pictureFileUrl, MultipartFile multipartFile) {
        // 如果有更新图片的需求, 也就是携带了 id
        if (pictureId != null) {
            boolean exists = this
                    .lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "指定 id 所对应的图片不存在所以无法更新"));
        }

        // 构造要入库的图片信息
        Picture picture = new Picture();
        UploadPictureResult uploadPictureResult = null;
        String uploadPathPrefix = String.format(spaceId == 0 ? "public/%s" : "space/%s", userId); // 构造和用户相关的图片父目录
        if (multipartFile != null) { // 支持对本地文件的上传
            log.debug("支持对本地文件的上传");
            uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, multipartFile); // 执行具体的上传任务
        }
        if (pictureFileUrl != null) { // 支持对远端文件的上传
            log.debug("支持对远端文件的上传");
            uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, pictureFileUrl); // 执行具体的上传任务
        }
        if (pictureId != null) {
            picture = this.getById(pictureId);
        }
        picture.setReviewStatus(pictureStatus);
        PictureReviewStatusEnum status = PictureReviewStatusEnum.getStatusDescription(pictureStatus);
        if (status == PictureReviewStatusEnum.REVIEWING) {
            picture.setReviewMessage("管理员正在审核");
        } else if (status == PictureReviewStatusEnum.NOTODO) {
            picture.setReviewMessage("该图片为私有空间图片无需审核");
        }
        picture.setSpaceId(spaceId);
        picture.setCategory(pictureCategory);
        picture.setTags(StringUtils.isNotBlank(pictureTags) ? pictureTags : null);
        picture.setIntroduction(pictureIntroduction);
        picture.setUserId(userId);
        picture.setName(StringUtils.isNotBlank(pictureName) ? pictureName : "尚无名称");
        if (uploadPictureResult != null) {
            picture.setName(StringUtils.isNotBlank(pictureName) ? pictureName : uploadPictureResult.getPicName());
            log.debug("测试这里的图片名字 {}", picture.getName());
            picture.setUrl(uploadPictureResult.getUrl());
            picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
            picture.setPicSize(uploadPictureResult.getPicSize());
            picture.setPicWidth(uploadPictureResult.getPicWidth());
            picture.setPicHeight(uploadPictureResult.getPicHeight());
            picture.setPicScale(uploadPictureResult.getPicScale());
            picture.setPicFormat(uploadPictureResult.getPicFormat());
        }
        // 校验额度并且增加存量
        if (spaceId != 0) { // TODO: 其实应该提前截获图片的大小的
            spaceService.spaceCheckAndIncreaseCurrent(picture);
        }

        // 执行落库
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));
        Picture newPicture = this.getById(picture.getId());
        log.debug("检查入库后的图片 {}", newPicture);
        return newPicture;
    }

    @Override
    @LogParams
    public List<String> pictureGetCategorys() {
        return Arrays.asList("动漫", "艺术", "表情", "素材", "海报");
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Picture> getQueryWrapper(PictureSearchRequest pictureSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(pictureSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空"));

        // 取得需要查询的参数
        Long id = pictureSearchRequest.getId();
        String name = pictureSearchRequest.getName();
        String introduction = pictureSearchRequest.getIntroduction();
        String category = pictureSearchRequest.getCategory();
        String tags = pictureSearchRequest.getTags();
        Long picSize = pictureSearchRequest.getPicSize();
        Integer picWidth = pictureSearchRequest.getPicWidth();
        Integer picHeight = pictureSearchRequest.getPicHeight();
        Double picScale = pictureSearchRequest.getPicScale();
        String picFormat = pictureSearchRequest.getPicFormat();
        Long userId = pictureSearchRequest.getUserId();
        Long spaceId = pictureSearchRequest.getSpaceId();
        Integer reviewStatus = pictureSearchRequest.getReviewStatus();
        String sortField = pictureSearchRequest.getSortField();
        String sortOrder = pictureSearchRequest.getSortOrder();

        log.debug("用户需要查询的审核状态 {}", reviewStatus);

        // 获取包装器进行返回
        LambdaQueryWrapper<Picture> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, Picture::getId, id)
                .like(StringUtils.isNotBlank(name), Picture::getName, name)
                .like(StringUtils.isNotBlank(introduction), Picture::getIntroduction, introduction)
                .like(StringUtils.isNotBlank(category), Picture::getCategory, category)
                .like(StringUtils.isNotBlank(tags), Picture::getTags, tags)
                .eq(picSize != null, Picture::getPicSize, picSize)
                .eq(picWidth != null, Picture::getPicWidth, picWidth)
                .eq(picHeight != null, Picture::getPicHeight, picHeight)
                .eq(picScale != null, Picture::getPicScale, picScale)
                .eq(picFormat != null, Picture::getPicFormat, picFormat)
                .eq(userId != null, Picture::getUserId, userId)
                .eq(spaceId != null, Picture::getSpaceId, spaceId)
                .eq(reviewStatus != null, Picture::getReviewStatus, reviewStatus)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        Picture::getCreateTime // 默认按照创建时间排序
                )
        ;
        return lambdaQueryWrapper;
    }

    /**
     * 检查添加或更新的参数
     */
    private void checkParameters(String name) {
        ThrowUtils.throwIf(StrUtil.isNotBlank(name) && name.length() > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "名字不得大于" + PictureConstant.NAME_LENGTH + "位字符"));
    }

}





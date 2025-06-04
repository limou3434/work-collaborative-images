package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.PictureReviewStatusEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.manager.AIManager;
import cn.com.edtechhub.workcollaborativeimages.manager.CosManager;
import cn.com.edtechhub.workcollaborativeimages.manager.SearchManager;
import cn.com.edtechhub.workcollaborativeimages.mapper.PictureMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.*;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ColorUtils;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.bean.BeanUtil;
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
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-05-04 22:19:43
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    /// 依赖注入 ///

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
     * 注入 AI 管理器依赖
     */
    @Resource
    AIManager aiManager;

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

    /// 服务实现 ///

    @Override
    @LogParams
    public Picture pictureAdd(PictureAddRequest pictureAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        String name = pictureAddRequest.getName();
        ThrowUtils.throwIf(
                name == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得为空"
        );
        ThrowUtils.throwIf(
                name.length() > PictureConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得大于" + PictureConstant.MAX_NAME_LENGTH + "位字符"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            var picture = PictureAddRequest.copyProperties2Entity(pictureAddRequest); // 创建实例
            // 操作数据库
            try {
                boolean result = this.save(picture); // 保存实例的同时利用唯一键约束避免并发问题
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
            } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够应对并发场景
                ThrowUtils.throwIf(true, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "已经存在该图片, 或者曾经被删除");
            }
            return this.getById(picture.getId());
        });
    }

    @Override
    @LogParams
    public Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = pictureDeleteRequest.getId();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不能为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );

        // 服务实现
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
        String name = pictureUpdateRequest.getName();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不能为空");
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );
        ThrowUtils.throwIf(
                StrUtil.isNotBlank(name) && name.length() > PictureConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得大于" + PictureConstant.MAX_NAME_LENGTH + "位字符"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            Picture picture = PictureUpdateRequest.copyProperties2Entity(pictureUpdateRequest);
            // 操作数据库
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
        ThrowUtils.throwIf(
                id != null && id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间标识不得非法"
        );
        ThrowUtils.throwIf(
                StrUtil.isNotBlank(name) && name.length() > PictureConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得大于" + PictureConstant.MAX_NAME_LENGTH + "位字符"
        );

        // 服务实现
        LambdaQueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureSearchRequest); // 构造查询条件
        Page<Picture> page = new Page<>(pictureSearchRequest.getPageCurrent(), pictureSearchRequest.getPageSize()); // 获取分页对象
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    @LogParams
    public Picture pictureSearchById(Long id) {
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不能为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法"
        );
        return this.getById(id);
    }

    @Override
    @LogParams
    public Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage) {
        // 检查参数
        PictureReviewStatusEnums reviewStatusEnum = PictureReviewStatusEnums.getEnums(reviewStatus);
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "图片 id 不能为空");
        ThrowUtils.throwIf(reviewStatusEnum == null, CodeBindMessageEnums.PARAMS_ERROR, "需要指定最终的有效审核状态");

        // 判断图片是否存在
        Picture oldPicture = this.pictureSearchById(id);
        ThrowUtils.throwIf(oldPicture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "标识所对应的图片不存在");

        // 已是该状态
        ThrowUtils.throwIf(oldPicture.getReviewStatus().equals(reviewStatus), CodeBindMessageEnums.PARAMS_ERROR, "该图片已经处于本次请求的指定状态, 请勿重复审核");

        // TODO: 简单使用 AI 接口来进行文本审核和图片审核

        // 更新审核状态(注意数据库默认设置为待审状态)
        Picture newPicture = new Picture();
        newPicture.setId(id);
        newPicture.setReviewStatus(reviewStatus);
        newPicture.setReviewMessage(reviewMessage);
        newPicture.setReviewerId(userService.userGetCurrentLonginUserId());
        newPicture.setReviewTime(LocalDateTime.now());
        boolean result = this.updateById(newPicture);
        ThrowUtils.throwIf(!result, CodeBindMessageEnums.SYSTEM_ERROR, "审核失败, 无法更新图片状态");
        return true;
    }

    @Override
    @LogParams
    public Picture pictureUpload(Long pictureId, Long spaceId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, String pictureFileUrl, MultipartFile multipartFile) {
        return transactionTemplate.execute(status -> {
            // 准备可能需要的参数
            Long userId = userService.userGetCurrentLonginUserId();
            String uploadPathPrefix = String.format(spaceId == null ? "public/%s" : "space/%s", userId); // 构造和用户相关的图片父目录

            // 如果携带图片先进行上传
            UploadPictureResult uploadPictureResult = null;
            if (multipartFile != null) { // 支持对本地文件的上传
                log.debug("支持对本地文件的上传");
                uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, multipartFile); // 执行具体的上传任务
            }
            if (pictureFileUrl != null) { // 支持对远端文件的上传
                log.debug("支持对远端文件的上传");
                uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, pictureFileUrl); // 执行具体的上传任务
            }

            // 这个上传逻辑即可以作为添加也可以作为修改
            var picture = new Picture();
            if (pictureId == null) { // 添加逻辑
                boolean res = pictureFileUrl == null && multipartFile == null;
                ThrowUtils.throwIf(res, CodeBindMessageEnums.PARAMS_ERROR, "上传图片需要附带图片地址或图片文件");
            } else { // 修改逻辑
                picture = this.pictureSearchById(pictureId);
                ThrowUtils.throwIf(picture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "指定 id 所对应的图片不存在所以无法更新");
                spaceService.spaceCheckAndDecreaseCurrent(picture); // 提前减少私有空间存量
            }

            // 无论是添加还是更新都需要填写实例参数
            picture.setId(pictureId);
            picture.setSpaceId(spaceId);
            picture.setCategory(pictureCategory);
            picture.setName(pictureName);
            picture.setIntroduction(pictureIntroduction);
            picture.setTags(pictureTags);
            picture.setUserId(userId);
            if (uploadPictureResult != null) {
                picture.setUrl(uploadPictureResult.getUrl());
                picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
                picture.setOriginalUrl(uploadPictureResult.getOriginalUrl());
                picture.setPicSize(uploadPictureResult.getPicSize());
                picture.setPicColor(uploadPictureResult.getPicColor());
                picture.setPicWidth(uploadPictureResult.getPicWidth());
                picture.setPicHeight(uploadPictureResult.getPicHeight());
                picture.setPicScale(uploadPictureResult.getPicScale());
                picture.setPicFormat(uploadPictureResult.getPicFormat());
            }

            // 无论是添加还是更新只要不是公共图库的图片都需要增加存量
            if (picture.getSpaceId() != null) {
                picture.setReviewMessage("该图片属于私有用户 " + userId + " 的图片, 无需审核, 若是通过审核则可以上传到公共图库中");
                spaceService.spaceCheckAndIncreaseCurrent(picture);
            }

            // 无论是添加还是更新都需要执行落库
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败");
            return this.getById(picture.getId());
        });
    }

    @Override
    @LogParams
    public Boolean pictureUnLink(Long pictureId) {
        return transactionTemplate.execute(status -> {
            Picture picture = this.pictureSearchById(pictureId);
            Space space = this.pictureGetSpace(picture);
            if (space != null) {
                spaceService.spaceCheckAndDecreaseCurrent(picture);
            }
            return this.pictureDelete(new PictureDeleteRequest().setId(pictureId));
        });
    }

    @Override
    @LogParams
    public Integer pictureBatch(String searchText, Integer searchCount, String name, String category, String introduction, String tags) {
        // 检查参数
        ThrowUtils.throwIf(searchText == null, CodeBindMessageEnums.PARAMS_ERROR, "缺少需要爬取的关键文本");
        ThrowUtils.throwIf(searchCount == null, CodeBindMessageEnums.PARAMS_ERROR, "缺少需要爬取的图片个数");
        ThrowUtils.throwIf(searchCount > PictureConstant.MAX_NAME_LENGTH, CodeBindMessageEnums.PARAMS_ERROR, "最多" + PictureConstant.MAX_NAME_LENGTH + "位字符");

        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document = null;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "获取页面失败");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "获取元素失败");
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
                Picture picture = this.pictureUpload(null, null, category, name + (uploadCount + 1), introduction, tags, fileUrl, null);
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
    public Space pictureGetSpace(Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) return null;
        return spaceService.spaceSearchById(spaceId);
    }

    @Override
    @LogParams
    public PictureReviewStatusEnums pictureGetReviewStatus(Picture picture) {
        return PictureReviewStatusEnums.getEnums(picture.getReviewStatus());
    }

    @Override
    @LogParams
    public List<String> pictureGetCategorys() {
        return Arrays.asList("动漫", "艺术", "表情", "素材", "海报");
    }

    @Override
    @LogParams
    public List<ImageSearchResult> pictureGetSimilarPictureList(Long pictureId) {
        ThrowUtils.throwIf(pictureId == null, CodeBindMessageEnums.PARAMS_ERROR, "图片标识参数不能为空");
        ThrowUtils.throwIf(pictureId <= 0, CodeBindMessageEnums.PARAMS_ERROR, "图片标识参数不合法, 必须为正整数");
        Picture oldPicture = this.pictureSearchById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该图片不存在无法寻找相似的图片");
        return SearchManager.getSimilarPictureList(oldPicture.getOriginalUrl());
    }

    @Override
    @LogParams
    public List<Picture> pictureGetSameColorPictureList(Long pictureId) {
        // 检查参数
        ThrowUtils.throwIf(pictureId == null, CodeBindMessageEnums.PARAMS_ERROR, "图片标识不能为空");
        ThrowUtils.throwIf(pictureId <= 0, CodeBindMessageEnums.PARAMS_ERROR, "图片标识必须为正整数");

        // 查找目标图
        Picture targetPicture = pictureSearchById(pictureId);
        ThrowUtils.throwIf(targetPicture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "不存在该图片");

        // 尝试获取目标图的所属空间, 若为私有空间图片则需要校验权限, 否则只查询公有图库中的图片
        Long spaceId = targetPicture.getSpaceId();
        Long mySpaceId = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnums.SELF).getId();
        PictureSearchRequest searchRequest = new PictureSearchRequest();
        if (spaceId != null) {
            log.debug("该图片具有私有空间为 {}", spaceId);
            ThrowUtils.throwIf(!spaceId.equals(mySpaceId), CodeBindMessageEnums.NO_AUTH_ERROR, "无权限访问该空间");
        } else {
            searchRequest.setReviewStatus(PictureReviewStatusEnums.PASS.getCode());
        }
        searchRequest.setSpaceId(spaceId);

        // 获取所有需要比对的图片页面
        List<Picture> picturesList = this.pictureSearch(searchRequest).getRecords();
        log.debug("获取到的图片页面为 {}", picturesList.stream().map(Picture::getId).collect(Collectors.toList()));

        // 提前解析目标图颜色，避免每次都 decode
        Color targetColor = Color.decode(targetPicture.getPicColor());

        // 为每个图片计算相似度（缓存颜色避免重复 decode）
        Map<Long, Double> similarityMap = new HashMap<>();
        for (Picture picture : picturesList) {
            if (picture.getId().equals(pictureId)) {
                continue; // 跳过自己
            }
            Color contrastColor = Color.decode(picture.getPicColor());
            double similarity = ColorUtils.calculateSimilarity(targetColor, contrastColor);
            similarityMap.put(picture.getId(), similarity);
            log.debug("计算相似度 pictureId={} vs pictureId={}, 相似度为 {}", targetPicture.getId(), picture.getId(), similarity);
        }

        // 过滤并排序（只排其他图片）
        return picturesList.stream()
                .filter(picture -> !picture.getId().equals(pictureId))
                .sorted(Comparator.comparingDouble(p -> similarityMap.getOrDefault(p.getId(), Double.MAX_VALUE)))
                .limit(12)
                .collect(Collectors.toList());
    }

    @Override
    @LogParams
    public CreateOutPaintingTaskResponse pictureCreateOutPaintingTask(Long pictureId, CreateOutPaintingTaskRequest.Parameters parameters) {
        // 获取图片信息
        Picture picture = Optional.ofNullable(this.pictureSearchById(pictureId)).orElseThrow(() -> new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "该图片不存在"));

        // 构造请求参数
        CreateOutPaintingTaskRequest currentTaskRequest = new CreateOutPaintingTaskRequest();
        CreateOutPaintingTaskRequest.Input currentInput = new CreateOutPaintingTaskRequest.Input();
        CreateOutPaintingTaskRequest.Parameters currentParameters = new CreateOutPaintingTaskRequest.Parameters();
        currentInput.setImageUrl(picture.getUrl());
        BeanUtil.copyProperties(parameters, currentParameters);
        currentTaskRequest.setInput(currentInput);
        currentTaskRequest.setParameters(currentParameters);

        // 创建任务
        return aiManager.createOutPaintingTask(currentTaskRequest);
    }

    @Override
    @LogParams
    public GetOutPaintingTaskResponse pictureGetOutPaintingTask(String taskId) {
        return aiManager.getOutPaintingTask(taskId);
    }

    /// 私有方法 ///

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Picture> getQueryWrapper(PictureSearchRequest pictureSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(pictureSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空");

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
        LocalDateTime startEditTime = pictureSearchRequest.getStartEditTime();
        LocalDateTime endEditTime = pictureSearchRequest.getEndEditTime();
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
                .ge(ObjUtil.isNotEmpty(startEditTime), Picture::getUpdateTime, startEditTime) // >=
                .lt(ObjUtil.isNotEmpty(endEditTime), Picture::getUpdateTime, endEditTime) // <
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        Picture::getCreateTime // 默认按照创建时间排序
                )
        ;
        return lambdaQueryWrapper;
    }

}





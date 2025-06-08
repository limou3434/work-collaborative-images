package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.mapper.UserMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UserTokenStatus;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * @author Limou
 * @description 针对表【user(用户信息表)】的数据库操作Service实现
 * @createDate 2025-05-04 19:38:16
 */
@SuppressWarnings("deprecation")
@Service
@Validated
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /// 依赖注入 ///

    /**
     * 注入事务管理依赖
     */
    @Resource
    TransactionTemplate transactionTemplate;

    /// 服务实现 ///

    @Override
    @LogParams
    public User userAdd(UserAddRequest userAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(userAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        String account = userAddRequest.getAccount();
        String passwd = userAddRequest.getPasswd();
        ThrowUtils.throwIf(
                StringUtils.isBlank(account),
                CodeBindMessageEnums.PARAMS_ERROR,
                "账户不得为空"
        );
        ThrowUtils.throwIf(
                account.length() < UserConstant.MIN_ACCOUNT_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "账户不得小于" + UserConstant.MIN_ACCOUNT_LENGTH + "位字符"
        );
        ThrowUtils.throwIf(
                this.checkAccountIsNotOk(account),
                CodeBindMessageEnums.PARAMS_ERROR,
                "账户不得包含特殊字符"
        );
        ThrowUtils.throwIf(
                StringUtils.isBlank(passwd),
                CodeBindMessageEnums.PARAMS_ERROR,
                "密码不得为空"
        );
        ThrowUtils.throwIf(
                passwd.length() < UserConstant.MIN_PASSWD_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "密码不得小于" + UserConstant.MIN_PASSWD_LENGTH + "位字符"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            User user = UserAddRequest.copyProperties2Entity(userAddRequest);
            user.setPasswd(this.encryptedPasswd(user.getPasswd()));
            // 操作数据库
            try {
                boolean result = this.save(user); // 保存实例的同时利用唯一键约束避免并发问题
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
            } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够应对并发场景
                ThrowUtils.throwIf(true, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "已经存在该用户, 或者曾经被删除");
            }
            return this.getById(user.getId());
        });
    }

    @Override
    @LogParams
    public Boolean userDelete(UserDeleteRequest userDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(userDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = userDeleteRequest.getId();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不得非法, 必须是正整数");

        // 服务实现
        return transactionTemplate.execute(status -> {
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除用户失败, 也许该用户不存在或者已经被删除");
            return true;
        });
    }

    @Override
    @LogParams
    public User userUpdate(UserUpdateRequest userUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(userUpdateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = userUpdateRequest.getId();
        String account = userUpdateRequest.getAccount();
        String passwd = userUpdateRequest.getPasswd();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );
        ThrowUtils.throwIf(
                StringUtils.isNotBlank(account) && this.checkAccountIsNotOk(account),
                CodeBindMessageEnums.PARAMS_ERROR,
                "账号不得包含非法字符"
        );
        ThrowUtils.throwIf(
                StringUtils.isNotBlank(passwd) && passwd.length() < UserConstant.MIN_PASSWD_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "密码不得小于" + UserConstant.MIN_PASSWD_LENGTH + "位字符"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            User user = UserUpdateRequest.copyProperties2Entity(userUpdateRequest);
            if (StringUtils.isNotBlank(user.getPasswd())) { // 后续需要更新一个用户时, 如果密码为 null 则我们认为不更新密码
                user.setPasswd(this.encryptedPasswd(user.getPasswd()));
            }
            StpUtil.getSessionByLoginId(id).set(UserConstant.USER_LOGIN_STATE, null); // 并且还需要把用户的会话记录清除, 才能动态修改权限
            StpUtil.kickout(id); // 踢下线确保完全更新用户的所有信息, 这是一个保守做法
            // 操作数据库
            this.updateById(user);
            return this.getById(id);
        });
    }

    @Override
    @LogParams
    public Page<User> userSearch(UserSearchRequest userSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(userSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = userSearchRequest.getId();
        String account = userSearchRequest.getAccount();
        ThrowUtils.throwIf(
                id != null && id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "用户标识不得非法"
        );
        ThrowUtils.throwIf(
                StringUtils.isNotBlank(account) && this.checkAccountIsNotOk(account),
                CodeBindMessageEnums.PARAMS_ERROR,
                "用户账号不得包含非法字符"
        );

        // 服务实现
        LambdaQueryWrapper<User> queryWrapper = this.getQueryWrapper(userSearchRequest); // 构造查询条件
        Page<User> page = new Page<>(userSearchRequest.getPageCurrent(), userSearchRequest.getPageSize()); // 获取分页对象
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    @LogParams
    public User userSearchById(Long id) {
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得为空"
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
    public Boolean userDisable(Long userId, Long disableTime, UserRoleEnum userRoleEnum) {
        // 参数检查
        ThrowUtils.throwIf(userId == null, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不得为空");
        ThrowUtils.throwIf(userId <= 0, CodeBindMessageEnums.PARAMS_ERROR, "用户标识不得非法");
        ThrowUtils.throwIf(disableTime == null, CodeBindMessageEnums.PARAMS_ERROR, "封禁时间不能为空, 至少需要填写为 0");
        ThrowUtils.throwIf(disableTime < 0, CodeBindMessageEnums.PARAMS_ERROR, "封禁时间不能为负数");

        // 服务实现
        User user = this.userSearchById(userId); // 查询对应用户
        user.setPasswd(null); // 设置密码为 null 避免密码在解封更新后被二次加密
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.copyProperties2Request(user);
        if (disableTime == 0) { // 如果封禁时间为 0 则表示取消封禁, 默认解封后设置为普通用户权限, 否则封禁用户
            StpUtil.untieDisable(userId); // 解除封禁
            userUpdateRequest.setRole(userRoleEnum.getCode()); // 持久到数据库中方便调试
        } else {
            StpUtil.kickout(userId); // 先踢下线
            StpUtil.disable(userId, disableTime); // 然后再进行封禁
            userUpdateRequest.setRole(UserRoleEnum.BAN_ROLE.getCode()); // 持久到数据库中方便调试
        }
        this.userUpdate(userUpdateRequest);
        return true;
    }

    @Override
    @LogParams
    public User userValidation(String account, String passwd) {
        // 参数检查
        ThrowUtils.throwIf(StringUtils.isBlank(account), CodeBindMessageEnums.PARAMS_ERROR, "账户为空");
        ThrowUtils.throwIf(StringUtils.isBlank(passwd), CodeBindMessageEnums.PARAMS_ERROR, "密码为空");

        // 服务实现
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(User::getAccount, account)
                .eq(User::getPasswd, encryptedPasswd(passwd));
        User user = this.getOne(lambdaQueryWrapper);
        log.debug("当前请求登录的用户 {}", user);
        return user;
    }

    @Override
    @LogParams
    public User userLogin(String account, String passwd, String device) {
        // 检查参数
        ThrowUtils.throwIf(StringUtils.isBlank(account), CodeBindMessageEnums.PARAMS_ERROR, "缺失必要的登录账号");
        ThrowUtils.throwIf(this.checkAccountIsNotOk(account), CodeBindMessageEnums.PARAMS_ERROR, "登录账号不得包含非法字符串");
        ThrowUtils.throwIf(StringUtils.isBlank(passwd), CodeBindMessageEnums.PARAMS_ERROR, "缺失必要的登录密码");
        ThrowUtils.throwIf(StringUtils.isBlank(device), CodeBindMessageEnums.PARAMS_ERROR, "缺失必要的登录设备类型");

        // 服务实现
        User user = this.userValidation(account, passwd); // 创建实例
        ThrowUtils.throwIf(user == null, CodeBindMessageEnums.NO_LOGIN_ERROR, "不存在该用户或者密码不正确");
        try { // 先检查是否被封号再来登录
            StpUtil.checkDisable(user.getId()); // 这个方法检测到封号就会抛出异常
        } catch (DisableServiceException e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "该用户已经被封号, 请联系管理员 898738804@qq.com");
        }
        StpUtil.login(user.getId(), device); // 允许登录
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE, user); // 把用户的信息存储到 Sa-Token 的会话中, 这样后续的用权限判断就不需要一直查询 SQL 才能得到, 缺点是更新权限的时候需要把用户踢下线否则会话无法更新
        log.debug("检测一次设备类型真的被设置为: {}", StpUtil.getLoginDevice());
        return user;
    }

    @Override
    @LogParams
    public Boolean userLogout(String device) {
        StpUtil.logout(); // 默认所有设备都被登出
        return true;
    }

    @Override
    @LogParams
    public Long userGetCurrentLonginUserId() {
        return Long.valueOf(StpUtil.getLoginId().toString());
    }

    @Override
    @LogParams
    public User userGetSessionById(Long id) {
        SaSession session = StpUtil.getSessionByLoginId(id);
        ThrowUtils.throwIf(session == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "无法获取会话");
        User user = (User) session.get(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(user == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该用户尚未登录没有会话资源");
        return user;
    }

    @Override
    @LogParams
    public UserTokenStatus userGetTokenById(Long id) {
        UserTokenStatus userTokenStatus = new UserTokenStatus();
        boolean isLogin = StpUtil.isLogin();
        userTokenStatus.setIsLogin(isLogin);
        if (!isLogin) {
            return userTokenStatus;
        }
        userTokenStatus
                .setTokenName(StpUtil.getTokenName())
                .setTokenTimeout(String.valueOf(StpUtil.getTokenTimeout()))
                .setUserId(id.toString())
                .setUserRole(this.userGetSessionById(id).getRole());
        return userTokenStatus;
    }

    @Override
    @LogParams
    public Boolean userIsAdmin(Long id) {
        return UserRoleEnum.getEnums(this.userGetSessionById(id).getRole()) == UserRoleEnum.ADMIN_ROLE;
    }

    /// 私有方法 ///

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<User> getQueryWrapper(UserSearchRequest userSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(userSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空");

        // 取得需要查询的参数
        Long id = userSearchRequest.getId();
        String account = userSearchRequest.getAccount();
        String email = userSearchRequest.getEmail();
        String phone = userSearchRequest.getPhone();
        String tags = userSearchRequest.getTags();
        String nick = userSearchRequest.getNick();
        String name = userSearchRequest.getName();
        String profile = userSearchRequest.getProfile();
        String address = userSearchRequest.getAddress();
        Integer role = userSearchRequest.getRole();
        Integer level = userSearchRequest.getLevel();
        String sortOrder = userSearchRequest.getSortOrder();
        String sortField = userSearchRequest.getSortField();

        // 获取包装器进行返回
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, User::getId, id)
                .like(StringUtils.isNotBlank(account), User::getAccount, account)
                .like(StringUtils.isNotBlank(email), User::getEmail, email)
                .like(StringUtils.isNotBlank(phone), User::getPhone, phone)
                .like(StringUtils.isNotBlank(tags), User::getTags, tags)
                .like(StringUtils.isNotBlank(nick), User::getNick, nick)
                .like(StringUtils.isNotBlank(name), User::getName, name)
                .like(StringUtils.isNotBlank(profile), User::getProfile, profile)
                .like(StringUtils.isNotBlank(address), User::getAddress, address)
                .eq(role != null, User::getRole, role)
                .eq(level != null, User::getLevel, level)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        User::getId // 默认按照标识排序
                );
        return lambdaQueryWrapper;
    }

    /**
     * 检查账号是否符合要求
     */
    private Boolean checkAccountIsNotOk(String checkAccount) {
        String validPattern = "^[$_-]+$";
        return checkAccount.matches(validPattern);
    }

    /**
     * 获取加密后的密码
     */
    private String encryptedPasswd(String passwd) {
        ThrowUtils.throwIf(StringUtils.isAnyBlank(passwd), CodeBindMessageEnums.PARAMS_ERROR, "需要加密的密码不能为空");
        return DigestUtils.md5DigestAsHex((UserConstant.SALT + passwd).getBytes()); // TODO: 使用 Sa-token 的加密工具
    }

}

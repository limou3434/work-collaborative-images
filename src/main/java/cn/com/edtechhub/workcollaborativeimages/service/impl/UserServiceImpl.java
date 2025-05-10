package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.mapper.UserMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UserStatus;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Limou
 * @description 针对表【user(用户信息表)】的数据库操作Service实现
 * @createDate 2025-05-04 19:38:16
 */
@SuppressWarnings("deprecation")
@Service
@Transactional
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public User userAdd(UserAddRequest userAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(userAddRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "添加请求体为空"));
        checkAccountAndPasswd(userAddRequest.getAccount(), userAddRequest.getPasswd());

        // 创建用户实例的同时加密密码
        var user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        String passwd = user.getPasswd().isEmpty() ? UserConstant.DEFAULT_PASSWD : user.getPasswd(); // 如果密码为空则需要设置默认密码
        user.setPasswd(this.encryptedPasswd(passwd));

        // 保存实例的同时利用唯一键约束避免并发问题
        try {
            this.save(user);
        } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够因对并发场景
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "已经存在该用户, 或者曾经被删除"));
        }
        return user;
    }

    @Override
    public Boolean userDelete(UserDeleteRequest userDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(userDeleteRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "删除请求体为空"));
        Long userId = userDeleteRequest.getId();
        ThrowUtils.throwIf(userId <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "参数用户 id 应为正整数"));

        // 这里 MyBatisPlus 会自动转化为逻辑删除
        boolean result = this.removeById(userId);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "删除用户失败, 也许该用户不存在或者已经被删除"));
        return true;
    }

    @Override
    public User userUpdate(UserUpdateRequest userUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(userUpdateRequest.getId() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户 id 不能为空"));
        ThrowUtils.throwIf(userUpdateRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户 id 必须是正整数"));

        // 更新用户并且需要考虑密码的问题
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        if (StringUtils.isNotBlank(user.getPasswd())) {
            user.setPasswd(this.encryptedPasswd(user.getPasswd())); // 需要加密密码(这里有个雷, 如果用户的密码被查询出来, 就会导致再次加密, 需要使用 if 解决)
        } // 后续需要更新一个用户时, 如果密码为 null 则我们认为不更新密码
        this.updateById(user);

        // 更新用户后最好把用户的信息也返回, 这样方便前端做实时的数据更新
        Long userId = user.getId();
        User newUser = this.getById(userId);
        StpUtil.getSessionByLoginId(userId).set(UserConstant.USER_LOGIN_STATE, newUser); // 并且还需要把用户的会话记录修改, 才能动态修改权限
        StpUtil.kickout(userId); // 踢下线确保完全更新用户的所有信息, 这是一个保守做法
        return newUser;
    }

    @Override
    public Page<User> userSearch(UserSearchRequest userSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(userSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户查询请求不能为空"));

        // 如果用户传递了 id 选项, 则必然是查询一条记录, 为了提高效率直接查询一条数据
        Long userId = userSearchRequest.getId();
        if (userId != null) {
            log.debug("本次查询只需要查询一条记录, 使用 id 字段来提高效率");
            User user = this.getById(userId);
            Page<User> resultPage = new Page<>();
            if (user != null) {
                resultPage.setRecords(Collections.singletonList(user));
                resultPage.setTotal(1);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            }
            else {
                resultPage.setRecords(Collections.emptyList());
                resultPage.setTotal(0);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            }
            return resultPage;
        }

        // 获取查询对象
        LambdaQueryWrapper<User> queryWrapper = this.getQueryWrapper(userSearchRequest); // 构造查询条件

        // 获取分页对象
        Page<User> page = new Page<>(userSearchRequest.getPageCurrent(), userSearchRequest.getPageSize()); // 这里还指定了页码和条数

        // 查询用户分页后直接取得内部的列表进行返回
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    public Boolean userDisable(Long userId, Long disableTime) {
        // 参数检查
        ThrowUtils.throwIf(userId == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户 id 不能为空"));
        ThrowUtils.throwIf(disableTime == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "封禁时间不能为空, 至少需要填写为 0"));

        // 查询数据库
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setId(userId);
        userSearchRequest.setPageCurrent(1);
        userSearchRequest.setPageSize(1);
        User user = this.userSearch(userSearchRequest).getRecords().get(0);
        user.setPasswd(null); // 暂时这么做以避免密码被二次加密

        // 复制用户原本的信息到更新请求实例中
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        BeanUtils.copyProperties(user, userUpdateRequest);

        // 如果封禁时间为 0 则表示取消封禁, 默认解封后设置为普通用户权限, 否则封禁用户
        if (disableTime == 0) {
            StpUtil.untieDisable(userId);
            userUpdateRequest.setRole(0);
        } else {
            StpUtil.kickout(userId); // 先踢下线
            StpUtil.disable(userId, disableTime); // 然后再进行封禁
            userUpdateRequest.setRole(-1);
        }

        // 把封禁体现到数据库中方便维护
        this.userUpdate(userUpdateRequest);
        return true;
    }

    @Override
    public Boolean userRegister(String account, String passwd, String checkPasswd) {
        // 检查参数
        checkAccountAndPasswd(account, passwd);
        ThrowUtils.throwIf(!passwd.equals(checkPasswd), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "两次输入的密码不一致"));

        // 注册一个新的用户
        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setAccount(account);
        userAddRequest.setPasswd(passwd);
        this.userAdd(userAddRequest); // 这里内部添加出错会自己出异常来解决

        return true;
    }

    @Override
    public User userLogin(String account, String passwd, String device) {
        // 检查参数
        checkAccountAndPasswd(account, passwd);
        ThrowUtils.throwIf(StringUtils.isAllBlank(device), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "缺失必要的登录设备类型"));

        // 查询是否存在该用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(User::getAccount, account)
                .eq(User::getPasswd, encryptedPasswd(passwd));
        User user = this.getOne(lambdaQueryWrapper);

        log.debug("当前请求登录的用户 {}", user);

        ThrowUtils.throwIf(user == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "该用户可能不存在, 也可能是密码错误"));

        // 先检查是否被封号再来登录
        try {
            StpUtil.checkDisable(user.getId()); // 这个方法检测到封号就会抛出异常
        } catch (DisableServiceException e) {
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "该用户已经被封号, 请联系管理员 898738804@qq.com"));
        }
        StpUtil.login(user.getId(), device);
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE, user); // 把用户的信息存储到 Sa-Token 的会话中, 这样后续的用权限判断就不需要一直查询 SQL 才能得到, 缺点是更新权限的时候需要把用户踢下线否则会话无法更新
        log.debug("检测一次设备类型真的被设置为: {}", StpUtil.getLoginDevice());
        return user;
    }

    @Override
    public Boolean userLogout(String device) {
        StpUtil.logout(); // 默认所有设备都被登出
        return true;
    }

    @Override
    public UserStatus userGetLoginStatus() {
        UserStatus userStatus = new UserStatus();
        userStatus.setIsLogin(StpUtil.isLogin());
        if (!userStatus.getIsLogin()) {
            return userStatus;
        }
        userStatus.setTokenName(StpUtil.getTokenName());
        userStatus.setTokenTimeout(String.valueOf(StpUtil.getTokenTimeout()));
        userStatus.setUserId(String.valueOf(StpUtil.getLoginId()));
        userStatus.setUserRole(((User) StpUtil.getSessionByLoginId(StpUtil.getLoginId()).get(UserConstant.USER_LOGIN_STATE)).getRole()); // 获取登陆用户会话中的权限
        return userStatus;
    }

    @Override
    public User userGetLoginInfo() {
        User user = (User) StpUtil.getSessionByLoginId(StpUtil.getLoginId()).get(UserConstant.USER_LOGIN_STATE);
        return user;
    }

    /**
     * 检查账户和密码是否合规的方法
     */
    private void checkAccountAndPasswd(String checkAccount, String checkPasswd) {
        // 账户和密码都不能为空
        ThrowUtils.throwIf(StringUtils.isAnyBlank(checkAccount), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "账户为空"));
        ThrowUtils.throwIf(StringUtils.isAnyBlank(checkPasswd), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "密码为空"));

        // 判断账户和密码的长度是否符合要求
        ThrowUtils.throwIf(checkAccount.length() < UserConstant.ACCOUNT_LENGTH, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "账户不得小于" + UserConstant.ACCOUNT_LENGTH + "位"));
        ThrowUtils.throwIf(checkPasswd.length() < UserConstant.PASSWD_LENGTH, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "密码不得小于" + UserConstant.PASSWD_LENGTH + "位"));

        // 避免账户和密码中的非法字符
        String validPattern = "^[$_-]+$";
        ThrowUtils.throwIf(checkAccount.matches(validPattern), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "账号不能包含特殊字符"));
    }

    /**
     * 获取加密后的密码
     */
    private String encryptedPasswd(String passwd) {
        ThrowUtils.throwIf(StringUtils.isAnyBlank(passwd), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "需要加密的密码不能为空"));
        return DigestUtils.md5DigestAsHex((UserConstant.SALT + passwd).getBytes());
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<User> getQueryWrapper(UserSearchRequest userSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(userSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空"));

        // 取得需要查询的参数
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

}

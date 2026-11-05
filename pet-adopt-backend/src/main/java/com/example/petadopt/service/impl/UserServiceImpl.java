package com.example.petadopt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.common.Result;
import com.example.petadopt.dto.LoginDTO;
import com.example.petadopt.entity.User;
import com.example.petadopt.exception.BizException;
import com.example.petadopt.jwt.JwtUtil;
import com.example.petadopt.mapper.UserMapper;
import com.example.petadopt.service.UserService;
import com.example.petadopt.vo.LoginVO;
import com.example.petadopt.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto, boolean adminOnly) {
        if (dto == null || !StringUtils.hasText(dto.getUsername()) || !StringUtils.hasText(dto.getPassword())) {
            throw new BizException(Result.BAD_REQUEST, "请输入用户名和密码");
        }
        String username = dto.getUsername().trim();
        User user = lambdaQuery().eq(User::getUsername, username).one();

        // 学习项目：密码明文直接比对；用 Objects.equals 防止数据库密码为 NULL 时 NPE
        if (user == null || !Objects.equals(user.getPassword(), dto.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (adminOnly && !Integer.valueOf(1).equals(user.getRole())) {
            throw new BizException(Result.FORBIDDEN, "该账号不是管理员，无法登录后台");
        }

        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, toUserVO(user), user.getRole());
    }

    @Override
    public PageResult<UserVO> pageUsers(long page, long size, String keyword) {
        long current = Math.max(page, 1);
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(User::getUsername, kw)
                    .or().like(User::getName, kw)
                    .or().like(User::getPhone, kw));
        }
        wrapper.orderByDesc(User::getId);

        Page<User> result = page(new Page<>(current, pageSize), wrapper);
        List<UserVO> records = result.getRecords().stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());
        return PageResult.of(result, records);
    }

    /** 转 VO，去掉密码 */
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setAddress(user.getAddress());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}

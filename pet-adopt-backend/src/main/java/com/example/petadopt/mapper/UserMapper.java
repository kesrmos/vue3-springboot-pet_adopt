package com.example.petadopt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petadopt.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.example.petadopt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.petadopt.entity.Pet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物 Mapper
 */
@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}

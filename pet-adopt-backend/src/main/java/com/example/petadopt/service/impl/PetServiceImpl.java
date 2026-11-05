package com.example.petadopt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.common.Result;
import com.example.petadopt.entity.AdoptApply;
import com.example.petadopt.entity.Pet;
import com.example.petadopt.exception.BizException;
import com.example.petadopt.mapper.AdoptApplyMapper;
import com.example.petadopt.mapper.PetMapper;
import com.example.petadopt.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 宠物服务实现
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Autowired
    private AdoptApplyMapper adoptApplyMapper;

    @Override
    public PageResult<Pet> pagePets(long page, long size, String name, String type, String gender, Integer status) {
        // 分页参数归一化：页码最小 1，每页 1~100，非法值回落到默认值
        long current = Math.max(page, 1);
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        String nameValue = StringUtils.hasText(name) ? name.trim() : null;
        String typeValue = StringUtils.hasText(type) ? type.trim() : null;
        String genderValue = StringUtils.hasText(gender) ? gender.trim() : null;

        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(nameValue != null, Pet::getName, nameValue)
                .eq(typeValue != null, Pet::getType, typeValue)
                .eq(genderValue != null, Pet::getGender, genderValue)
                .eq(status != null, Pet::getStatus, status)
                .orderByDesc(Pet::getId);

        Page<Pet> result = page(new Page<>(current, pageSize), wrapper);
        return PageResult.of(result);
    }

    @Override
    public Pet getPetDetail(Long id) {
        if (id == null || id <= 0) {
            throw new BizException(Result.BAD_REQUEST, "宠物ID不合法");
        }
        Pet pet = getById(id);
        if (pet == null) {
            throw new BizException(Result.NOT_FOUND, "宠物不存在或已被删除");
        }
        return pet;
    }

    @Override
    public void savePet(Pet pet) {
        if (pet == null) {
            throw new BizException(Result.BAD_REQUEST, "宠物信息不能为空");
        }
        if (pet.getId() != null && pet.getId() <= 0) {
            throw new BizException(Result.BAD_REQUEST, "宠物ID不合法");
        }
        if (!StringUtils.hasText(pet.getName())) {
            throw new BizException("宠物名称不能为空");
        }
        if (!StringUtils.hasText(pet.getType())) {
            throw new BizException("宠物类型不能为空");
        }
        pet.setName(pet.getName().trim());
        pet.setType(pet.getType().trim());

        if (pet.getStatus() == null) {
            pet.setStatus(0);
        }
        if (!Integer.valueOf(0).equals(pet.getStatus()) && !Integer.valueOf(1).equals(pet.getStatus())) {
            throw new BizException("宠物状态不合法（0 待领养，1 已领养）");
        }

        if (pet.getId() == null) {
            save(pet);
            return;
        }
        if (getById(pet.getId()) == null) {
            throw new BizException(Result.NOT_FOUND, "要编辑的宠物不存在");
        }
        updateById(pet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePet(Long id) {
        if (id == null || id <= 0) {
            throw new BizException(Result.BAD_REQUEST, "宠物ID不合法");
        }
        if (getById(id) == null) {
            throw new BizException(Result.NOT_FOUND, "宠物不存在或已被删除");
        }
        // 先清理该宠物的领养申请，避免留下孤儿数据
        adoptApplyMapper.delete(new LambdaQueryWrapper<AdoptApply>().eq(AdoptApply::getPetId, id));
        removeById(id);
    }
}

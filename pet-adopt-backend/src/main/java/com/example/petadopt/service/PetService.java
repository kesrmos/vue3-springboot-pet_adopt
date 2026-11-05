package com.example.petadopt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.entity.Pet;

/**
 * 宠物服务
 */
public interface PetService extends IService<Pet> {

    /**
     * 宠物分页列表
     *
     * @param name   名称关键字（管理端使用，前台传 null）
     * @param type   类型，可为空
     * @param gender 性别，可为空
     * @param status 状态，可为空
     */
    PageResult<Pet> pagePets(long page, long size, String name, String type, String gender, Integer status);

    /** 宠物详情，不存在时抛业务异常 */
    Pet getPetDetail(Long id);

    /** 新增 / 编辑宠物（id 为空新增，否则编辑） */
    void savePet(Pet pet);

    /** 删除宠物（同时删除其领养申请记录） */
    void removePet(Long id);
}

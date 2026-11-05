package com.example.petadopt.controller;

import com.example.petadopt.common.PageResult;
import com.example.petadopt.common.Result;
import com.example.petadopt.entity.Pet;
import com.example.petadopt.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户前台：宠物浏览（免登录）
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 宠物分页列表
     * GET /api/pet/list?page=1&size=10&type=狗&gender=公&status=0
     */
    @GetMapping("/list")
    public Result<PageResult<Pet>> list(@RequestParam(defaultValue = "1") long page,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Integer status) {
        return Result.ok(petService.pagePets(page, size, null, type, gender, status));
    }

    /**
     * 宠物详情
     * GET /api/pet/1
     */
    @GetMapping("/{id}")
    public Result<Pet> detail(@PathVariable Long id) {
        return Result.ok(petService.getPetDetail(id));
    }
}

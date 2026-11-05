package com.example.petadopt.controller;

import com.example.petadopt.common.PageResult;
import com.example.petadopt.common.Result;
import com.example.petadopt.dto.AuditDTO;
import com.example.petadopt.dto.LoginDTO;
import com.example.petadopt.entity.Pet;
import com.example.petadopt.service.AdoptApplyService;
import com.example.petadopt.service.PetService;
import com.example.petadopt.service.UserService;
import com.example.petadopt.vo.ApplyVO;
import com.example.petadopt.vo.LoginVO;
import com.example.petadopt.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理员接口（除登录外，均需要 role=1，由 JwtInterceptor 统一校验）
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptApplyService adoptApplyService;

    /**
     * 管理员登录
     * POST /api/admin/login  { "username": "admin", "password": "123456" }
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.ok("登录成功", userService.login(dto, true));
    }

    /**
     * 管理员：宠物分页列表
     * GET /api/admin/pet/list?page=1&size=10&name=豆&type=狗&gender=公&status=0
     */
    @GetMapping("/pet/list")
    public Result<PageResult<Pet>> petList(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String gender,
                                           @RequestParam(required = false) Integer status) {
        return Result.ok(petService.pagePets(page, size, name, type, gender, status));
    }

    /**
     * 管理员：新增 / 编辑宠物
     * POST /api/admin/pet/save
     * 新增：不传 id；编辑：传 id
     */
    @PostMapping("/pet/save")
    public Result<Void> savePet(@RequestBody Pet pet) {
        boolean isNew = pet.getId() == null;
        petService.savePet(pet);
        return Result.ok(isNew ? "新增成功" : "保存成功", null);
    }

    /**
     * 管理员：删除宠物
     * DELETE /api/admin/pet/1
     */
    @DeleteMapping("/pet/{id}")
    public Result<Void> deletePet(@PathVariable Long id) {
        petService.removePet(id);
        return Result.ok("删除成功", null);
    }

    /**
     * 管理员：全部领养申请分页
     * GET /api/admin/apply/list?page=1&size=10&auditStatus=0
     */
    @GetMapping("/apply/list")
    public Result<PageResult<ApplyVO>> applyList(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) Integer auditStatus) {
        return Result.ok(adoptApplyService.pageApplies(page, size, auditStatus));
    }

    /**
     * 管理员：审核领养申请
     * PUT /api/admin/apply/audit
     * { "applyId": 2, "auditStatus": 1, "auditRemark": "条件符合，同意领养" }
     */
    @PutMapping("/apply/audit")
    public Result<Void> audit(@RequestBody @Valid AuditDTO dto) {
        adoptApplyService.audit(dto);
        return Result.ok("审核完成", null);
    }

    /**
     * 管理员：用户管理分页
     * GET /api/admin/user/list?page=1&size=10&keyword=张
     */
    @GetMapping("/user/list")
    public Result<PageResult<UserVO>> userList(@RequestParam(defaultValue = "1") long page,
                                               @RequestParam(defaultValue = "10") long size,
                                               @RequestParam(required = false) String keyword) {
        return Result.ok(userService.pageUsers(page, size, keyword));
    }
}

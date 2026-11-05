package com.example.petadopt.controller;

import com.example.petadopt.common.Result;
import com.example.petadopt.dto.ApplySubmitDTO;
import com.example.petadopt.jwt.UserContext;
import com.example.petadopt.service.AdoptApplyService;
import com.example.petadopt.vo.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户前台：领养申请（需要登录）
 */
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private AdoptApplyService adoptApplyService;

    /**
     * 提交领养申请
     * POST /api/apply/submit  { "petId": 1, "liveEnv": "自有住房", "exp": "养过猫", "remark": "..." }
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody @Valid ApplySubmitDTO dto) {
        adoptApplyService.submit(dto, UserContext.getUserId());
        return Result.ok("申请提交成功，请等待管理员审核", null);
    }

    /**
     * 我的领养申请
     * GET /api/apply/my
     */
    @GetMapping("/my")
    public Result<List<ApplyVO>> my() {
        return Result.ok(adoptApplyService.myApplies(UserContext.getUserId()));
    }
}

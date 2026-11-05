package com.example.petadopt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.dto.ApplySubmitDTO;
import com.example.petadopt.dto.AuditDTO;
import com.example.petadopt.entity.AdoptApply;
import com.example.petadopt.vo.ApplyVO;

import java.util.List;

/**
 * 领养申请服务
 */
public interface AdoptApplyService extends IService<AdoptApply> {

    /** 提交领养申请 */
    void submit(ApplySubmitDTO dto, Long userId);

    /** 当前用户的领养申请列表 */
    List<ApplyVO> myApplies(Long userId);

    /**
     * 管理员：全部申请分页
     *
     * @param auditStatus 审核状态，null 表示全部
     */
    PageResult<ApplyVO> pageApplies(long page, long size, Integer auditStatus);

    /** 审核领养申请 */
    void audit(AuditDTO dto);
}

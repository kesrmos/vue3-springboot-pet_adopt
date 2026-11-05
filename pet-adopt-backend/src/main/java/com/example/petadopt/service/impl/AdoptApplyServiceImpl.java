package com.example.petadopt.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadopt.common.PageResult;
import com.example.petadopt.common.Result;
import com.example.petadopt.dto.ApplySubmitDTO;
import com.example.petadopt.dto.AuditDTO;
import com.example.petadopt.entity.AdoptApply;
import com.example.petadopt.entity.Pet;
import com.example.petadopt.exception.BizException;
import com.example.petadopt.mapper.AdoptApplyMapper;
import com.example.petadopt.mapper.PetMapper;
import com.example.petadopt.service.AdoptApplyService;
import com.example.petadopt.vo.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 领养申请服务实现
 */
@Service
public class AdoptApplyServiceImpl extends ServiceImpl<AdoptApplyMapper, AdoptApply> implements AdoptApplyService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(ApplySubmitDTO dto, Long userId) {
        if (userId == null) {
            throw new BizException(Result.UNAUTHORIZED, "未登录，请先登录");
        }
        if (dto == null || dto.getPetId() == null || dto.getPetId() <= 0) {
            throw new BizException(Result.BAD_REQUEST, "请选择要领养的宠物");
        }
        Pet pet = petMapper.selectById(dto.getPetId());
        if (pet == null) {
            throw new BizException(Result.NOT_FOUND, "宠物不存在或已被删除");
        }
        if (Integer.valueOf(1).equals(pet.getStatus())) {
            throw new BizException("该宠物已被领养，看看其它小伙伴吧");
        }

        long count = lambdaQuery()
                .eq(AdoptApply::getUserId, userId)
                .eq(AdoptApply::getPetId, dto.getPetId())
                .eq(AdoptApply::getAuditStatus, 0)
                .count();
        if (count > 0) {
            throw new BizException("您已提交过该宠物的领养申请，请耐心等待审核");
        }

        AdoptApply apply = new AdoptApply();
        apply.setUserId(userId);
        apply.setPetId(dto.getPetId());
        apply.setLiveEnv(dto.getLiveEnv());
        apply.setExp(dto.getExp());
        apply.setRemark(dto.getRemark());
        apply.setAuditStatus(0);
        save(apply);
    }

    @Override
    public List<ApplyVO> myApplies(Long userId) {
        if (userId == null) {
            throw new BizException(Result.UNAUTHORIZED, "未登录，请先登录");
        }
        return baseMapper.selectMyApply(userId);
    }

    @Override
    public PageResult<ApplyVO> pageApplies(long page, long size, Integer auditStatus) {
        long current = Math.max(page, 1);
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        IPage<ApplyVO> result = baseMapper.selectApplyPage(new Page<>(current, pageSize), auditStatus);
        return PageResult.of(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditDTO dto) {
        if (dto == null || dto.getApplyId() == null || dto.getApplyId() <= 0) {
            throw new BizException(Result.BAD_REQUEST, "请选择要审核的申请");
        }
        if (dto.getAuditStatus() == null) {
            throw new BizException(Result.BAD_REQUEST, "请选择审核结果");
        }
        AdoptApply apply = getById(dto.getApplyId());
        if (apply == null) {
            throw new BizException(Result.NOT_FOUND, "领养申请不存在");
        }
        if (!Integer.valueOf(0).equals(apply.getAuditStatus())) {
            throw new BizException("该申请已审核，请勿重复操作");
        }
        if (!Integer.valueOf(1).equals(dto.getAuditStatus()) && !Integer.valueOf(2).equals(dto.getAuditStatus())) {
            throw new BizException(Result.BAD_REQUEST, "审核结果不合法（1 通过，2 驳回）");
        }

        apply.setAuditStatus(dto.getAuditStatus());
        apply.setAuditRemark(dto.getAuditRemark());
        updateById(apply);

        // 审核通过：宠物标记为已领养，并自动驳回该宠物其它待审核申请
        if (Integer.valueOf(1).equals(dto.getAuditStatus())) {
            Pet pet = petMapper.selectById(apply.getPetId());
            if (pet != null) {
                pet.setStatus(1);
                petMapper.updateById(pet);
            }
            List<AdoptApply> others = lambdaQuery()
                    .eq(AdoptApply::getPetId, apply.getPetId())
                    .eq(AdoptApply::getAuditStatus, 0)
                    .ne(AdoptApply::getId, apply.getId())
                    .list();
            for (AdoptApply other : others) {
                other.setAuditStatus(2);
                other.setAuditRemark("该宠物已被其他申请人领养");
                updateById(other);
            }
        }
    }
}

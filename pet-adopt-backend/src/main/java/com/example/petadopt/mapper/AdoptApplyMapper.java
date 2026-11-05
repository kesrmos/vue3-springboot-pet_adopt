package com.example.petadopt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.petadopt.entity.AdoptApply;
import com.example.petadopt.vo.ApplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 领养申请 Mapper
 * 简单 CRUD 用 MyBatis-Plus，联表查询写在 AdoptApplyMapper.xml
 */
@Mapper
public interface AdoptApplyMapper extends BaseMapper<AdoptApply> {

    /**
     * 管理员：全部领养申请分页（联表宠物 + 申请人）
     *
     * @param page        分页参数
     * @param auditStatus 审核状态，null 表示全部
     */
    IPage<ApplyVO> selectApplyPage(Page<ApplyVO> page, @Param("auditStatus") Integer auditStatus);

    /**
     * 前台：当前用户的领养申请列表
     */
    List<ApplyVO> selectMyApply(@Param("userId") Long userId);
}

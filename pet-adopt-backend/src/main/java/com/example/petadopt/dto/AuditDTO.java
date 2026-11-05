package com.example.petadopt.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 审核领养申请参数
 */
@Data
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "申请ID不能为空")
    private Long applyId;

    /** 审核结果：1 通过，2 驳回 */
    @NotNull(message = "请选择审核结果")
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;
}

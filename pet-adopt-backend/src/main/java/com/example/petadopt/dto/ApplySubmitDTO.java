package com.example.petadopt.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 提交领养申请参数
 */
@Data
public class ApplySubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请选择要领养的宠物")
    private Long petId;

    @NotBlank(message = "请填写居住环境")
    private String liveEnv;

    /** 养宠经验 */
    private String exp;

    /** 备注 */
    private String remark;
}

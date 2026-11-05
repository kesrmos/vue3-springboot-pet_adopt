package com.example.petadopt.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领养申请展示对象（联表宠物 + 申请人信息）
 */
@Data
public class ApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long petId;

    /** 居住环境 */
    private String liveEnv;

    /** 养宠经验 */
    private String exp;

    /** 备注 */
    private String remark;

    /** 审核状态：0 待审核，1 通过，2 驳回 */
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;

    /** 申请时间 */
    private LocalDateTime createTime;

    // ---------------- 宠物信息 ----------------
    private String petName;
    private String petType;
    private String petGender;
    private Integer petAge;
    private String petPhoto;
    private Integer petStatus;

    // ---------------- 申请人信息 ----------------
    private String userName;
    private String userPhone;
    private String userAddress;
}

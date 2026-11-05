package com.example.petadopt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领养申请表
 */
@Data
@TableName("adopt_apply")
public class AdoptApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请用户 id */
    private Long userId;

    /** 宠物 id */
    private Long petId;

    /** 居住环境 */
    private String liveEnv;

    /** 养宠经验（exp 是函数名，加反引号避免歧义） */
    @TableField("`exp`")
    private String exp;

    /** 备注 */
    private String remark;

    /** 审核状态：0 待审核，1 通过，2 驳回 */
    private Integer auditStatus;

    /** 审核意见 */
    private String auditRemark;

    /** 申请时间，由数据库默认值生成 */
    private LocalDateTime createTime;
}

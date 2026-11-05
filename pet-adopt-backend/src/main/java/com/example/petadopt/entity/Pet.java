package com.example.petadopt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物表
 */
@Data
@TableName("pet")
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宠物名称 */
    private String name;

    /** 类型：猫/狗/兔/鸟... */
    private String type;

    /** 性别：公/母 */
    private String gender;

    /** 年龄（月） */
    private Integer age;

    /** 照片地址 */
    private String photo;

    /** 描述 */
    private String description;

    /** 状态：0 待领养，1 已领养 */
    private Integer status;

    /** 创建时间，由数据库默认值生成 */
    private LocalDateTime createTime;
}

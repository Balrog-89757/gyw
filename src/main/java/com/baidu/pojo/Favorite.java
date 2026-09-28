package com.baidu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer musicId;

    private String remark;

    private Date createTime;

    // ===== 以下为非数据库字段，用于前端展示 =====
    @TableField(exist = false)
    private String musicName;

    @TableField(exist = false)
    private String singer;
}
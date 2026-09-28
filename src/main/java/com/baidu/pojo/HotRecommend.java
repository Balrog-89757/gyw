package com.baidu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 热门推荐实体类
 * 对应数据库表 hot_recommend
 */
@Data
@TableName("hot_recommend")
public class HotRecommend {

    /**
     * 推荐ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 音乐ID
     */
    private Integer musicId;

    /**
     * 排序序号（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 推荐标题（如：本周热歌）
     */
    private String recommendTitle;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

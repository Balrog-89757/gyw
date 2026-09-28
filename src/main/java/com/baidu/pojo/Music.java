package com.baidu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 音乐实体类
 * 对应数据库表 music
 */
@Data
@TableName("music")
public class Music {

    /**
     * 音乐ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 歌曲名称
     */
    private String name;

    /**
     * 歌手
     */
    private String singer;

    /**
     * 专辑
     */
    private String album;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 音乐文件URL
     */
    private String fileUrl;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 是否热门：0-否，1-是
     */
    private Integer isHot;

    /**
     * 状态：0-下架，1-上架
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
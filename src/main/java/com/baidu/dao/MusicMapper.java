package com.baidu.dao;

import com.baidu.pojo.Music;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {
    // 可在此添加自定义查询方法
}

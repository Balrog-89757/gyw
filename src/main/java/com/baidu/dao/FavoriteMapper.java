package com.baidu.dao;

import com.baidu.pojo.Favorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    // 可在此添加自定义查询方法
}
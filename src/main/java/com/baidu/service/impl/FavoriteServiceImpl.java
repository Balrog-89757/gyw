package com.baidu.service.impl;


import com.baidu.dao.FavoriteMapper;
import com.baidu.pojo.Favorite;

import com.baidu.service.FavoriteService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
}

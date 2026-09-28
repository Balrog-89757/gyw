package com.baidu.service.impl;

import com.baidu.dao.HotRecommendMapper;
import com.baidu.dao.MusicMapper;
import com.baidu.pojo.HotRecommend;
import com.baidu.pojo.Music;
import com.baidu.service.HotRecommendService;
import com.baidu.service.MusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HotRecommendServiceImpl extends ServiceImpl<HotRecommendMapper, HotRecommend> implements HotRecommendService {
    // 不需要写任何方法，父类已提供
}


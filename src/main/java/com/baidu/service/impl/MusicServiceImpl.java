package com.baidu.service.impl;

import com.baidu.dao.MusicMapper;
import com.baidu.pojo.Music;
import com.baidu.service.MusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service  //
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    // 不需要写任何方法，父类已提供
}
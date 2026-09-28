package com.baidu.service.impl;

import com.baidu.dao.UserMapper;
import com.baidu.pojo.User;
import com.baidu.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 自定义方法实现
}
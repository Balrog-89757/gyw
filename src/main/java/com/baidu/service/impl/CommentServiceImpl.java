package com.baidu.service.impl;

import com.baidu.dao.CommentMapper;
import com.baidu.pojo.Comment;
import com.baidu.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}

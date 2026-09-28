package com.baidu.controller;

import com.baidu.pojo.Comment;
import com.baidu.service.CommentService;
import com.baidu.utils.PageResult;
import com.baidu.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // ========== 用户端：查询当前用户自己的评论（分页 + 关键词搜索） ==========
    @GetMapping("/my/list")
    public PageResult<Comment> myList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer limit,
                                      @RequestParam(required = false) String keyword,
                                      HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            PageResult<Comment> pr = new PageResult<>();
            pr.setCode(1);
            pr.setMsg("请先登录");
            return pr;
        }

        Page<Comment> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> result = commentService.page(pageParam, wrapper);
        PageResult<Comment> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }

    // ========== 用户端：添加评论 ==========
    @PostMapping("/add")
    public R add(@RequestBody Comment comment, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }
        if (StringUtils.isBlank(comment.getContent())) {
            return R.error("评论内容不能为空");
        }
        comment.setUserId(userId);
        comment.setLikeCount(0);
        boolean success = commentService.save(comment);
        return success ? R.right("评论成功") : R.error("评论失败");
    }

    // ========== 用户端：修改评论（只能修改自己的） ==========
    @PutMapping("/update")
    public R update(@RequestBody Comment comment, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }
        if (StringUtils.isBlank(comment.getContent())) {
            return R.error("评论内容不能为空");
        }

        Comment exist = commentService.getById(comment.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return R.error("无权操作或评论不存在");
        }

        exist.setContent(comment.getContent());
        boolean success = commentService.updateById(exist);
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    //用户端：删除评论
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }

        Comment exist = commentService.getById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            return R.error("无权操作或评论不存在");
        }

        boolean success = commentService.removeById(id);
        return success ? R.right("删除成功") : R.error("删除失败");
    }

    //查询单条评论
    @GetMapping("/get/{id}")
    public R get(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }
        Comment comment = commentService.getById(id);
        if (comment == null || !comment.getUserId().equals(userId)) {
            return R.error("无权查看或评论不存在");
        }
        return R.right().put("data", comment);
    }

    // ========== 管理员接口（仅删除，不能修改） ==========
    @DeleteMapping("/admin/delete/{id}")
    public R adminDelete(@PathVariable Integer id) {
        boolean success = commentService.removeById(id);
        return success ? R.right("删除成功") : R.error("删除失败");
    }

    @GetMapping("/admin/list")
    public PageResult<Comment> adminList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer limit,
                                         @RequestParam(required = false) String keyword) {
        Page<Comment> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> result = commentService.page(pageParam, wrapper);
        PageResult<Comment> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }
}
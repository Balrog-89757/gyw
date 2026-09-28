package com.baidu.controller;

import com.baidu.pojo.Music;
import com.baidu.service.MusicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private MusicService musicService;

    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(required = false) String keyword,
            Model model) {

        // 1. 热门推荐（取 is_hot=1 的前 6 条）
        LambdaQueryWrapper<Music> hotWrapper = new LambdaQueryWrapper<>();
        hotWrapper.eq(Music::getIsHot, 1)
                .eq(Music::getStatus, 1)
                .orderByDesc(Music::getPlayCount)
                .last("limit 6");
        List<Music> hotList = musicService.list(hotWrapper);
        model.addAttribute("hotList", hotList);

        // 2. 所有音乐（分页 + 搜索）
        int limit = 8;
        Page<Music> page = new Page<>(currentPage, limit);
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Music::getStatus, 1);

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Music::getName, keyword)
                    .or().like(Music::getSinger, keyword));
        }
        wrapper.orderByDesc(Music::getPlayCount);

        Page<Music> result = musicService.page(page, wrapper);

        model.addAttribute("list", result.getRecords());
        model.addAttribute("totalCount", result.getTotal());
        model.addAttribute("pages", result.getCurrent());
        model.addAttribute("keyword", keyword);

        return "index";   // 对应 templates/index.html
    }
}
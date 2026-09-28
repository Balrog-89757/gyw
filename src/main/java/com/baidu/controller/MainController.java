package com.baidu.controller;

import com.baidu.pojo.Music;
import com.baidu.service.MusicService;
import com.baidu.utils.SysMenu;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class MainController {

    @Autowired
    private MusicService musicService;

    // 跳转到登录页面
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    // 跳转到注册页面
    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }

    //跳转用户欢迎界面
    @RequestMapping("/userCenter")
    public String userCenter() {
        return "userMenu/userCenter";   // 对应 templates/userMenu/userCenter.html
    }


    // 管理员登录后跳转
    @RequestMapping("/toIndex")
    public String toContent(HttpServletRequest request) {
        request.setAttribute("userMenuList", getSysMenu());

        return "content";
    }
    // 管理员个人信息修改菜单跳转
    @RequestMapping("/toProfile")
    public String toProfile() {
        return "profile";
    }

    //管理员音乐列表跳转
    @RequestMapping("/toMusicList")
    public String toMusicList() { return "music/musicList"; }

    //管理员热门推荐跳转
    @RequestMapping("/toHotList")
    public String toHotList() { return "hot/hotRecommendList"; }

    //管理员评论管理跳转
    @RequestMapping("/toCommentList")
    public String toCommentList() { return "comment/admin/commentList"; }



    //用户个人中心跳转
    @RequestMapping("/toUserIndex")
    public String toUserIndex(HttpServletRequest request) {
        request.setAttribute("userMenuList", getUserMenu());  //  设置普通用户菜单
        return "userMenu/userContent";
    }
    //用户评论管理跳转
    @RequestMapping("/toCommentPage")
    public String toCommentPage() {
        return "comment/commentList";
    }

    //用户收藏跳转
    @RequestMapping("/toFavoritePage")
    public String toFavoritePage() {
        return "favorite/favoriteList";
    }
    // 普通用户登录后跳转（传入完整数据）
    @RequestMapping("/index")
    public String toUserContent(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request,
            Model model) {

        // 1. 菜单数据
        request.setAttribute("userMenuList", getUserMenu());

        // 2. 音乐列表数据（分页 + 搜索）
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

        return "userContent";
    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    // 跳转到用户管理页面（管理员）
    @RequestMapping("/toUserList")
    public String toUserList() {
        return "user/list";   // 对应 templates/user/list.html
    }
    // 管理员菜单
    private List<SysMenu> getSysMenu() {
        List<SysMenu> ml = new ArrayList<>();

        SysMenu m1 = new SysMenu("user", "用户管理", "layui-icon layui-icon-user", false, null);
        m1.getChildMenuList().add(new SysMenu("user", "用户列表", "layui-icon layui-icon-list", true, "/toUserList"));
        ml.add(m1);

        SysMenu m2 = new SysMenu("music", "音乐管理", "layui-icon layui-icon-music", false, null);
        m2.getChildMenuList().add(new SysMenu("music", "音乐列表", "layui-icon layui-icon-list", true, "/toMusicList"));
        ml.add(m2);

        SysMenu m3 = new SysMenu("hot", "热门推荐", "layui-icon layui-icon-fire", false, null);
        m3.getChildMenuList().add(new SysMenu("hot", "推荐列表", "layui-icon layui-icon-list", true, "/toHotList"));
        ml.add(m3);

        SysMenu m4 = new SysMenu("comment", "评论管理", "layui-icon layui-icon-comment", false, null);
        m4.getChildMenuList().add(new SysMenu("comment", "评论列表", "layui-icon layui-icon-list", true, "/toCommentList"));
        ml.add(m4);

        SysMenu m5 = new SysMenu("profile", "个人中心", "layui-icon layui-icon-set", false, null);
        m5.getChildMenuList().add(new SysMenu("profile", "个人信息", "layui-icon layui-icon-user", true, "/toProfile"));
        ml.add(m5);
        return ml;
    }

    // 普通用户菜单
    private List<SysMenu> getUserMenu() {
        List<SysMenu> ml = new ArrayList<>();

        // 1. 我的收藏
        SysMenu m1 = new SysMenu("favorite", "我的收藏", "layui-icon layui-icon-star", false, null);
        m1.getChildMenuList().add(new SysMenu("favorite", "收藏列表", "layui-icon layui-icon-list", true, "/toFavoritePage"));
        ml.add(m1);

        // 2. 我的评论
        SysMenu m2 = new SysMenu("comment", "我的评论", "layui-icon layui-icon-comment", false, null);
        m2.getChildMenuList().add(new SysMenu("comment", "评论列表", "layui-icon layui-icon-list", true, "/toCommentPage"));
        ml.add(m2);

        // 3. 个人中心
        SysMenu m3 = new SysMenu("profile", "个人中心", "layui-icon layui-icon-user", false, null);
        m3.getChildMenuList().add(new SysMenu("profile", "个人信息", "layui-icon layui-icon-user", true, "/toProfile"));
        ml.add(m3);

        return ml;
    }
}
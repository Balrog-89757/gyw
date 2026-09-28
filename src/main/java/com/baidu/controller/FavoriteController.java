package com.baidu.controller;

import com.baidu.pojo.Favorite;
import com.baidu.pojo.Music;
import com.baidu.service.FavoriteService;
import com.baidu.service.MusicService;
import com.baidu.utils.PageResult;
import com.baidu.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MusicService musicService;

    // ========== 添加收藏 ==========
    @PostMapping("/add")
    public R add(@RequestParam Integer musicId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getMusicId, musicId);
        if (favoriteService.count(wrapper) > 0) {
            return R.error("已收藏");
        }

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setMusicId(musicId);
        boolean success = favoriteService.save(fav);
        return success ? R.right("收藏成功") : R.error("收藏失败");
    }

    // ========== 取消收藏 ==========
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }

        Favorite fav = favoriteService.getById(id);
        if (fav == null || !fav.getUserId().equals(userId)) {
            return R.error("无权操作");
        }
        boolean success = favoriteService.removeById(id);
        return success ? R.right("取消成功") : R.error("取消失败");
    }

    // ========== 修改备注 ==========
    @PutMapping("/update")
    public R update(@RequestBody Favorite favorite, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }

        Favorite exist = favoriteService.getById(favorite.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return R.error("无权操作");
        }
        exist.setRemark(favorite.getRemark());
        boolean success = favoriteService.updateById(exist);
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    // ========== 查询单个收藏（回显） ==========
    @GetMapping("/get/{id}")
    public R get(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return R.error("请先登录");
        }

        Favorite fav = favoriteService.getById(id);
        if (fav == null || !fav.getUserId().equals(userId)) {
            return R.error("无权查看");
        }
        return R.right().put("data", fav);
    }

    // ========== 收藏列表（分页，支持按歌曲名称搜索） ==========
    @GetMapping("/list")
    public PageResult<Favorite> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer limit,
                                     @RequestParam(required = false) String keyword,
                                     HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            PageResult<Favorite> pr = new PageResult<>();
            pr.setCode(1);
            pr.setMsg("请先登录");
            return pr;
        }

        // 1. 如果有关键词，先查询匹配的音乐ID
        List<Integer> musicIdList = null;
        if (StringUtils.isNotBlank(keyword)) {
            LambdaQueryWrapper<Music> musicWrapper = new LambdaQueryWrapper<>();
            musicWrapper.like(Music::getName, keyword)
                    .or().like(Music::getSinger, keyword)
                    .eq(Music::getStatus, 1);
            List<Music> musicList = musicService.list(musicWrapper);
            if (musicList.isEmpty()) {
                // 没有匹配的音乐，直接返回空结果
                PageResult<Favorite> pr = new PageResult<>();
                pr.setCode(0);
                pr.setMsg("查询成功");
                pr.setCount(0);
                pr.setData(new ArrayList<>());
                return pr;
            }
            musicIdList = musicList.stream().map(Music::getId).collect(Collectors.toList());
        }

        // 2. 分页查询收藏
        Page<Favorite> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        if (musicIdList != null && !musicIdList.isEmpty()) {
            wrapper.in(Favorite::getMusicId, musicIdList);
        }
        wrapper.orderByDesc(Favorite::getCreateTime);
        Page<Favorite> result = favoriteService.page(pageParam, wrapper);

        // 3. 填充音乐名称和歌手
        List<Integer> musicIds = result.getRecords().stream()
                .map(Favorite::getMusicId)
                .distinct()
                .collect(Collectors.toList());
        List<Music> musicList = new ArrayList<>();
        if (!musicIds.isEmpty()) {
            LambdaQueryWrapper<Music> musicWrapper = new LambdaQueryWrapper<>();
            musicWrapper.in(Music::getId, musicIds);
            musicList = musicService.list(musicWrapper);
        }
        Map<Integer, Music> musicMap = musicList.stream()
                .collect(Collectors.toMap(Music::getId, m -> m));

        result.getRecords().forEach(fav -> {
            Music music = musicMap.get(fav.getMusicId());
            if (music != null) {
                fav.setMusicName(music.getName());
                fav.setSinger(music.getSinger());
            } else {
                fav.setMusicName("未知");
                fav.setSinger("");
            }
        });

        // 4. 返回结果
        PageResult<Favorite> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }

    // ========== 歌曲搜索 ==========
    @GetMapping("/searchMusic")
    public R searchMusic(@RequestParam String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return R.right().put("data", new ArrayList<>());
        }
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Music::getName, keyword)
                .eq(Music::getStatus, 1)
                .orderByDesc(Music::getPlayCount)
                .last("limit 10");
        List<Music> list = musicService.list(wrapper);
        List<Map<String, Object>> result = list.stream().map(m -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", m.getId());
            map.put("name", m.getName());
            map.put("singer", m.getSinger());
            return map;
        }).collect(Collectors.toList());
        return R.right().put("data", result);
    }
}
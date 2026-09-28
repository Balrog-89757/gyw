package com.baidu.controller;

import com.baidu.pojo.Comment;
import com.baidu.pojo.Music;
import com.baidu.service.CommentService;
import com.baidu.service.MusicService;
import com.baidu.utils.PageResult;
import com.baidu.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private CommentService commentService;

    // ===== 管理员接口 =====
    @ResponseBody
    @GetMapping("/admin/list")
    public PageResult<Music> list(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(required = false) String keyword) {
        Page<Music> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(Music::getName, keyword).or().like(Music::getSinger, keyword);
        }
        wrapper.orderByDesc(Music::getCreateTime);
        Page<Music> result = musicService.page(pageParam, wrapper);
        PageResult<Music> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }

    @ResponseBody
    @PostMapping("/admin/add")
    public R add(
            @RequestParam String name,
            @RequestParam String singer,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer isHot,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) MultipartFile coverFile,
            @RequestParam MultipartFile musicFile) {

        Music music = new Music();
        music.setName(name);
        music.setSinger(singer);
        music.setAlbum(album);
        music.setDuration(duration != null ? duration : 0);
        music.setIsHot(isHot != null ? isHot : 0);
        music.setStatus(status != null ? status : 1);
        music.setPlayCount(0);

        if (coverFile != null && !coverFile.isEmpty()) {
            String coverUrl = saveFile(coverFile, "covers");
            music.setCoverUrl(coverUrl);
        } else {
            music.setCoverUrl("/img/default-cover.jpg");
        }
        if (musicFile != null && !musicFile.isEmpty()) {
            String fileUrl = saveFile(musicFile, "music");
            music.setFileUrl(fileUrl);
        } else {
            return R.error("音乐文件不能为空");
        }

        boolean success = musicService.save(music);
        return success ? R.right("添加成功") : R.error("添加失败");
    }

    @ResponseBody
    @PutMapping("/admin/update")
    public R update(
            @RequestParam Integer id,
            @RequestParam String name,
            @RequestParam String singer,
            @RequestParam(required = false) String album,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Integer isHot,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) MultipartFile coverFile,
            @RequestParam(required = false) MultipartFile musicFile) {

        Music exist = musicService.getById(id);
        if (exist == null) return R.error("音乐不存在");

        exist.setName(name);
        exist.setSinger(singer);
        exist.setAlbum(album);
        exist.setDuration(duration != null ? duration : 0);
        exist.setIsHot(isHot != null ? isHot : 0);
        exist.setStatus(status != null ? status : 1);

        if (coverFile != null && !coverFile.isEmpty()) {
            String coverUrl = saveFile(coverFile, "covers");
            exist.setCoverUrl(coverUrl);
        }
        if (musicFile != null && !musicFile.isEmpty()) {
            String fileUrl = saveFile(musicFile, "music");
            exist.setFileUrl(fileUrl);
        }

        boolean success = musicService.updateById(exist);
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    @ResponseBody
    @DeleteMapping("/admin/delete/{id}")
    public R delete(@PathVariable Integer id) {
        boolean success = musicService.removeById(id);
        return success ? R.right("删除成功") : R.error("删除失败");
    }

    @ResponseBody
    @GetMapping("/admin/get/{id}")
    public R get(@PathVariable Integer id) {
        Music music = musicService.getById(id);
        return music != null ? R.right().put("data", music) : R.error("音乐不存在");
    }

    // ===== 用户端：音乐详情页 =====
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Music music = musicService.getById(id);
        if (music == null) {
            return "error/404";
        }
        music.setPlayCount(music.getPlayCount() + 1);
        musicService.updateById(music);
        model.addAttribute("music", music);

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getMusicId, id)
                .orderByDesc(Comment::getCreateTime);
        List<Comment> comments = commentService.list(wrapper);
        model.addAttribute("comments", comments);

        return "detail";
    }

    // ===== 文件保存工具 =====
    private String saveFile(MultipartFile file, String subDir) {
        try {
            String basePath = System.getProperty("user.dir") + "/target/classes/static/" + subDir + "/";
            File dir = new File(basePath);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID() + ext;
            File dest = new File(basePath + newFilename);
            file.transferTo(dest);

            return "/" + subDir + "/" + newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
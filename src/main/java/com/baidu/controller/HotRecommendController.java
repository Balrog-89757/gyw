package com.baidu.controller;

import com.baidu.pojo.HotRecommend;
import com.baidu.service.HotRecommendService;
import com.baidu.utils.PageResult;
import com.baidu.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotRecommend")
public class HotRecommendController {

    @Autowired
    private HotRecommendService hotRecommendService;

    // ========== 分页查询（支持按推荐标题搜索） ==========
    @GetMapping("/admin/list")
    public PageResult<HotRecommend> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer limit,
                                         @RequestParam(required = false) String keyword) {
        Page<HotRecommend> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<HotRecommend> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(HotRecommend::getRecommendTitle, keyword);
        }

        wrapper.orderByAsc(HotRecommend::getSortOrder);
        Page<HotRecommend> result = hotRecommendService.page(pageParam, wrapper);

        PageResult<HotRecommend> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }

    // ========== 添加 ==========
    @PostMapping("/admin/add")
    public R add(@RequestBody HotRecommend hotRecommend) {
        boolean success = hotRecommendService.save(hotRecommend);
        return success ? R.right("添加成功") : R.error("添加失败");
    }

    // ========== 修改 ==========
    @PutMapping("/admin/update")
    public R update(@RequestBody HotRecommend hotRecommend) {
        boolean success = hotRecommendService.updateById(hotRecommend);
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    // ========== 删除 ==========
    @DeleteMapping("/admin/delete/{id}")
    public R delete(@PathVariable Integer id) {
        boolean success = hotRecommendService.removeById(id);
        return success ? R.right("删除成功") : R.error("删除失败");
    }

    // ========== 查询单个 ==========
    @GetMapping("/admin/get/{id}")
    public R get(@PathVariable Integer id) {
        HotRecommend hotRecommend = hotRecommendService.getById(id);
        return hotRecommend != null ? R.right().put("data", hotRecommend) : R.error("记录不存在");
    }
}
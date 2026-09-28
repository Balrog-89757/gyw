package com.baidu.controller;

import com.baidu.pojo.User;
import com.baidu.service.UserService;
import com.baidu.utils.PageResult;
import com.baidu.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Slf4j  // ✅ 添加日志注解，方便调试
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ==================== 登录/注册（公开接口） ====================

    @PostMapping("/login")
    public R login(@RequestBody User loginUser, HttpSession session) {
        log.info("登录请求：username={}", loginUser.getUsername());  //  添加日志
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return R.error("用户名和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);

        if (user == null) {
            return R.error("用户不存在");
        }
        if (user.getStatus() == 0) {
            return R.error("账号已被禁用，请联系管理员");
        }

        if (!password.equals(user.getPassword())) {
            return R.error("密码错误");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("user", user);

        user.setPassword(null);
        if (user.getRole() == 1) {
            return R.right("登录成功").put("data", user).put("redirect", "/toIndex");
        } else {
            return R.right("登录成功").put("data", user).put("redirect", "/");
        }
    }

    @PostMapping("/register")
    public R register(@RequestBody User user) {
        log.info("注册请求：username={}", user.getUsername());  //  添加日志
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return R.error("用户名和密码不能为空");
        }
        if (password.length() < 6) {
            return R.error("密码长度不能少于6位");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userService.count(wrapper) > 0) {
            return R.error("用户名已存在");
        }

        user.setPassword(password);
        user.setRole(0);
        user.setStatus(1);

        boolean success = userService.save(user);
        return success ? R.right("注册成功").put("redirect", "/toLogin") : R.error("注册失败，请稍后再试");
    }

    @PostMapping("/logout")
    public R logout(HttpSession session) {
        session.invalidate();
        return R.right("退出成功").put("redirect", "/toLogin");
    }

    // ==================== 管理员接口 ====================

    @GetMapping("/admin/list")
    public PageResult<User> list(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required = false) String keyword) {
        log.info("查询用户列表：page={}, limit={}, keyword={}", page, limit, keyword);  //  添加日志
        Page<User> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(pageParam, wrapper);
        PageResult<User> pr = new PageResult<>();
        pr.setCode(0);
        pr.setMsg("查询成功");
        pr.setCount((int) result.getTotal());
        pr.setData(result.getRecords());
        return pr;
    }

    @PostMapping("/admin/add")
    public R add(@RequestBody User user) {
        log.info("添加用户：username={}", user.getUsername());  //  添加日志
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return R.error("用户名和密码不能为空");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userService.count(wrapper) > 0) {
            return R.error("用户名已存在");
        }
        if (user.getRole() == null) user.setRole(0);
        if (user.getStatus() == null) user.setStatus(1);
        boolean success = userService.save(user);
        return success ? R.right("添加成功") : R.error("添加失败");
    }

    @PutMapping("/admin/update")
    public R update(@RequestBody User user) {
        log.info("修改用户：id={}, username={}", user.getId(), user.getUsername());  // ✅ 添加日志
        if (StringUtils.isBlank(user.getPassword())) {
            User exist = userService.getById(user.getId());
            if (exist != null) {
                user.setPassword(exist.getPassword());
            }
        }
        boolean success = userService.updateById(user);
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    @DeleteMapping("/admin/delete/{id}")
    public R delete(@PathVariable Integer id) {
        log.info("删除用户：id={}", id);  //  添加日志
        boolean success = userService.removeById(id);
        return success ? R.right("删除成功") : R.error("删除失败");
    }

    @GetMapping("/admin/get/{id}")
    public R get(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return user != null ? R.right().put("data", user) : R.error("用户不存在");
    }

    // ==================== 普通用户个人中心 ====================

    @GetMapping("/profile")
    public R profile(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        log.info("个人中心查询：userId={}", userId);  //  添加日志
        if (userId == null) return R.error("请先登录");
        User user = userService.getById(userId);
        if (user != null) user.setPassword(null);
        return user != null ? R.right().put("data", user) : R.error("用户不存在");
    }

    @PutMapping("/profile/update")
    public R updateProfile(@RequestBody User user, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        log.info("修改个人信息：userId={}, nickname={}, email={}, phone={}",
                userId, user.getNickname(), user.getEmail(), user.getPhone());  // ✅ 添加日志
        if (userId == null) return R.error("请先登录");
        user.setId(userId);
        user.setUsername(null);
        user.setRole(null);
        user.setPassword(null);
        boolean success = userService.updateById(user);
        if (success) {
            //  更新 session 中的用户信息
            User updatedUser = userService.getById(userId);
            if (updatedUser != null) {
                updatedUser.setPassword(null);
                session.setAttribute("user", updatedUser);
            }
        }
        return success ? R.right("修改成功") : R.error("修改失败");
    }

    @PutMapping("/profile/changePwd")
    public R changePwd(@RequestParam String oldPwd,
                       @RequestParam String newPwd,
                       HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        log.info("修改密码：userId={}", userId);  //  添加日志
        if (userId == null) return R.error("请先登录");
        if (StringUtils.isBlank(newPwd) || newPwd.length() < 6) {
            return R.error("新密码不能少于6位");
        }
        User user = userService.getById(userId);
        if (!oldPwd.equals(user.getPassword())) {
            log.warn("原密码错误：userId={}", userId);  //  添加警告日志
            return R.error("原密码错误");
        }
        user.setPassword(newPwd);
        boolean success = userService.updateById(user);
        if (success) {
            //  更新 session 中的用户信息
            session.setAttribute("user", user);
        }
        return success ? R.right("密码修改成功") : R.error("修改失败");
    }
}
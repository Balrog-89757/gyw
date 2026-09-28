package com.baidu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.baidu.dao")  // 扫描 Mapper 接口
public class MusicBoxApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicBoxApplication.class, args);
        System.out.println("🎵 在线智能音乐盒启动成功！");
    }
}

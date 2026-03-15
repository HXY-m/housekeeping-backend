package com.euler.housekeepingservice;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {

        // ================= 动态获取项目路径 =================
        // user.dir 默认获取的是当前项目的根目录 (例如: F:\Desktop\软件工程\HomeService\Backend\housekeeping-service)
        String projectPath = System.getProperty("user.dir");
        String DIR_PATH = projectPath + "/src/main/java";
        String XML_DIR_PATH = projectPath + "/src/main/resources/mapper";
        // ====================================================

        // 1. 数据库配置 (需修改为你的密码)
        String url = "jdbc:mysql://127.0.0.1:3306/housekeeping_service?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456"; // TODO: 改为你自己的数据库密码

        FastAutoGenerator.create(url, username, password)
                // 2. 全局配置
                .globalConfig(builder -> {
                    builder.author("Euler") // 设置作者
                            .disableOpenDir()
                            .enableSwagger() // 开启 Swagger/Knife4j 注解
                            .outputDir(DIR_PATH); // 动态指定 Java 代码输出目录
                })
                // 3. 包配置
                .packageConfig(builder -> {
                    builder.parent("com.euler.housekeepingservice") // 设置父包名
                            .entity("model.entity") // 实体类放在 model/entity 目录下
                            // 动态配置 Mapper XML 文件的生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, XML_DIR_PATH));
                })
                // 4. 策略配置 (核心)
                .strategyConfig(builder -> {
                    // 需要生成的表名集合
                    builder.addInclude("sys_user", "biz_customer", "biz_professional", "biz_service", "biz_order")
                            .addTablePrefix("sys_", "biz_") // 过滤掉表前缀

                            // 实体类(Entity)策略配置
                            .entityBuilder()
                            .enableLombok() // 开启 Lombok 注解
                            .enableTableFieldAnnotation() // 生成实体类字段注解
                            .logicDeleteColumnName("is_deleted") // 指定逻辑删除字段名
                            .logicDeletePropertyName("isDeleted")
                            .addTableFills(new Column("create_time", FieldFill.INSERT)) // 自动填充: 插入时
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // 自动填充: 插入或更新时

                            // 控制器(Controller)策略配置
                            .controllerBuilder()
                            .enableRestStyle() // 开启 @RestController 风格

                            // 服务(Service)策略配置
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 规范 Service 接口名 (去掉默认的 I 前缀)

                            // 数据访问层(Mapper)策略配置
                            .mapperBuilder()
                            .enableMapperAnnotation(); // 开启 @Mapper 注解
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 引擎
                .execute(); // 执行生成
    }
}
package com.euler.housekeepingservice.controller;


import com.euler.housekeepingservice.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class UploadController {

    // 将文件存在项目根目录下的 uploads 文件夹中
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        try {
            // 1. 确保目录存在
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 生成唯一文件名 (防止重名覆盖)
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 3. 保存文件到本地磁盘
            File dest = new File(UPLOAD_DIR + newFileName);
            file.transferTo(dest);

            // 4. 拼接能让前端访问的 URL (假设后端跑在 8080 端口)
            // 注意：真实线上环境这里应该配置为域名
            String fileUrl = "http://localhost:8080/api/uploads/" + newFileName;

            return Result.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(500, "文件上传失败");
        }
    }
}
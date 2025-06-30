package com.neuedu.controller;

import com.neuedu.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("upload")
@CrossOrigin
public class UploadController {

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    @PostMapping("/uploadFiles")
    public Result doUploadFiles(@RequestParam(value = "cover", required = false) MultipartFile cover,
                                @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {
        try {
            MultipartFile file = cover != null ? cover : video;
            if (file == null || file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名（原文件名+UUID+扩展名）
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // 保存文件到磁盘
            Files.write(filePath, file.getBytes());

            // 构建返回结果（URL需根据实际部署调整）
            String fileUrl = "/uploads/" + uniqueFilename;
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            return Result.success(data);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/removeFile")
    public Result remove(String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            return Result.error("文件路径不存在");
        }
        Path path = Paths.get(uploadDir + "/" + fileName);
        try {
            Files.delete(path);
            return Result.success();
        } catch (NoSuchFileException e) {
            return Result.error("文件不存在");
        } catch (DirectoryNotEmptyException e) {
            System.out.println("目录不为空，无法删除！");
        } catch (IOException e) {
            System.out.println("发生了IO错误，删除失败：" + e.getMessage());
        }
        return null;
    }


    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }


}

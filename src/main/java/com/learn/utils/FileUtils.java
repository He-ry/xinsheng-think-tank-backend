package com.learn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    @Value(value="${host}")
    private String host;

    public String uploadToLocal(MultipartFile file, String uploadDir) {
        log.info("uploadDir = {}", (Object)uploadDir);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        try {
            String timeDir = String.valueOf(System.currentTimeMillis());
            Path basePath = Paths.get(uploadDir, new String[0]);
            Path targetDir = basePath.resolve(timeDir);
            Files.createDirectories(targetDir, new FileAttribute[0]);
            String originalFilename = Paths.get(file.getOriginalFilename(), new String[0]).getFileName().toString();
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
            Path targetPath = targetDir.resolve(newFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return this.host + "/files/" + timeDir + "/" + newFileName;
        }


        catch (Exception e) {
            log.error("文件上传失败", (Throwable)e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private boolean isVideo(MultipartFile file, String filename) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) {
            return true;
        }
        String lower = filename.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi") || lower.endsWith(".mkv") || lower.endsWith(".flv");
    }


    public void delete(String fileUrl, String uploadDir) {
        Path filePath = Paths.get(uploadDir, fileUrl.replaceFirst("^files/", ""));
        try {
            Files.deleteIfExists(filePath);
        }


        catch (IOException e) {
            log.error("物理删除文件失败: {}", (Object)filePath, (Object)e);
        }
    }
}

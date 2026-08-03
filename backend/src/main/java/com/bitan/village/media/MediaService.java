package com.bitan.village.media;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaService {
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    private final Path uploadDirectory;

    public MediaService(@Value("${app.upload-dir:./uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void createUploadDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("无法创建图片上传目录", exception);
        }
    }

    public UploadedMedia save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片不能超过 10MB");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("仅支持 JPEG 或 PNG 图片");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException exception) {
            throw new IllegalArgumentException("图片内容无法读取");
        }
        if (image == null || image.getWidth() < 320 || image.getHeight() < 200) {
            throw new IllegalArgumentException("图片无效或尺寸过小");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename).normalize();
        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("图片保存失败", exception);
        }
        return new UploadedMedia("/api/media/" + filename, filename, image.getWidth(), image.getHeight());
    }

    public MediaFile load(String filename) {
        if (!filename.matches("[a-f0-9-]{36}\\.(jpg|png)")) {
            throw new IllegalArgumentException("图片地址无效");
        }
        Path file = uploadDirectory.resolve(filename).normalize();
        if (!file.startsWith(uploadDirectory) || !Files.isRegularFile(file)) {
            return null;
        }
        String contentType = filename.endsWith(".png") ? "image/png" : "image/jpeg";
        return new MediaFile(new FileSystemResource(file), contentType);
    }

    public record UploadedMedia(String url, String filename, int width, int height) {}

    public record MediaFile(Resource resource, String contentType) {}
}

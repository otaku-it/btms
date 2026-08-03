package com.bitan.village.media;

import com.bitan.village.shared.ResourceNotFoundException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/api/admin/media")
    public MediaService.UploadedMedia upload(@RequestParam("image") MultipartFile image) {
        return mediaService.save(image);
    }

    @GetMapping("/api/media/{filename}")
    public ResponseEntity<?> get(@PathVariable String filename) {
        MediaService.MediaFile file = mediaService.load(filename);
        if (file == null) {
            throw new ResourceNotFoundException("图片不存在");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .body(file.resource());
    }
}

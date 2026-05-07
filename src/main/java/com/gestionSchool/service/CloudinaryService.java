package com.gestionSchool.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${CLOUDINARY_CLOUD_NAME}")
    private String cloudName;

    @Value("${CLOUDINARY_API_KEY}")
    private String apiKey;

    @Value("${CLOUDINARY_API_SECRET}")
    private String apiSecret;

    public String uploadImage(MultipartFile file) throws IOException {
        Map config = Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        );

        Cloudinary cloudinary = new Cloudinary(config);

        Map result = cloudinary.uploader().upload(file.getBytes(), 
                Map.of("public_id", "student_" + System.currentTimeMillis(),
                        "folder", "gestionSchool"));

        return (String) result.get("secure_url");
    }

    public String getResizedUrl(String imageUrl, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        Cloudinary cloudinary = new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
        
        return cloudinary.url()
                .transformation(new Transformation().width(width).height(height).crop("fill"))
                .generate(imageUrl);
    }
}
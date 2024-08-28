package com.example.appbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Response;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class S3Service {
    private final S3Client s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .metadata(metadata)
                .contentType(file.getContentType())
                .build();

        S3Response response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        return fileName;
    }

    public String getFileUrl(String fileName) {
        URL url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName));
        return url.toString();
    }


}

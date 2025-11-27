package com.T4.storyTracks.service;


import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketNm;
    private final Region region;

    public S3Service(
            @Value("${AWS_ACCESS_KEY_ID}") String accessKey,
            @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
            @Value("${AWS_REGION}") String region,
            @Value("${AWS_S3_BUCKET}") String bucketNm
    ) {
        this.bucketNm = bucketNm;
        this.region = Region.of(region);
        this.s3Client = S3Client.builder()
                .region(this.region)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                ).build();
    }
    public String uploadProfileImg(MultipartFile file) throws IOException {
        String key = "profiles/" + Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();
        uploadToS3(file, key);
        return key;
    }

    public List<String> uploadPostFiles(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String key = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();
            uploadToS3(file, "posts/"+key);
            urls.add(key);
        }
        return urls;
    }

    private void uploadToS3(MultipartFile file, String key) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketNm)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
    }

}

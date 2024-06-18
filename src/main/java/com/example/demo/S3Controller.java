package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
public class S3Controller {

    private final S3Client s3Client;
    private final String bucketName = "you_S3_bucket_name";
    private final String fileName = "data.txt";

    public S3Controller() {
        this.s3Client = S3Client.builder()
                .region(Region.EU_CENTRAL_1) // Replace with your AWS region
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .build();
    }

    @GetMapping("/")
    public String getDataFromS3() {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseInputStream<GetObjectResponse> s3ObjectResponse = s3Client.getObject(getObjectRequest);

            // Read file content
            String fileContent = new BufferedReader(new InputStreamReader(s3ObjectResponse, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            // Create JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writeValueAsString(new DataResponse(fileContent));

            return jsonOutput;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to retrieve file: " + e.getMessage();
        }
    }

    // Custom response class for JSON serialization
    static class DataResponse {
        private String content;

        public DataResponse(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

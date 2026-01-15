//package com.example.apigmac.infra;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class AwsConfig {
//
//    @Value("${aws.region}")
//    private String awsRegion;
//
//    @Value("${aws.accessKeyId}")
//    private String accessKey;
//
//    @Value("${aws.secretKey}")
//    private String secretKey;
//
//    @Value("${aws.sessionToken}")
//    private String sessionToken;
//
//    @Bean
//    public S3Client s3Client() {
//
//        AwsSessionCredentials credentials =
//                AwsSessionCredentials.create(
//                        accessKey.trim(),
//                        secretKey.trim(),
//                        sessionToken.trim()
//                );
//
//        return S3Client.builder()
//                .region(Region.of(awsRegion))
//                .credentialsProvider(
//                        StaticCredentialsProvider.create(credentials)
//                )
//                .build();
//    }
//}

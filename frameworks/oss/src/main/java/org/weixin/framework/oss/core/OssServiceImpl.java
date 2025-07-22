package org.weixin.framework.oss.core;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.weixin.framework.oss.config.CloudServiceEnum;
import org.weixin.framework.oss.config.OssProperties;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OssProperties ossProperties;

    private final S3AsyncClient s3AsyncClient;

    private final S3Presigner s3Presigner;

    // 用于异步读取流
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(ossProperties.getBucketName())
                .key(key)
                .build();
        CompletableFuture<DeleteObjectResponse> responseFuture = s3AsyncClient.deleteObject(deleteObjectRequest);
        responseFuture.whenComplete((deleteRes, ex) -> {
            if (deleteRes != null) {
                log.info("S3 key: {} was deleted response: {}", key, JSONUtil.toJsonStr(deleteRes));
            } else {
                log.error("An S3 exception occurred during delete key: {}", key, ex);
            }
        });
    }

    @Override
    public String getPresignedUrl(String key, Duration expiredTime) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(ossProperties.getBucketName())
                .key(key)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiredTime)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    @Override
    public String uploadInputStream(String key, InputStream inputStream) {
        AsyncRequestBody asyncRequestBody = AsyncRequestBody.fromInputStream(inputStream, null, executorService);
        CompletableFuture<PutObjectResponse> responseFuture = s3AsyncClient.putObject(builder -> builder.bucket(ossProperties.getBucketName()).key(key), asyncRequestBody);
        responseFuture.handle((putRes, ex) -> {
            if (putRes != null) {
                log.info("S3 key: {} was uploaded response: {}", key, JSONUtil.toJsonStr(putRes));
                return putRes;
            }
            throw new RuntimeException("An S3 exception occurred during uploaded key: " + key, ex);
        });
        PutObjectResponse putObjectResponse = responseFuture.join();
        if (Objects.nonNull(putObjectResponse)) {
            return getAccessUrl(key);
        }
        return StrUtil.EMPTY;
    }

    @Override
    public String uploadFile(String key, File file) {
        AsyncRequestBody asyncRequestBody = AsyncRequestBody.fromFile(file);
        CompletableFuture<PutObjectResponse> responseFuture = s3AsyncClient.putObject(builder -> builder.bucket(ossProperties.getBucketName()).key(key), asyncRequestBody);
        responseFuture.handle((putRes, ex) -> {
            if (putRes != null) {
                log.info("S3 key: {} was uploaded response: {}", key, JSONUtil.toJsonStr(putRes));
                return putRes;
            }
            throw new RuntimeException("An S3 exception occurred during uploaded key: " + key, ex);
        });
        PutObjectResponse putObjectResponse = responseFuture.join();
        if (Objects.nonNull(putObjectResponse)) {
            return getAccessUrl(key);
        }
        return StrUtil.EMPTY;
    }


    private String getAccessUrl(String key) {
        String endpoint = ossProperties.getEndpoint();
        String bucketName = ossProperties.getBucketName();
        // MINIO的桶名称放置在域名后
        if (CloudServiceEnum.MINIO == ossProperties.getCloudServiceEnum()) {
            return String.join(StrUtil.SLASH, endpoint, bucketName, key);
        }
        // https:// 或者 http://
        String httpHeader = ReUtil.getGroup0("https?://", endpoint);
        String domain = endpoint.replace(httpHeader, StrUtil.EMPTY);
        return String.join(StrUtil.SLASH, String.join(StrUtil.DOT, httpHeader + bucketName, domain), key);
    }

    public static void main(String[] args) {
        String endpoint = "https://cos.ap-guangzhou.myqcloud.com";
        String httpHeader = ReUtil.getGroup0("https?://", endpoint);
        String domain = endpoint.replace(httpHeader, StrUtil.EMPTY);
        System.out.println(domain);
    }

}

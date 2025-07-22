package org.weixin.framework.oss.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.weixin.framework.oss.core.OssService;
import org.weixin.framework.oss.core.OssServiceImpl;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;


@EnableConfigurationProperties(OssProperties.class)
public class OssConfiguration {

    /**
     * S3异步上传客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public S3AsyncClient s3AsyncClient(OssProperties ossProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(credentials);
        return S3AsyncClient.crtBuilder()
                .credentialsProvider(staticCredentialsProvider)
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .region(StrUtil.isBlank(ossProperties.getRegionName()) ? Region.US_EAST_1 : Region.of(ossProperties.getRegionName()))
                // 避免一些厂商没有校验和
                .checksumValidationEnabled(false)
                // 文件传输配置
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * 1024 * 1024L)
                // Path-style URL 路径风格
                .forcePathStyle(true)
                .build();
    }

    /**
     * 高级文件传输工具
     */
    @Bean
    @ConditionalOnMissingBean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }

    /**
     * S3预签名URL生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner(OssProperties ossProperties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(credentials);
        return S3Presigner.builder()
                .credentialsProvider(staticCredentialsProvider)
                .region(StrUtil.isBlank(ossProperties.getRegionName()) ? Region.US_EAST_1 : Region.of(ossProperties.getRegionName()))
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        // 避免一些厂商没有校验和
                        .checksumValidationEnabled(false)
                        // Path-style URL 路径风格
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }


    @Bean
    @ConditionalOnMissingBean
    public OssService ossService(OssProperties ossProperties,
                                 S3AsyncClient s3AsyncClient,
                                 S3Presigner s3Presigner) {
        return new OssServiceImpl(ossProperties, s3AsyncClient, s3Presigner);
    }


}

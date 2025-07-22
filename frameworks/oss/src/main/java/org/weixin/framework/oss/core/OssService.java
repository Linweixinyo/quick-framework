package org.weixin.framework.oss.core;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

public interface OssService {

    void deleteObject(String key);

    String getPresignedUrl(String key, Duration expiredTime);

    String uploadInputStream(String key, InputStream inputStream);

    String uploadFile(String key, File file);
}

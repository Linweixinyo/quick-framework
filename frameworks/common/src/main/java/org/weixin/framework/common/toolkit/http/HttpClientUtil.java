package org.weixin.framework.common.toolkit.http;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class HttpClientUtil {

    private static final CloseableHttpClient HTTP_CLIENT;

    public static final Long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(5);

    public static final Long SOCKET_TIMEOUT = TimeUnit.MINUTES.toMillis(2);

    static {

        // 配置HTTP,HTTPS连接工厂，用于绕过HTTPS连接认证
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setValidateAfterInactivity((int) TimeUnit.MINUTES.toMillis(5));


        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return TimeUnit.SECONDS.toMillis(60);
        };

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT.intValue())
                .setConnectTimeout(CONNECTION_TIMEOUT.intValue())
                .setSocketTimeout(SOCKET_TIMEOUT.intValue())
                .setRedirectsEnabled(true)
                .build();

        HTTP_CLIENT = HttpClients.custom()
                .setConnectionManagerShared(true)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
                .build();
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url)
                .setCharset(Consts.UTF_8);
        if(Objects.nonNull(params) && !params.isEmpty()) {
            params.forEach(uriBuilder::addParameter);
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        addHeaders(httpGet, headers);
        return doHttp(httpGet);
    }

    public static String doPost(String url, Object paramObj, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost, headers);
        String paramsStr = paramObj instanceof String ? (String) paramObj : JSONUtil.toJsonStr(paramObj);
        StringEntity stringEntity = new StringEntity(paramsStr, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        return doHttp(httpPost);
    }

    private static void addHeaders(HttpRequestBase requestBase, Map<String, String> headers) {
        if(Objects.nonNull(headers) && !headers.isEmpty()) {
            headers.forEach(requestBase::addHeader);
        }
    }

    private static String doHttp(HttpRequestBase request) throws IOException {
        ResponseHandler<String> handler = new BasicResponseHandler();
        long startPoint = System.currentTimeMillis();
        String response;
        try {
            response = HTTP_CLIENT.execute(request, handler);
            log.info("请求耗时【{}】ms, 接口返回信息【{}】", System.currentTimeMillis() - startPoint, response);
        } catch (IOException e) {
            log.error("HTTP 请求失败，URL: {}，错误信息: {}", request.getURI(), e.getMessage(), e);
            throw e;
        }
        return response;
    }

    // 展示如何对接SSE接口
    // 公共的websocket和sse接口 https://websocket.org/tools/websocket-echo-server/#websocket-echo-server
    public static void main(String[] args) throws IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet("https://echo.websocket.org/.sse");
        httpGet.setHeader("Accept", "text/event-stream");
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(response.getEntity().getContent()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析SSE事件
                if (line.startsWith("data:")) {
                    String eventData = line.substring(5).trim();
                    System.out.println("Received event: " + eventData);
                }
                // 处理其他事件类型（如event:、id:等）
            }
        }
    }


}

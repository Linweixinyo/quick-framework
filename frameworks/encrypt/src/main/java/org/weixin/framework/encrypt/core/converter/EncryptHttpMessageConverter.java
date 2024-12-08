package org.weixin.framework.encrypt.core.converter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.weixin.framework.encrypt.config.EncryptProperties;
import org.weixin.framework.encrypt.core.Encryptable;
import org.weixin.framework.encrypt.core.callback.EncryptProcessorCallback;
import org.weixin.framework.encrypt.core.constant.EncryptConstant;
import org.weixin.framework.encrypt.core.req.EncryptRequest;
import org.weixin.framework.encrypt.core.res.EncryptRes;
import org.weixin.framework.encrypt.core.service.SignatureService;
import org.weixin.framework.encrypt.toolkit.AESUtil;
import org.weixin.framework.encrypt.toolkit.RSAUtil;
import org.weixin.framework.web.toolkit.ServletUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
public class EncryptHttpMessageConverter extends MappingJackson2HttpMessageConverter implements GenericHttpMessageConverter<Object> {

    private final EncryptProperties encryptProperties;

    private final SignatureService signatureService;

    private final EncryptProcessorCallback encryptProcessorCallback;

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (type instanceof Class<?> clazz) {
            return readInternal(clazz, inputMessage);
        }
        return super.read(type, contextClass, inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 接受参数为Encryptable
        if (Encryptable.class.isAssignableFrom(clazz)) {
            String bodyStr = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
            EncryptRequest encryptRequest = JSONUtil.toBean(bodyStr, EncryptRequest.class);
            try {
                // 使用私钥解密得到AES密钥
                String privateKey = encryptProperties.loadPrivateKey();
                String aesKey = RSAUtil.decrypt(encryptRequest.getAesKey(), privateKey);
                HttpServletRequest request = ServletUtil.getRequest();
                request.setAttribute(EncryptConstant.AES_KEY_ATTRIBUTE, aesKey);
                String encryptData = encryptRequest.getData();
                // 校验签名
                boolean checkSign = Objects.nonNull(encryptProperties.getCheckSign()) && encryptProperties.getCheckSign();
                if (checkSign && signatureService.checkHeader(request)) {
                    String signature = signatureService.buildSignature(encryptData);
                    if (!Objects.equals(signature, encryptRequest.getSign())) {
                        throw new IllegalArgumentException("Signature parameters is error");
                    }
                }

                // 使用AES密钥解密数据
                encryptProcessorCallback.decryptBefore(encryptData, aesKey);
                String data = AESUtil.decrypt(encryptData, aesKey);
                Object requestData = JSONUtil.toBean(data, clazz);
                encryptProcessorCallback.decryptAfter(requestData);
                return requestData;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else {
            return super.readInternal(clazz, inputMessage);
        }

    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (Encryptable.class.isAssignableFrom(object.getClass())) {
            HttpServletRequest request = ServletUtil.getRequest();
            String aesKey = (String) request.getAttribute(EncryptConstant.AES_KEY_ATTRIBUTE);
            if (StrUtil.isBlank(aesKey)) {
                super.writeInternal(object, type, outputMessage);
            } else {
                encryptProcessorCallback.encryptBefore(object);
                String encryptData = AESUtil.encrypt(JSONUtil.toJsonStr(object), aesKey);
                EncryptRes encryptResult = new EncryptRes()
                        .setData(encryptData);
                encryptProcessorCallback.encryptAfter(encryptData);
                super.writeInternal(encryptResult, type, outputMessage);
            }
        } else {
            super.writeInternal(object, type, outputMessage);
        }

    }
}

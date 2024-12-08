package org.weixin.framework.encrypt.core.resolver;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.weixin.framework.encrypt.config.EncryptProperties;
import org.weixin.framework.encrypt.core.Encryptable;
import org.weixin.framework.encrypt.core.callback.EncryptProcessorCallback;
import org.weixin.framework.encrypt.core.constant.EncryptConstant;
import org.weixin.framework.encrypt.core.service.SignatureService;
import org.weixin.framework.encrypt.toolkit.AESUtil;
import org.weixin.framework.encrypt.toolkit.RSAUtil;

import java.util.Objects;

@RequiredArgsConstructor
public class EncryptableArgumentResolver implements HandlerMethodArgumentResolver {

    private final EncryptProperties encryptProperties;

    private final SignatureService signatureService;

    private final EncryptProcessorCallback encryptProcessorCallback;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Encryptable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 使用私钥解密得到AES密钥
        String privateKey = encryptProperties.loadPrivateKey();
        String aesKey = RSAUtil.decrypt(webRequest.getParameter("aesKey"), privateKey);
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        request.setAttribute(EncryptConstant.AES_KEY_ATTRIBUTE, aesKey);
        String encryptData = webRequest.getParameter("data");
        String signOfRequest = webRequest.getParameter("sign");
        // 校验签名
        boolean checkSign = Objects.nonNull(encryptProperties.getCheckSign()) && encryptProperties.getCheckSign();
        if (checkSign && signatureService.checkHeader(request)) {
            String signature = signatureService.buildSignature(encryptData);
            if (!Objects.equals(signature, signOfRequest)) {
                throw new IllegalArgumentException("Signature parameters is error");
            }
        }
        // 使用AES密钥解密数据
        encryptProcessorCallback.decryptBefore(encryptData, aesKey);
        String data = AESUtil.decrypt(encryptData, aesKey);
        Object requestData = JSONUtil.toBean(data, parameter.getParameterType());
        encryptProcessorCallback.decryptAfter(requestData);
        return requestData;
    }
}

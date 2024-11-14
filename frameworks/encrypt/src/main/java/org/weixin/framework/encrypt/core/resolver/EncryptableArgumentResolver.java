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
import org.weixin.framework.encrypt.core.constant.EncryptConstant;
import org.weixin.framework.encrypt.toolkit.AESUtil;
import org.weixin.framework.encrypt.toolkit.RSAUtil;

@RequiredArgsConstructor
public class EncryptableArgumentResolver implements HandlerMethodArgumentResolver {

    private final EncryptProperties encryptProperties;

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
        // 使用AES密钥解密数据
        String data = AESUtil.decrypt(webRequest.getParameter("data"), aesKey);
        return JSONUtil.toBean(data, parameter.getParameterType());
    }
}

package com.ansim.map.global.common;

import com.ansim.map.global.common.LoginUser;
import org.springframework.core.MethodParameter; // ★ 이 부분으로 수정해야 합니다!
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginUser 어노테이션이 있고, 파라미터 타입이 String인지 체크
        return parameter.hasParameterAnnotation(LoginUser.class) &&
                parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 익명 사용자인 경우 null 반환
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        // 인증 객체의 이름을 반환 (보통 JWT 필터에서 이메일을 Name으로 넣어둠)
        return authentication.getName();
    }
}
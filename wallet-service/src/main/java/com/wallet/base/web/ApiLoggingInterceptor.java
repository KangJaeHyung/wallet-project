package com.wallet.base.web;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, startTime);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        MDC.put(REQUEST_ID_ATTRIBUTE, requestId);
        log.info("[{}] {} {}", requestId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object requestId = request.getAttribute(REQUEST_ID_ATTRIBUTE);
        Object startTime = request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);

        long durationMs = 0L;
        if (startTime instanceof Long startedAt) {
            durationMs = System.currentTimeMillis() - startedAt;
        }

        log.info("[{}] {} {} -> status={} ({} ms)",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs);

        MDC.remove(REQUEST_ID_ATTRIBUTE);
    }
}

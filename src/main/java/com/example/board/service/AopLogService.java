package com.example.board.service;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

//AOP를 통해 모든 request에 대한 로그 출력
@Slf4j
@Aspect
@Component
public class AopLogService {

    @Around("execution(* com.example.board.controller..*.*(..))")
    public Object controllerLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); // request 정보를 가져온다.
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("controller name : ", proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName());
        jsonObject1.put("method name : ", proceedingJoinPoint.getSignature().getName());
        Enumeration<String> req_bodys = req.getParameterNames();
        jsonObject1.put("method", req.getMethod());
        log.info("request : {}", jsonObject1);
//        get호출 또는 form데이터 post
        JSONObject jsonObject2 = new JSONObject();
        while (req_bodys.hasMoreElements()) {
            String body = req_bodys.nextElement();
            String replaceBody = body.replaceAll("\\.", "-");
            jsonObject2.put(replaceBody, req.getParameter(body));
            jsonObject2.put(body, req.getParameter(body));
        }
        log.info("request params : {}", jsonObject2);
        return proceedingJoinPoint.proceed();
    }
}
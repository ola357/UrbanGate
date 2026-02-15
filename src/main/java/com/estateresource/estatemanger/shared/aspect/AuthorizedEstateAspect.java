package com.estateresource.estatemanger.shared.aspect;

import com.estateresource.estatemanger.security.jwt.JwtUtils;
import com.estateresource.estatemanger.shared.entity.Estate;
import com.estateresource.estatemanger.shared.repository.EstateRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;


@Aspect
@Component
@Slf4j
public class AuthorizedEstateAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private EstateRepository estateRepository;

    @Around("@annotation(authorizedEstate)")
    public Object getAuthorizedEstate(ProceedingJoinPoint joinPoint, AuthorizedEstate authorizedEstate) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String methodName = methodSignature.getMethod().getName();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();

        Annotation[][] annotations;
        try {
            annotations = joinPoint
                    .getTarget()
                    .getClass()
                    .getMethod(methodName, parameterTypes)
                    .getParameterAnnotations();
        }catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(e.getMessage());
        }

        Integer estateAnnotationPosition = null;
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType().equals(OnboardedEstate.class)) {
                    estateAnnotationPosition = i;
                    break;
                }
            }
            if (estateAnnotationPosition != null) {
                break;
            }
        }

        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Claims claims = jwtUtils.extractAllClaims(token);

        log.info("AuthorizedEstate: {}", claims);
        Long estateId = claims.get("estate", Long.class);
        log.info("Estate Id: {}", estateId);
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(RuntimeException::new);

        if (estateAnnotationPosition != null) {
            log.info("Estate Position: {}", estateAnnotationPosition);
            log.info("Estate Name: {}", estate.getName());
            args[estateAnnotationPosition] = estate;
        }

        return joinPoint.proceed(args);
    }
}

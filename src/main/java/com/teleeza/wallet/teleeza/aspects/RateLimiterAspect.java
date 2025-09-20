package com.teleeza.wallet.teleeza.aspects;

//import com.homespot.phs.configs.RateLimitConfig;
import com.teleeza.wallet.teleeza.rateconfig.RateLimitConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
//import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Component("customRateLimiterAspect") // Rename the bean
public class RateLimiterAspect {
    @Autowired
    private Map<String, RateLimiter> clientRateLimiters;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);


    @Around("execution(* com.teleeza.wallet.teleeza.rewarded_ads.controller.*Controller.*(..)) || " +
            "execution(* com.teleeza.wallet.teleeza.customer_registration.controllers.*Controller.*(..))")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("RateLimiterAspect.applyRateLimiting");
        // Get the HTTP request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Extract the clientId header
        String clientId = request.getHeader("clientId");
        if (clientId == null || clientId.isEmpty()) {
            throw new RuntimeException("clientId header is required");
        }

        logger.debug("Applying rate limiter for clientId: {}", clientId);

        // Get or create the rate limiter for the client
        RateLimiter rateLimiter = rateLimitConfig.getOrCreateRateLimiter(clientId);

        try {
            // Acquire a permit from the rate limiter
            RateLimiter.waitForPermission(rateLimiter);
            logger.debug("Permit acquired for clientId: {}", clientId);

            // Proceed with the method execution
            return joinPoint.proceed();
        } catch (RequestNotPermitted e) {
            logger.warn("Rate limit exceeded for clientId: {}", clientId);

            // Handle rate limit exceeded
//            throw new RuntimeException("Rate limit exceeded for client: " + clientId + ". Please try again later.");
        }
        return ResponseEntity.status(429).body("You have exceeded the rate limit");
    }
}
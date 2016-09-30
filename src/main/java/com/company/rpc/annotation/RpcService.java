package com.company.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service // 表明可被 Spring 扫描
public @interface RpcService {

    Class<?> value();
}
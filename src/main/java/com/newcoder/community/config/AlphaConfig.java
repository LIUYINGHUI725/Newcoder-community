package com.newcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration //表示它是一个配置类，不是一个普通的类
public class AlphaConfig {

    @Bean //Bean的名字就是simpleDateFormat，Bean的名字就是以方法名命名的
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //这个方法返回的对象将被装配到容器里

    }
}

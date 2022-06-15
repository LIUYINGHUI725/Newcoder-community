package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype") 实例化多个Bean，很少用
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){ //构造器方法
        System.out.println("instantiate AlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("initialize AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("destroy AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}

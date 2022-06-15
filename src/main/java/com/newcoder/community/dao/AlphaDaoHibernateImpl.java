package com.newcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
        //Bean的名字默认是这个类的名字并且首字母小写，比如alphaDaoHibernateImpl，为了方便就定个别名，比如这里的"alphaHibernate"
    }
}

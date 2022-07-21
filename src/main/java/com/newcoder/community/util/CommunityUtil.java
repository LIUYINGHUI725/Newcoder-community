package com.newcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串。后面用于取5位作为salt
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密,它只能加密不能解密，而且每次加密结果一样
    //key是密码加salt
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();

    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    //写一个main来测试一下
    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("name","Lily");
        map.put("age",21);
        System.out.println(getJSONString(0,"ok",map));
    }

}

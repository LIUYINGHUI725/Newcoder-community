package com.newcoder.community.service;

import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞 第一次点赞，第二次取消点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {

        //entityUserId是被赞帖子的作者，也就是被赞人。userId是点赞人的id。
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey=RedisKeyUtil.getUserLikeKey(entityUserId);
                //查当前用户有没有对实体（帖子/评论/评论的回复）点过赞
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);

                //开启事务，上面查询isMember（是否点赞）要放在事务之外
                operations.multi();

                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    //查询某实体（帖子/评论/评论的回复）获赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某用户对某实体（帖子/评论/评论的回复）点没点过赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    //查询某个用户获得赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count= (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count==null? 0:count.intValue();
    }


}

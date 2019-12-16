package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-12 14:52
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String CATEGORY_KEY = "index:cates:";

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<CategoryEntity> queryLevel1Category() {

        Resp<List<CategoryEntity>> listResp = gmallPmsClient.queryCategoryEntites(1, 0l);
        List<CategoryEntity> categoryEntities = listResp.getData();
        return categoryEntities;
    }

    @GmallCache(prefix = "index:cates:")
    @Override
    public List<CategoryVO> querySubCatrgory(Long pid) {
//        // 1. 先去redis找
//        String key = CATEGORY_KEY + pid;
//        String jsonStr = stringRedisTemplate.opsForValue().get(key);
//        // 2. 找到就返回结果
//        if (!StringUtils.isEmpty(jsonStr)){
//            return JSON.parseArray(jsonStr,CategoryVO.class);
//        }
//        RLock lock = redissonClient.getLock("lock");
//        lock.lock();
//        //加锁后在判断一次
//        String jsonStr2 = stringRedisTemplate.opsForValue().get(key);
//        // 2. 找到就返回结果
//        if (!StringUtils.isEmpty(jsonStr2)){
//            lock.unlock();
//            return JSON.parseArray(jsonStr,CategoryVO.class);
//        }
//        // 3. 找不到查询数据库
        Resp<List<CategoryVO>> listResp = this.gmallPmsClient.querySubCategoryEntites(pid);
        List<CategoryVO> data = listResp.getData();
//        // 4. 将结果放入Redis缓存中
//        String catesStr = JSON.toJSONString(data);
//        stringRedisTemplate.opsForValue().set(key,catesStr,7l + new Random().nextInt(5), TimeUnit.DAYS);
//        lock.unlock();
        return data;
    }
}

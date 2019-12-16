package com.atguigu.gmall.message.service;

import com.atguigu.core.utils.MessageUtils;
import com.atguigu.gmall.message.template.SmsTempLate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-16 19:11
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class MessageService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private SmsTempLate smsTempLate = new SmsTempLate();

    public String sendMessage(String phoneNum){
        //1.手机号是否合法
        if (!MessageUtils.isPhoneNum(phoneNum)) {
            return "手机号码格式错误！";
        }

        //2.判断手机号码24小时内申请次数是否超过3次[使用redis保存，第一次访问时没有次数]
        //2.1拼接 手机号码 保存次数 存在redis中的键
        String phoneCountKey = "phone:code:"+phoneNum+":count";
        Boolean hasKey = stringRedisTemplate.hasKey(phoneCountKey);
        int count = 0;
        if (hasKey) {
            //已经获取过
            String str = stringRedisTemplate.opsForValue().get(phoneCountKey);
            count = Integer.parseInt(str);
            if (count >= 30) {
                return "该手机号获取验证码次数过多";
            }
        }
        //3.判断手机号码是否存在未使用的验证码
        String phoneCodeKey = "phone:code:"+phoneNum+":code";
        Boolean bool = stringRedisTemplate.hasKey(phoneCodeKey);

        if (bool) {
            return "该手机号获取验证码过于频繁";
        }

        //4.生成验证码
        String code = UUID.randomUUID().toString().replaceAll("-","").substring(0, 6);
        //5.发送
        boolean sendMesage = smsTempLate.sendMesage(phoneNum, code);
        if (!sendMesage) {
            return "网络繁忙，请稍后再试..";
        }

        //6.发送成功，存入Redis保存5分钟
        stringRedisTemplate.opsForValue().set(phoneCodeKey, code, 5, TimeUnit.MINUTES);
        //7.更新该手机号24小时内的验证次数
        if (count == 0) {
            stringRedisTemplate.opsForValue().set(phoneCountKey, "1",24,TimeUnit.HOURS);
        } else {
            stringRedisTemplate.boundValueOps(phoneCountKey).increment(1);
        }
        return "请查收验证码";
    }
}

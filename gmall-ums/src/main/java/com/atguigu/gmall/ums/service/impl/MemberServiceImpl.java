package com.atguigu.gmall.ums.service.impl;

import com.atguigu.Gmall.message.api.GmallMessageApi;
import com.atguigu.core.exception.UmsException;
import com.atguigu.gmall.ums.feign.GmallMessageClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GmallMessageClient gmallMessageClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean check(String data, Integer type) {

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1:
                wrapper.eq("username",data);
                break;
            case 2:
                wrapper.eq("mobile",data);
                break;
            case 3:
                wrapper.eq("email",data);
                break;
            default:
                return null;
        }
        return this.memberDao.selectCount(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
//        校验短信验证码   TODO
        String mobile = memberEntity.getMobile();
        String phoneCodeKey = "phone:code:"+mobile+":code";
        String checkCode = stringRedisTemplate.opsForValue().get(phoneCodeKey);
        if (StringUtils.equals(checkCode,code)){
//            生成盐
            String salt = UUID.randomUUID().toString().substring(0, 6);
            memberEntity.setSalt(salt);
            memberEntity.setCreateTime(new Date());
//           对密码加密
            String password = DigestUtils.md5Hex(memberEntity.getPassword() + salt);
            memberEntity.setPassword(password);
//           写入数据库
            this.save(memberEntity);
//           删除Redis中的验证码  TODO
            stringRedisTemplate.delete(phoneCodeKey);
        }


    }

    @Override
    public MemberEntity queryByUsernameAndPassword(String username, String password) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        MemberEntity memberEntity = this.getOne(wrapper.eq("username", username));
        if (memberEntity == null){
            throw new UmsException("用户名错误！！");
        }
        wrapper.eq("password",password);
        MemberEntity entity = this.getOne(wrapper);
        if (entity == null){
            throw new UmsException("密码错误！");
        }
        return entity;
    }

    @Override
    public void getCode(String phone) {
        this.gmallMessageClient.sendMessage(phone);
    }

}
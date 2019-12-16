package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueDao attrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryGroupByCatId(QueryCondition condition, Long cid) {

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (cid != null){
            wrapper.eq("catelog_id",cid);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    public GroupVO queryAllAttrByGid(Long gid) {
        GroupVO vo = new GroupVO();
        //查组
        AttrGroupEntity groupEntity = this.getById(gid);
        //查中间表
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_group_id",gid);
        List<AttrAttrgroupRelationEntity> relations = relationService.list(wrapper);
        vo.setRelations(relations);
        //获取id，查属性表
        List<Long> attrIds = relations.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)){
            return vo;
        }
        Collection<AttrEntity> attrEntities = attrService.listByIds(attrIds);

        vo.setAttrEntities((List<AttrEntity>) attrEntities);

        return vo;
    }

    @Override
    public List<GroupVO> queryGroupAndAttrByCid(Long catId) {
        //
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));

        //
        List<GroupVO> groupVOS = groupEntities.stream().map(attrGroupEntity -> {
            return this.queryAllAttrByGid(attrGroupEntity.getAttrGroupId());
        }).collect(Collectors.toList());
        return groupVOS;
    }

    @Override
    public List<ItemGroupVO> queryGroupByCatIdAndSpuId(Long catId, Long spuId) {
        // 1. 通过catId查询分组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        List<ItemGroupVO> groupVOS = groupEntities.stream().map(groupEntity -> {
            ItemGroupVO itemGroupVO = new ItemGroupVO();

            itemGroupVO.setName(groupEntity.getAttrGroupName());

            // 2. 通过分组Id查询中间表获取attrId
            Long groupId = groupEntity.getAttrGroupId();
            List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = this.relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));
            List<Long> attrIds = attrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            // 3. 通过souId和attrid查询属性信息
            List<ProductAttrValueEntity> productAttrValueEntities = this.attrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().in("attr_id", attrIds).eq("spu_id", spuId));
            itemGroupVO.setBaseAttrs(productAttrValueEntities);
            return itemGroupVO;
        }).collect(Collectors.toList());

        return groupVOS;
    }

}
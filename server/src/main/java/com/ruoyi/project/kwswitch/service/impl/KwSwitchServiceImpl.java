package com.ruoyi.project.kwswitch.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.IdUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.project.kwswitch.mapper.KwSwitchMapper;
import com.ruoyi.project.kwswitch.domain.KwSwitch;
import com.ruoyi.project.kwswitch.service.IKwSwitchService;

/**
 * 智能开关Service业务层处理
 *
 * @author kerwincui
 * @date 2020-04-23
 */
@Service
//@CacheConfig(cacheNames = "kwSwitch")
public class KwSwitchServiceImpl implements IKwSwitchService {
    @Autowired
    private KwSwitchMapper kwSwitchMapper;

    /**
     * 查询智能开关
     *
     * @param deviceId 智能开关ID
     * @return 智能开关
     */
    @Override
    //@Cacheable(key="#deviceId")
    public KwSwitch selectKwSwitchById(Long deviceId) {
        return kwSwitchMapper.selectKwSwitchById(deviceId);
    }

    /**
     * 查询智能开关列表
     *
     * @param kwSwitch 智能开关
     * @return 智能开关
     */
    @Override
    public List<KwSwitch> selectKwSwitchList(KwSwitch kwSwitch) {
        return kwSwitchMapper.selectKwSwitchList(kwSwitch);
    }

    /**
     * 新增智能开关
     *
     * @param kwSwitch 智能开关
     * @return 结果
     */
    @Override
    //@CachePut(key="#kwSwitch.deviceId")
    public int insertKwSwitch(KwSwitch kwSwitch) {
        kwSwitch.setCreateTime(DateUtils.getNowDate());
        kwSwitch.setApiKey(IdUtils.simpleUUID().toUpperCase());
        kwSwitch.setUserName(SecurityUtils.getUsername());
        return kwSwitchMapper.insertKwSwitch(kwSwitch);
    }

    /**
     * 修改智能开关
     *
     * @param kwSwitch 智能开关
     * @return 结果
     */
    @Override
    //@CachePut(key="#kwSwitch.deviceId")
    public int updateKwSwitch(KwSwitch kwSwitch) {
        kwSwitch.setUpdateTime(DateUtils.getNowDate());
        return kwSwitchMapper.updateKwSwitch(kwSwitch);
    }

    /**
     * 批量删除智能开关
     *
     * @param deviceIds 需要删除的智能开关ID
     * @return 结果
     */
    @Override
    public int deleteKwSwitchByIds(Long[] deviceIds) {
        return kwSwitchMapper.deleteKwSwitchByIds(deviceIds);
    }

    /**
     * 删除智能开关信息
     *
     * @param deviceId 智能开关ID
     * @return 结果
     */
    @Override
    //@CacheEvict(key="#deviceId",allEntries = true)
    public int deleteKwSwitchById(Long deviceId) {
        return kwSwitchMapper.deleteKwSwitchById(deviceId);
    }
}

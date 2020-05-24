package com.ruoyi.project.kwswitch.mapper;

import java.util.List;
import com.ruoyi.project.kwswitch.domain.KwSwitch;

/**
 * 智能开关Mapper接口
 * 
 * @author kerwincui
 * @date 2020-04-23
 */
public interface KwSwitchMapper
{
    /**
     * 查询智能开关
     * 
     * @param deviceId 智能开关ID
     * @return 智能开关
     */
    public KwSwitch selectKwSwitchById(Long deviceId);

    /**
     * 查询智能开关列表
     * 
     * @param kwSwitch 智能开关
     * @return 智能开关集合
     */
    public List<KwSwitch> selectKwSwitchList(KwSwitch kwSwitch);

    /**
     * 新增智能开关
     * 
     * @param kwSwitch 智能开关
     * @return 结果
     */
    public int insertKwSwitch(KwSwitch kwSwitch);

    /**
     * 修改智能开关
     * 
     * @param kwSwitch 智能开关
     * @return 结果
     */
    public int updateKwSwitch(KwSwitch kwSwitch);

    /**
     * 删除智能开关
     * 
     * @param deviceId 智能开关ID
     * @return 结果
     */
    public int deleteKwSwitchById(Long deviceId);

    /**
     * 批量删除智能开关
     * 
     * @param deviceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteKwSwitchByIds(Long[] deviceIds);
}

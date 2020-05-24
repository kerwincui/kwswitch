package com.ruoyi.project.kwswitch.controller;

import java.util.List;

import com.ruoyi.project.kwswitch.document.KwSwitchLog;
import com.ruoyi.project.kwswitch.mqtt.IMqttSender;
import com.ruoyi.project.kwswitch.service.impl.KwSwitchLogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.kwswitch.domain.KwSwitch;
import com.ruoyi.project.kwswitch.service.IKwSwitchService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;

/**
 * 智能开关Controller
 *
 * @author kerwincui
 * @date 2020-04-23
 */
@RestController
@RequestMapping("/kwswitch/switch")
@Api("智能开关")
public class KwSwitchController extends BaseController {
    @Autowired
    private IKwSwitchService kwSwitchService;
    @Autowired
    private KwSwitchLogServiceImpl kwSwitchLogService;
    @Autowired
    private IMqttSender iMqttSender;

    /**
     * 设置开关状态
     *
     * @param deviceId 设备ID 0-代表所有
     * @param switchA  1-开，0-关
     * @param switchB  1-开，0-关
     * @return
     */
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    @ApiOperation("设置开关的状态，1-开 0-关")
    public AjaxResult SetSwitch(@RequestParam long deviceId, @RequestParam String switchA, @RequestParam String switchB) {
        System.out.println("0。");
        if (deviceId != 0) {
            KwSwitch kwSwitch = kwSwitchService.selectKwSwitchById(deviceId);
            if (kwSwitch.getState().equals("1")) {
                //发送指令到mqtt
                String topic = "set/switch/" + deviceId;
                String payload=switchA + switchB + kwSwitch.getApiKey();
                //两位数标识，11-两开，00-两关，10-A开B关，01-A关B开
                iMqttSender.sendToMqtt(topic,payload);
            }
        }
        return toAjax(1);
    }

    /**
     * 获取开关监测数据
     *
     * @param deviceId 设备ID 0-代表所有
     * @return
     */
    @RequestMapping(value = "/get/status/{deviceId}", method = RequestMethod.GET)
    @ApiOperation("获取开关监测数据")
    public AjaxResult GetStatus(@PathVariable("deviceId") Long deviceId) {
        if (deviceId != 0) {
            KwSwitch kwSwitch = kwSwitchService.selectKwSwitchById(deviceId);
            if (kwSwitch.getState().equals("1")) {
                String topic = "get/monitor/" + deviceId;
                String payload = "00" + kwSwitch.getApiKey();    //前两位占位，从第三位开始存储
                //发送指令到mqtt
                iMqttSender.sendToMqtt(topic, payload);
            }
        }
        return toAjax(1);
    }

    /**
     * 查询智能开关列表
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:list')")
    @GetMapping("/list")
    public TableDataInfo list(KwSwitch kwSwitch) {
        startPage();
        List<KwSwitch> list = kwSwitchService.selectKwSwitchList(kwSwitch);
        return getDataTable(list);
    }

    /**
     * 查询智能开关检测日志列表
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:list')")
    @GetMapping("/log/list/{deviceId}")
    @ApiOperation("获取开关监测数据列表")
    public TableDataInfo logList(@PathVariable Long deviceId){
        List<KwSwitchLog> list=kwSwitchLogService.getKwSwitchLogByDeviceId(deviceId);
        return getDataTable(list);
    }

    /**
     * 导出智能开关列表
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:export')")
    @Log(title = "智能开关", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(KwSwitch kwSwitch) {
        List<KwSwitch> list = kwSwitchService.selectKwSwitchList(kwSwitch);
        ExcelUtil<KwSwitch> util = new ExcelUtil<KwSwitch>(KwSwitch.class);
        return util.exportExcel(list, "switch");
    }

    /**
     * 获取智能开关详细信息
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:query')")
    @GetMapping(value = "/{deviceId}")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId) {
        return AjaxResult.success(kwSwitchService.selectKwSwitchById(deviceId));
    }

    /**
     * 新增智能开关
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:add')")
    @Log(title = "智能开关", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KwSwitch kwSwitch) {
        return toAjax(kwSwitchService.insertKwSwitch(kwSwitch));
    }

    /**
     * 修改智能开关
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:edit')")
    @Log(title = "智能开关", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KwSwitch kwSwitch) {
        return toAjax(kwSwitchService.updateKwSwitch(kwSwitch));
    }

    /**
     * 删除智能开关
     */
    @PreAuthorize("@ss.hasPermi('kwswitch:switch:remove')")
    @Log(title = "智能开关", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    public AjaxResult remove(@PathVariable Long[] deviceIds) {
        return toAjax(kwSwitchService.deleteKwSwitchByIds(deviceIds));
    }
}

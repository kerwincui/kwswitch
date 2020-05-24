import request from '@/utils/request'

// 查询智能开关列表
export function listSwitch(query) {
  return request({
    url: '/kwswitch/switch/list',
    method: 'get',
    params: query
  })
}

// 设置开关状态
export function setSwitch(deviceId,switchA,switchB) {
  const params = {
    deviceId,
    switchA,
    switchB
  }
  return request({
    url: '/kwswitch/switch/set',
    method: 'post',
    params: params
  })
}

// 查询智能开关监测日志列表
export function getMonitor(deviceId) {
  return request({
    url: '/kwswitch/switch/log/list/' + deviceId,
    method: 'get'
  })
}

// 查询智能开关详细
export function getSwitch(deviceId) {
  return request({
    url: '/kwswitch/switch/' + deviceId,
    method: 'get'
  })
}

// 新增智能开关
export function addSwitch(data) {
  return request({
    url: '/kwswitch/switch',
    method: 'post',
    data: data
  })
}

// 修改智能开关
export function updateSwitch(data) {
  return request({
    url: '/kwswitch/switch',
    method: 'put',
    data: data
  })
}

// 删除智能开关
export function delSwitch(deviceId) {
  return request({
    url: '/kwswitch/switch/' + deviceId,
    method: 'delete'
  })
}

// 导出智能开关
export function exportSwitch(query) {
  return request({
    url: '/kwswitch/switch/export',
    method: 'get',
    params: query
  })
}
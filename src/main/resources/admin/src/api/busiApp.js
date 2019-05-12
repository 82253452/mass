import request from '@/utils/request'

const space = '/busiApp'

export function selectByPage(query) {
  return request({
    url: space + '/selectByPage',
    method: 'get',
    params: query
  })
}

export function insert(data) {
  return request({
    url: space + '/insert',
    method: 'post',
    data
  })
}

export function selectById(query) {
  return request({
    url: space + '/selectById',
    method: 'get',
    params: query
  })
}

export function updateById(data) {
  return request({
    url: space + '/updateById',
    method: 'post',
    data
  })
}

export function deleteById(data) {
  return request({
    url: space + '/deleteById',
    method: 'post',
    data
  })
}

export function Generator(query) {
  return request({
    url: space + '/generator',
    method: 'get',
    params: query
  })
}

export function Download(query) {
  return request({
    url: space + '/downloadFile',
    method: 'get',
    params: query
  })
}

export function getAppPages(query) {
  return request({
    url: space + '/getAppPages',
    method: 'get',
    params: query
  })
}

export function getAuthUrl(query) {
  return request({
    url: space + '/getAuthUrl',
    method: 'get',
    params: query
  })
}
export function pushWeappByAppId(query) {
  return request({
    url: space + '/pushWeapp',
    method: 'get',
    params: query
  })
}
export function onlyPushWeapp(query) {
  return request({
    url: space + '/onlyPushWeapp',
    method: 'get',
    params: query
  })
}
export function getItemList(query) {
  return request({
    url: space + '/getItemList',
    method: 'get',
    params: query
  })
}
export function releaseApp(query) {
  return request({
    url: space + '/releaseWeapp',
    method: 'get',
    params: query
  })
}

export function autoMessageApi(query) {
  return request({
    url: space + '/autoMessageApi',
    method: 'get',
    params: query
  })
}

export function closeMessageApi(query) {
  return request({
    url: space + '/closeMessageApi',
    method: 'get',
    params: query
  })
}

export function getColumns(query) {
  return request({
    url: space + '/getColumns',
    method: 'get',
    params: query
  })
}


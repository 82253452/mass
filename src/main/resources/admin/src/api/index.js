import request from '@/utils/request'

export function getPv(query) {
  return request({
    url: '/index/getPV',
    method: 'get',
    params: query
  })
}

export function getPersonUv(query) {
  return request({
    url: '/index/getUV',
    method: 'get',
    params: query
  })
}

export function getProductAdd(query) {
  return request({
    url: '/index/getProductAdd',
    method: 'get',
    params: query
  })
}
export function getTagTop(query) {
  return request({
    url: '/index/getTagTop',
    method: 'get',
    params: query
  })
}
export function getHistogram(query) {
  return request({
    url: '/index/getHistogram',
    method: 'get',
    params: query
  })
}


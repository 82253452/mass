import request from '@/utils/request'

const space='/sysRole'

export function selectByPage(query) {
  return request({
    url: space+'/selectByPage',
    method: 'get',
    params: query
  })
}

export function insert(data) {
  return request({
    url: space+'/insert',
    method: 'post',
    data
  })
}

export function selectById(query) {
  return request({
    url: space+'/selectById',
    method: 'get',
    params: query
  })
}

export function updateById(data) {
  return request({
    url: space+'/updateById',
    method: 'post',
    data
  })
}

export function deleteById(data) {
  return request({
    url: space+'/deleteById',
    method: 'post',
    data
  })
}

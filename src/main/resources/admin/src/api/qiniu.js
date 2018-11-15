import request from '@/utils/request'

export function getToken() {
  return request({
    url: '/common/qiniuToken',
    method: 'get'
  })
}

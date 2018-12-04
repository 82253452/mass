import request from '@/utils/request'

export function gettemplatedraftlist(query) {
  return request({
    url: '/busiApp/gettemplatedraftlist',
    method: 'get',
    params: query
  })
}
export function gettemplatelist(query) {
  return request({
    url: '/busiApp/gettemplatelist',
    method: 'get',
    params: query
  })
}
export function addtotemplate(query) {
  return request({
    url: '/busiApp/addtotemplate',
    method: 'get',
    params: query
  })
}
export function deleteTemplate(query) {
  return request({
    url: '/busiApp/deleteTemplate',
    method: 'get',
    params: query
  })
}
export function getCurrentTemplateId(query) {
  return request({
    url: '/busiApp/getCurrentTemplateId',
    method: 'get',
    params: query
  })
}
export function setCurrentTemplateId(query) {
  return request({
    url: '/busiApp/setCurrentTemplateId',
    method: 'get',
    params: query
  })
}

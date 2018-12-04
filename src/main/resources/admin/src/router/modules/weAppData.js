/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const busiRouter = {
  path: '/weapp',
  component: Layout,
  redirect: 'noredirect',
  name: 'weapp',
  meta: {
    title: '微信模板管理',
    icon: 'component',
    roles: ['admin']
  },
  children: [
    {
      path: 'draftTemplate',
      component: () => import('@/busi/weAppDraft'),
      name: 'draftTemplate',
      meta: { title: '草稿箱' }
    },
    {
      path: 'template',
      component: () => import('@/busi/weAppTemplate'),
      name: 'template',
      meta: { title: '模板管理' }
    }
  ]
}

export default busiRouter

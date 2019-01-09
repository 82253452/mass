/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const busiRouter = {
  path: '/data',
  component: Layout,
  redirect: 'noredirect',
  name: 'data',
  meta: {
    title: '数据管理',
    icon: 'component',
    roles: ['admin', 'user']
  },
  children: [
    {
      path: 'busiArticle',
      component: () => import('@/busi/busiArticle'),
      name: 'busiArticle',
      meta: { title: '文章管理' }
    },
    {
      path: 'busiQuestion',
      component: () => import('@/busi/busiQuestion'),
      name: 'busiQuestion',
      meta: { title: '答题管理' }
    },
    {
      path: 'wxmp',
      component: () => import('@/busi/wxmp'),
      name: 'wxmp',
      meta: { title: '采集数据' }
    }
  ]
}

export default busiRouter

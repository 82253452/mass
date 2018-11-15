/** When your routing table is too long, you can split it into small modules**/

import Layout from '@/views/layout/Layout'

const busiRouter = {
  path: '/data',
  component: Layout,
  redirect: 'noredirect',
  name: 'data',
  meta: {
    title: '数据管理',
    icon: 'component'
  },
  children: [
    {
      path: 'busiArticle',
      component: () => import('@/busi/busiArticle'),
      name: 'busiArticle',
      meta: { title: '文章管理' }
    },
  ]
}

export default busiRouter

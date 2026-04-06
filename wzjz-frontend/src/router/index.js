import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/login/LoginView.vue')
        },
        // --- 捐助者专属路由区 ---
        {
            path: '/donor',
            component: () => import('../views/donor/Layout.vue'),
            children: [
                // 捐助者首页
                { path: '', name: 'donorHome', component: () => import('../views/donor/IndexView.vue') },
                // 物资发布页
                { path: 'publish', name: 'donorPublish', component: () => import('../views/donor/PublishView.vue') },
                //捐助追踪页面
                { path: 'records', name: 'donorRecords', component: () => import('../views/donor/RecordsView.vue') },
                //需求大厅页面
                { path: 'hall', name: 'donorHall', component: () => import('../views/donor/HallView.vue') },
                // 沟通留言页面
                { path: 'messages', name: 'donorMessages', component: () => import('../views/donor/MessagesView.vue') },
                // 爱心证书页面
                { path: 'points', name: 'donorPoints', component: () => import('../views/donor/PointsView.vue') },
                // 个人中心页面
                { path: 'profile', name: 'donorProfile', component: () => import('../views/donor/ProfileView.vue') }
            ]
        },
        // --- 受助方专属路由区 ---
        {
            path: '/recipient',
            component: () => import('../views/recipient/Layout.vue'),
            children: [
                { path: '', name: 'recipientHome', component: () => import('../views/recipient/IndexView.vue') },
                //需求管理页面
                { path: 'demands', name: 'recipientDemands', component: () => import('../views/recipient/DemandsView.vue') },
                // 接收确认页面
                { path: 'receive', name: 'recipientReceive', component: () => import('../views/recipient/ReceiveView.vue') },
                // 沟通留言页面
                { path: 'messages', name: 'recipientMessages', component: () => import('../views/recipient/MessagesView.vue') },
                { path: 'feedback', name: 'recipientFeedback', component: () => import('../views/recipient/FeedbackView.vue') },
                // 机构主页
                { path: 'homepage', name: 'recipientHomepage', component: () => import('../views/recipient/HomepageView.vue') }
            ]
        },
        // --- 管理员专属路由区 ---
        {
            path: '/admin',
            component: () => import('../views/admin/Layout.vue'),
            children: [
                { path: '', name: 'adminHome', component: () => import('../views/admin/IndexView.vue') },
                { path: 'audit-user', name: 'adminAuditUser', component: () => import('../views/admin/UserManageView.vue') },
                // 需求项目审核
                { path: 'audit-demand', name: 'adminAuditDemand', component: () => import('../views/admin/DemandManageView.vue') },
                // 物资管理审核
                { path: 'audit-material', name: 'adminAuditMaterial', component: () => import('../views/admin/MaterialManageView.vue') }
            ]
        }
    ]
})

export default router
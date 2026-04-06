<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside admin-aside">
      <div class="logo admin-logo">系统后台管理</div>
      <el-menu active-text-color="#ffd04b" background-color="#2b2f3a" text-color="#bfcbd9" router :default-active="$route.path">
        <el-menu-item index="/admin"><el-icon><DataLine /></el-icon>数据看板</el-menu-item>
        <el-menu-item index="/admin/audit-user"><el-icon><UserFilled /></el-icon>资质审核</el-menu-item>
        <el-menu-item index="/admin/audit-material"><el-icon><Goods /></el-icon>物资审核</el-menu-item>
        <el-menu-item index="/admin/audit-demand"><el-icon><DocumentChecked /></el-icon>需求审核</el-menu-item>
        <el-menu-item index="/admin/orders"><el-icon><Van /></el-icon>物流监控</el-menu-item>
        <el-menu-item index="/admin/dictionary"><el-icon><Collection /></el-icon>字典管理</el-menu-item>
        <el-menu-item index="/admin/notice"><el-icon><Bell /></el-icon>公告与表彰</el-menu-item>
        <el-menu-item index="/admin/complaints"><el-icon><Warning /></el-icon>意见反馈</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">管理员总控中心</div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">{{ userPhone }} <el-icon><ArrowDown /></el-icon></span>
            <template #dropdown>
              <el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <div class="page-box"><router-view /></div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DataLine, UserFilled, Goods, DocumentChecked, Van, Collection, Bell, Warning, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userPhone = ref('')

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.phone || userInfo.role !== 'admin') return router.push('/login')
  userPhone.value = userInfo.phone
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}
</script>
<style scoped src="../donor/layout.css"></style>
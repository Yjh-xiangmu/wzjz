<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">公益受助端</div>
      <el-menu active-text-color="#67C23A" background-color="#ffffff" text-color="#303133" router :default-active="$route.path">
        <el-menu-item index="/recipient"><el-icon><House /></el-icon>首页</el-menu-item>
        <el-menu-item index="/recipient/demands"><el-icon><DocumentAdd /></el-icon>需求管理</el-menu-item>
        <el-menu-item index="/recipient/receive"><el-icon><Checked /></el-icon>接收确认</el-menu-item>
        <el-menu-item index="/recipient/messages"><el-icon><Message /></el-icon>消息回复</el-menu-item>
        <el-menu-item index="/recipient/feedback"><el-icon><Star /></el-icon>感恩回馈</el-menu-item>
        <el-menu-item index="/recipient/homepage"><el-icon><OfficeBuilding /></el-icon>机构主页</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">受助方工作台</div>
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
import { House, DocumentAdd, Checked, Message, Star, OfficeBuilding, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userPhone = ref('')

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.phone || userInfo.role !== 'recipient') return router.push('/login')
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
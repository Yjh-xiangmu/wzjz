<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">爱心捐助端</div>
      <el-menu active-text-color="#409EFF" background-color="#ffffff" text-color="#303133" router :default-active="$route.path">
        <el-menu-item index="/donor"><el-icon><House /></el-icon>首页</el-menu-item>
        <el-menu-item index="/donor/publish"><el-icon><Upload /></el-icon>物资发布</el-menu-item>
        <el-menu-item index="/donor/hall"><el-icon><ShoppingTrolley /></el-icon>需求大厅</el-menu-item>
        <el-menu-item index="/donor/records"><el-icon><List /></el-icon>捐助追踪</el-menu-item>
        <el-menu-item index="/donor/messages"><el-icon><ChatDotRound /></el-icon>沟通留言</el-menu-item>
        <el-menu-item index="/donor/points"><el-icon><Medal /></el-icon>爱心证书</el-menu-item>
        <el-menu-item index="/donor/profile"><el-icon><User /></el-icon>个人中心</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">欢迎回来，爱心人士</div>
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
import { House, Upload, ShoppingTrolley, List, ChatDotRound, Medal, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userPhone = ref('')

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.phone || userInfo.role !== 'donor') return router.push('/login')
  userPhone.value = userInfo.phone
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}
</script>
<style scoped src="./layout.css"></style>
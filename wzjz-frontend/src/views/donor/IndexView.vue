<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2>欢迎回来，爱心人士 {{ userInfo.phone }}</h2>
      <p>感谢您让这个世界变得更加温暖。这是目前平台的实时爱心数据：</p>
    </div>

    <el-row :gutter="20" class="stat-row" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6f1fc; color: #409eff;"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">入驻用户</div>
            <div class="stat-value">{{ statData.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c;"><el-icon><Box /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">已发布物资</div>
            <div class="stat-value">{{ statData.materialCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fef0f0; color: #f56c6c;"><el-icon><Notification /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">受助方需求</div>
            <div class="stat-value">{{ statData.demandCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f0f9eb; color: #67c23a;"><el-icon><Connection /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">成功匹配次数</div>
            <div class="stat-value">{{ statData.orderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-weight: bold;"><el-icon><Clock /></el-icon> 平台最新匹配动态</span>
            </div>
          </template>
          <el-timeline v-if="dynamics.length > 0">
            <el-timeline-item
                v-for="(item, index) in dynamics"
                :key="index"
                :timestamp="formatTime(item.create_time)"
                type="primary"
                hollow
            >
              爱心人士 ({{ hidePhone(item.donor_phone) }}) 成功将
              <strong style="color: #f56c6c;">{{ item.material_name }}</strong>
              捐赠给项目 <strong>【{{ item.demand_title }}】</strong>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无动态数据"></el-empty>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="box-card" shadow="hover">
          <template #header><div class="card-header"><span style="font-weight: bold;">快捷入口</span></div></template>
          <div class="quick-links">
            <el-button type="primary" plain class="nav-btn" @click="$router.push('/donor/hall')">去需求大厅捐助</el-button>
            <el-button type="success" plain class="nav-btn" @click="$router.push('/donor/publish')">发布闲置物资</el-button>
            <el-button type="warning" plain class="nav-btn" @click="$router.push('/donor/points')">查看爱心证书</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { User, Box, Notification, Connection, Clock } from '@element-plus/icons-vue'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const loading = ref(false)
const statData = ref({})
const dynamics = ref([])

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''
const hidePhone = (phone) => phone ? phone.substring(0, 3) + '****' + phone.substring(7) : '未知'

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const statRes = await request.get('/api/stat/overview')
    if (statRes) statData.value = statRes

    const dyRes = await request.get('/api/stat/dynamics')
    if (dyRes) dynamics.value = dyRes
  } catch (error) {
    console.error('获取大盘数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchDashboardData())
</script>

<style scoped>
.welcome-section { margin-bottom: 20px; }
.welcome-section h2 { color: #303133; margin-bottom: 5px; }
.welcome-section p { color: #909399; font-size: 14px; }
.stat-card {
  display: flex; align-items: center; padding: 10px;
}
:deep(.el-card__body) { display: flex; width: 100%; align-items: center;}
.stat-icon {
  width: 60px; height: 60px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 28px; margin-right: 15px;
}
.stat-title { font-size: 13px; color: #909399; margin-bottom: 5px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.quick-links { display: flex; flex-direction: column; gap: 15px; }
.nav-btn { width: 100%; margin-left: 0 !important; }
</style>
<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2>您好，{{ userInfo.phone }}，平台始终与您同在</h2>
      <p>目前全平台的爱心汇聚情况如下：</p>
    </div>

    <el-row :gutter="20" class="stat-row" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6f1fc; color: #409eff;"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">爱心用户总数</div>
            <div class="stat-value">{{ statData.userCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c;"><el-icon><Box /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">全平台爱心物资</div>
            <div class="stat-value">{{ statData.materialCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fef0f0; color: #f56c6c;"><el-icon><Notification /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">累计发起求助</div>
            <div class="stat-value">{{ statData.demandCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f0f9eb; color: #67c23a;"><el-icon><Connection /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">已落地捐助次数</div>
            <div class="stat-value">{{ statData.orderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header"><span style="font-weight: bold;"><el-icon><Clock /></el-icon> 实时爱心动态</span></div>
          </template>
          <el-timeline v-if="dynamics.length > 0">
            <el-timeline-item v-for="(item, index) in dynamics" :key="index" :timestamp="formatTime(item.create_time)" type="success" hollow>
              爱心人士 ({{ hidePhone(item.donor_phone) }}) 成功将
              <strong style="color: #67c23a;">{{ item.material_name }}</strong>
              捐赠给项目 <strong>【{{ item.demand_title }}】</strong>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无动态数据"></el-empty>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="box-card" shadow="hover">
          <template #header><div class="card-header"><span style="font-weight: bold;">快捷功能</span></div></template>
          <div class="quick-links">
            <el-button type="danger" plain class="nav-btn" @click="$router.push('/recipient/demands')">发起新的求助</el-button>
            <el-button type="primary" plain class="nav-btn" @click="$router.push('/recipient/receive')">待接收物资工作台</el-button>
            <el-button type="warning" plain class="nav-btn" @click="$router.push('/recipient/feedback')">去写感恩回馈</el-button>
          </div>
        </el-card>

        <el-card class="box-card" shadow="hover" style="margin-top: 20px;">
          <template #header><div class="card-header"><span style="font-weight: bold;">平台公告</span></div></template>
          <div v-if="notices.length > 0">
            <div
              v-for="item in notices" :key="item.id"
              @click="viewNotice(item)"
              class="notice-item"
            >
              <el-tag size="small" :type="item.type === '表彰' ? 'success' : (item.type === '资讯' ? 'warning' : 'info')">{{ item.type }}</el-tag>
              <span style="margin-left: 8px; font-size: 13px; color: #303133;">{{ item.title }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无公告" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="noticeDialogVisible" :title="currentNotice.title" width="600px">
      <div style="margin-bottom: 12px;">
        <el-tag :type="currentNotice.type === '表彰' ? 'success' : (currentNotice.type === '资讯' ? 'warning' : 'info')">{{ currentNotice.type }}</el-tag>
        <span style="margin-left: 10px; font-size: 13px; color: #909399;">{{ formatTime(currentNotice.createTime) }}</span>
      </div>
      <div style="line-height: 1.8; white-space: pre-wrap; color: #303133; min-height: 80px;">{{ currentNotice.content }}</div>
    </el-dialog>
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
const notices = ref([])

const noticeDialogVisible = ref(false)
const currentNotice = ref({})

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''
const hidePhone = (phone) => phone ? phone.substring(0, 3) + '****' + phone.substring(7) : '未知'

const viewNotice = (item) => {
  currentNotice.value = item
  noticeDialogVisible.value = true
}

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const statRes = await request.get('/api/stat/overview')
    if (statRes) statData.value = statRes

    const dyRes = await request.get('/api/stat/dynamics')
    if (dyRes) dynamics.value = dyRes

    const noticeRes = await request.get('/api/notice/list')
    if (noticeRes) notices.value = noticeRes.slice(0, 5)
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
.stat-card { display: flex; align-items: center; padding: 10px; }
:deep(.el-card__body) { display: flex; width: 100%; align-items: center;}
.stat-icon { width: 60px; height: 60px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 28px; margin-right: 15px; }
.stat-title { font-size: 13px; color: #909399; margin-bottom: 5px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.quick-links { display: flex; flex-direction: column; gap: 15px; }
.nav-btn { width: 100%; margin-left: 0 !important; }
.notice-item {
  margin-bottom: 12px;
  padding: 6px 4px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}
.notice-item:hover { background: #f5f7fa; }
.notice-item:last-child { border-bottom: none; margin-bottom: 0; }
</style>

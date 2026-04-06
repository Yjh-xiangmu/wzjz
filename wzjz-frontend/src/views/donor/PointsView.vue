<template>
  <div class="points-container">

    <el-card class="score-card" shadow="hover">
      <div class="score-header">
        <div class="score-info">
          <h3>我的爱心积分</h3>
          <div class="score-number">{{ currentPoints }}</div>
        </div>
        <div class="level-info">
          <el-tag :type="levelTagType" effect="dark" size="large" class="level-tag">
            <el-icon><Medal /></el-icon> {{ currentLevel }}
          </el-tag>
          <p class="level-hint">距离下一等级还需 {{ nextLevelPoints }} 积分</p>
        </div>
      </div>
    </el-card>

    <div class="certificate-wrapper" v-loading="loading">
      <div class="certificate-box">
        <div class="cert-border-inner">
          <div class="cert-header">荣誉证书</div>
          <div class="cert-content">
            <p class="cert-name">尊敬的爱心人士 <strong>{{ userPhone }}</strong>：</p>
            <p class="cert-text">
              感谢您在物资捐助平台上的无私奉献。经系统核算，您已累计获得
              <span class="highlight">{{ currentPoints }}</span> 点爱心积分，
              荣获 <strong>【{{ currentLevel }}】</strong> 称号。
            </p>
            <p class="cert-text">
              您的每一次善举，都在为这个世界增添一份温暖。特发此证，以资鼓励。
            </p>
          </div>
          <div class="cert-footer">
            <div class="seal">爱心认证</div>
            <div class="date">颁发日期：{{ currentDate }}</div>
          </div>
        </div>
      </div>
    </div>

    <el-card class="rule-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon> 积分获取与等级规则
        </div>
      </template>
      <el-timeline>
        <el-timeline-item type="success" hollow>
          <strong>如何获取积分：</strong>每成功完成一次物资捐助（受助方点击确认签收），系统自动发放 100 积分。
        </el-timeline-item>
        <el-timeline-item type="primary" hollow>
          <strong>爱心使者 (0 - 200分)：</strong> 注册并开始您的公益之旅。
        </el-timeline-item>
        <el-timeline-item type="warning" hollow>
          <strong>公益达人 (300 - 900分)：</strong> 您的善意已经帮助了许多人。
        </el-timeline-item>
        <el-timeline-item type="danger" hollow>
          <strong>慈善先锋 (1000分以上)：</strong> 您是平台的核心力量，感谢您的一路相伴！
        </el-timeline-item>
      </el-timeline>
    </el-card>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../utils/request'
import { Medal, InfoFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const userPhone = ref('')
const currentPoints = ref(0)
const currentDate = ref(new Date().toISOString().split('T')[0])

// 获取用户信息（包含积分）
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    userPhone.value = localUser.phone

    // 调用刚才写的后端接口查最新数据
    const res = await request.get(`/api/user/info?id=${localUser.id}`)
    if (res) {
      currentPoints.value = res.points || 0

      // 同步更新一下本地缓存的积分
      localUser.points = res.points
      localStorage.setItem('userInfo', JSON.stringify(localUser))
    }
  } catch (error) {
    console.error('获取积分失败', error)
  } finally {
    loading.value = false
  }
}

// 根据积分计算等级
const currentLevel = computed(() => {
  if (currentPoints.value >= 1000) return '慈善先锋'
  if (currentPoints.value >= 300) return '公益达人'
  return '爱心使者'
})

const levelTagType = computed(() => {
  if (currentPoints.value >= 1000) return 'danger' // 红色
  if (currentPoints.value >= 300) return 'warning' // 黄色
  return 'primary' // 蓝色
})

const nextLevelPoints = computed(() => {
  if (currentPoints.value >= 1000) return 0
  if (currentPoints.value >= 300) return 1000 - currentPoints.value
  return 300 - currentPoints.value
})

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.points-container {
  max-width: 900px;
  margin: 0 auto;
}

/* 顶部卡片 */
.score-card {
  margin-bottom: 30px;
  background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
}
.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
}
.score-info h3 {
  margin: 0 0 10px 0;
  color: #606266;
  font-weight: normal;
}
.score-number {
  font-size: 48px;
  font-weight: bold;
  color: #f56c6c;
  font-family: 'Arial', sans-serif;
}
.level-info {
  text-align: right;
}
.level-tag {
  font-size: 16px;
  padding: 8px 16px;
}
.level-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
}

/* 证书样式 - 纯CSS绘制 */
.certificate-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.certificate-box {
  width: 700px;
  background-color: #fffaf0;
  border: 10px solid #d4af37; /* 金色外边框 */
  padding: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  position: relative;
}
.cert-border-inner {
  border: 2px solid #d4af37;
  padding: 40px;
  height: 100%;
  position: relative;
}
.cert-header {
  text-align: center;
  font-size: 36px;
  font-weight: bold;
  color: #c9302c; /* 证书红 */
  letter-spacing: 10px;
  margin-bottom: 40px;
  font-family: 'STKaiti', 'KaiTi', serif; /* 楷体 */
}
.cert-content {
  font-size: 18px;
  line-height: 2;
  color: #333;
  font-family: 'STFangsong', 'FangSong', serif; /* 仿宋 */
}
.cert-name {
  font-size: 20px;
  margin-bottom: 20px;
}
.highlight {
  color: #c9302c;
  font-weight: bold;
  font-size: 22px;
  margin: 0 5px;
}
.cert-footer {
  margin-top: 60px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: relative;
}
.date {
  font-size: 16px;
  font-family: 'STKaiti', 'KaiTi', serif;
}
.seal {
  position: absolute;
  right: 50px;
  top: -40px;
  width: 100px;
  height: 100px;
  border: 4px solid #c9302c;
  border-radius: 50%;
  color: #c9302c;
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'STKaiti', 'KaiTi', serif;
  transform: rotate(-15deg);
  opacity: 0.8;
  letter-spacing: 2px;
}

.card-header {
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
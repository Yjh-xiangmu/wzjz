<template>
  <div class="hall-container">
    <div class="header-section">
      <h2>爱心需求大厅</h2>
      <p>在这里，每一个微小的善意都能找到需要它的地方。</p>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="item in demandList" :key="item.id" style="margin-bottom: 20px;">
        <el-card class="demand-card" shadow="hover">
          <div class="card-top">
            <el-tag :type="item.urgency === '特急' ? 'danger' : (item.urgency === '紧急' ? 'warning' : 'info')" effect="dark">
              {{ item.urgency }}
            </el-tag>
            <span class="date">{{ formatTime(item.createTime) }}</span>
          </div>
          <h3 class="title">{{ item.title }}</h3>

          <div class="quantity-info">
            <div class="info-row">
              <span>目标: <b>{{ item.quantity }}</b> 份</span>
              <span>已筹集: <b style="color: #67c23a;">{{ item.matchedQuantity || 0 }}</b> 份</span>
            </div>
            <el-progress
                :percentage="Math.min(Math.round(((item.matchedQuantity || 0) / item.quantity) * 100), 100)"
                :stroke-width="8"
                :color="item.urgency === '特急' ? '#f56c6c' : '#409EFF'"
            />
          </div>

          <div class="address-text" v-if="item.address">
            <el-icon><Location /></el-icon> {{ item.address }}
          </div>
          <div class="address-text" v-if="item.contactPhone">
            <el-icon><Phone /></el-icon> {{ item.contactPhone }}
          </div>

          <div class="background-text">
            {{ item.background }}
          </div>

          <div class="card-bottom">
            <el-button link type="primary" @click="openOrgDialog(item.recipientId)">
              <el-icon><View /></el-icon> 了解受助机构
            </el-button>
            <el-button type="primary" round class="donate-btn" @click="openDonateDialog(item)">我要捐助</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="选择要捐助的物资" width="600px">
      <div v-if="currentDemand" class="target-info">
        您正在向 <strong>{{ currentDemand.title }}</strong> 项目发起捐助
      </div>
      <el-table :data="availableMaterials" style="width: 100%" max-height="300px" v-loading="materialLoading">
        <el-table-column width="55">
          <template #default="scope">
            <el-radio v-model="selectedMaterialId" :label="scope.row.id"><span></span></el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="物资名称" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmDonate" :disabled="!selectedMaterialId">确认捐助</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="orgDialogVisible" title="受助方机构与资质信息" width="600px" destroy-on-close>
      <div v-loading="orgLoading" class="org-content">
        <template v-if="orgData">
          <div class="org-header">
            <el-avatar :size="50" style="background: #E6A23C;">受</el-avatar>
            <div class="org-header-text">
              <h3>{{ orgData.orgName || '爱心受助个人/暂未命名机构' }}</h3>
              <span class="org-phone">联系账号: {{ orgData.phone }}</span>
            </div>
          </div>
          <el-divider />
          <div class="org-desc">
            <h4><el-icon><Reading /></el-icon> 机构简介</h4>
            <p>{{ orgData.orgDescription || '该受助方暂未填写详细简介。' }}</p>
          </div>
          <div class="org-qual" v-if="orgData.qualifications">
            <h4><el-icon><Picture /></el-icon> 资质证明与现场照片</h4>
            <div class="image-gallery">
              <el-image
                  v-for="(img, index) in splitImages(orgData.qualifications)"
                  :key="index"
                  class="detail-img"
                  :src="'http://localhost:8080' + img"
                  :preview-src-list="splitImages(orgData.qualifications).map(i => 'http://localhost:8080' + i)"
                  preview-teleported
                  fit="cover"
              />
            </div>
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Phone, View, Reading, Picture } from '@element-plus/icons-vue'

const loading = ref(false)
const demandList = ref([])

const dialogVisible = ref(false)
const currentDemand = ref(null)
const materialLoading = ref(false)
const availableMaterials = ref([])
const selectedMaterialId = ref(null)

// 机构信息弹窗相关
const orgDialogVisible = ref(false)
const orgLoading = ref(false)
const orgData = ref(null)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.split('T')[0]
}

const splitImages = (imgStr) => imgStr ? imgStr.split(',').filter(i => i.trim()) : []

const fetchDemandList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/demand/hallList')
    demandList.value = res || []
  } catch (error) {
    console.error("获取大厅数据失败", error)
  } finally {
    loading.value = false
  }
}

// 打开机构信息弹窗并获取数据
const openOrgDialog = async (recipientId) => {
  orgDialogVisible.value = true
  orgLoading.value = true
  try {
    const res = await request.get(`/api/user/info?id=${recipientId}`)
    orgData.value = res
  } catch (error) {
    console.error('获取机构信息失败', error)
  } finally {
    orgLoading.value = false
  }
}

const openDonateDialog = async (demand) => {
  currentDemand.value = demand
  selectedMaterialId.value = null
  dialogVisible.value = true
  materialLoading.value = true

  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const res = await request.get(`/api/material/available?donorId=${userInfo.id}`)
    availableMaterials.value = res || []
    if (availableMaterials.value.length === 0) {
      ElMessage.warning('您当前没有待捐助的可用物资，请先去发布物资。')
    }
  } catch (error) {
    console.error("获取可用物资失败", error)
  } finally {
    materialLoading.value = false
  }
}

const confirmDonate = () => {
  ElMessageBox.confirm(
      '确认将选中的物资定向捐助给该项目吗？提交后将生成物流订单。',
      '系统提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const orderData = {
      demandId: currentDemand.value.id,
      recipientId: currentDemand.value.recipientId,
      materialId: selectedMaterialId.value,
      donorId: userInfo.id
    }
    try {
      await request.post('/api/order/create', orderData)
      ElMessage.success('捐助匹配成功！请前往“捐助追踪”查看发货状态。')
      dialogVisible.value = false
      fetchDemandList()
    } catch (error) {
      console.error("生成订单失败", error)
    }
  }).catch(() => {})
}

onMounted(() => fetchDemandList())
</script>

<style scoped>
.header-section { text-align: center; margin-bottom: 30px; }
.header-section h2 { font-size: 28px; color: #303133; margin-bottom: 10px; }
.header-section p { color: #909399; }
.demand-card { border-radius: 12px; transition: all 0.3s; height: 100%; display: flex; flex-direction: column; }
.demand-card:hover { transform: translateY(-5px); box-shadow: 0 12px 24px rgba(0,0,0,0.1) !important; }
.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.date { font-size: 13px; color: #909399; }
.title { font-size: 18px; color: #303133; margin: 0 0 10px 0; font-weight: bold; }
.quantity-info { margin-bottom: 15px; }
.info-row { display: flex; justify-content: space-between; font-size: 13px; color: #606266; margin-bottom: 5px; }
.address-text { font-size: 13px; color: #409EFF; margin-bottom: 8px; display: flex; align-items: center; gap: 4px; }
.background-text { font-size: 14px; color: #606266; line-height: 1.6; flex: 1; display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 3; overflow: hidden; margin-bottom: 20px; }

/* 底部操作区改成左右布局 */
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.donate-btn { padding: 8px 24px; }

.target-info { background-color: #f0f9eb; color: #67c23a; padding: 10px 15px; border-radius: 4px; margin-bottom: 20px; }

/* 机构信息弹窗样式 */
.org-content { padding: 10px; }
.org-header { display: flex; align-items: center; }
.org-header-text { margin-left: 15px; }
.org-header-text h3 { margin: 0 0 5px 0; color: #303133; }
.org-phone { font-size: 13px; color: #909399; }
.org-desc h4, .org-qual h4 { color: #303133; margin-bottom: 10px; display: flex; align-items: center; gap: 5px;}
.org-desc p { color: #606266; line-height: 1.6; white-space: pre-wrap; background-color: #f5f7fa; padding: 15px; border-radius: 8px;}
.image-gallery { display: flex; flex-wrap: wrap; gap: 10px; }
.detail-img { width: 120px; height: 120px; border-radius: 8px; border: 1px solid #eee; cursor: zoom-in; }
</style>
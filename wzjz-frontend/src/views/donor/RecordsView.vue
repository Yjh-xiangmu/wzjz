<template>
  <div class="records-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的捐助追踪</span>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="物资名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="当前状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === 1" type="success" size="small" @click="openShipDialog(scope.row.id)">发货</el-button>
            <el-button link type="primary" size="small" @click="viewDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="物资捐助详情" width="700px" destroy-on-close>
      <div v-if="detailData" class="detail-content">
        <el-steps :active="getStepActive(detailData.status)" finish-status="success" align-center class="status-steps">
          <el-step title="待捐助" />
          <el-step title="已匹配待发货" />
          <el-step title="运输中" />
          <el-step title="已签收" />
        </el-steps>

        <el-divider content-position="left">基础信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="物资名称">{{ detailData.name }}</el-descriptions-item>
          <el-descriptions-item label="物资分类">{{ detailData.category }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ detailData.quantity }}</el-descriptions-item>
          <el-descriptions-item label="新旧程度">{{ detailData.conditionDegree }}</el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="有效期" v-if="detailData.validityDate">{{ detailData.validityDate }}</el-descriptions-item>
          <el-descriptions-item label="物资状态">
            <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="补充说明" :span="2">{{ detailData.description || '无' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">实拍图片</el-divider>
        <div class="image-gallery">
          <el-image
              v-for="(img, index) in splitImages(detailData.images)"
              :key="index"
              class="detail-img"
              :src="'http://localhost:8080' + img"
              :preview-src-list="splitImages(detailData.images).map(i => 'http://localhost:8080' + i)"
              preview-teleported
              fit="cover"
          />
        </div>

        <template v-if="feedbackData">
          <el-divider content-position="left" style="border-color: #f56c6c; color: #f56c6c;">
            <el-icon><StarFilled /></el-icon> 收到感恩回馈
          </el-divider>
          <div class="feedback-box">
            <p class="fb-content">" {{ feedbackData.content }} "</p>
            <div class="image-gallery" v-if="feedbackData.images">
              <el-image
                  v-for="(img, index) in splitImages(feedbackData.images)"
                  :key="'fb'+index"
                  class="detail-img"
                  :src="'http://localhost:8080' + img"
                  :preview-src-list="splitImages(feedbackData.images).map(i => 'http://localhost:8080' + i)"
                  preview-teleported
                  fit="cover"
              />
            </div>
            <div class="fb-time">受助方发送于：{{ formatTime(feedbackData.createTime) }}</div>
          </div>
        </template>

      </div>
    </el-dialog>

    <el-dialog v-model="shipDialogVisible" title="填写发货物流" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.logisticsCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitShip" :loading="shipLoading">确认发货</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { StarFilled } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const detailData = ref(null)
const feedbackData = ref(null)

const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipForm = reactive({ materialId: null, logisticsCompany: '', logisticsNo: '' })

const fetchMyRecords = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const data = await request.get(`/api/material/myList?donorId=${userInfo.id}`)
    tableData.value = data || []
  } catch (error) {
    console.error("获取列表失败:", error)
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ') : ''
const getStatusText = (status) => ({ 0: '待捐助', 1: '已匹配待发货', 2: '运输中', 3: '已签收', 4: '已驳回' }[status] || '未知')
const getStatusType = (status) => ({ 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }[status] || 'info')
const getStepActive = (status) => status === 4 ? 0 : status + 1
const splitImages = (imgStr) => imgStr ? imgStr.split(',').filter(i => i.trim()) : []

const viewDetail = async (id) => {
  try {
    const data = await request.get(`/api/material/${id}`)
    detailData.value = data

    feedbackData.value = null
    if (data.status === 3) {
      const fbRes = await request.get(`/api/feedback/getByMaterial?materialId=${id}`)
      if (fbRes) feedbackData.value = fbRes
    }

    detailVisible.value = true
  } catch (error) {
    console.error("获取详情失败:", error)
  }
}

const openShipDialog = (materialId) => {
  shipForm.materialId = materialId
  shipForm.logisticsCompany = ''
  shipForm.logisticsNo = ''
  shipDialogVisible.value = true
}

const submitShip = async () => {
  if (!shipForm.logisticsCompany || !shipForm.logisticsNo) {
    return ElMessage.warning('请填写物流公司和快递单号')
  }
  shipLoading.value = true
  try {
    await request.post('/api/order/ship', shipForm)
    ElMessage.success('发货成功！')
    shipDialogVisible.value = false
    fetchMyRecords()
  } catch (error) {
    console.error("发货失败", error)
  } finally {
    shipLoading.value = false
  }
}

onMounted(() => fetchMyRecords())
</script>

<style scoped>
.card-header { font-weight: bold; }
.status-steps { margin-bottom: 30px; padding: 10px 0; }
.detail-content { padding: 10px; }
.image-gallery { display: flex; flex-wrap: wrap; gap: 10px; }
.detail-img { width: 120px; height: 120px; border-radius: 8px; border: 1px solid #eee; cursor: zoom-in; }

.feedback-box {
  background-color: #fdf6ec;
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
}
.fb-content {
  font-size: 15px;
  color: #e6a23c;
  line-height: 1.6;
  font-style: italic;
  margin-bottom: 15px;
  white-space: pre-wrap;
}
.fb-time {
  text-align: right;
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
}
</style>
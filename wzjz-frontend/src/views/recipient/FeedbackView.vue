<template>
  <div class="feedback-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>感恩回馈工作台</span>
          <span class="sub-text">为爱心人士送上一份谢意</span>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="material_name" label="收到的物资" min-width="150" />
        <el-table-column prop="donor_phone" label="捐助者账号" width="150" />
        <el-table-column prop="order_time" label="签收时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.order_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="feedback_id" label="回馈状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.feedback_id" type="success">已感谢</el-tag>
            <el-tag v-else type="warning">待回馈</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
                v-if="!scope.row.feedback_id"
                type="primary"
                size="small"
                @click="openFeedbackDialog(scope.row.order_id)"
            >
              写感谢信
            </el-button>
            <el-button v-else link type="primary" size="small" @click="viewFeedback(scope.row)">
              查看回馈
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="致爱心人士的感谢信" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="感谢感言" prop="content">
          <el-input
              v-model="formData.content"
              type="textarea"
              :rows="5"
              placeholder="请写下受助群体的近况，或是对爱心人士的感谢..."
          />
        </el-form-item>

        <el-form-item label="现场照片">
          <el-upload
              class="upload-demo"
              action="http://localhost:8080/api/file/upload"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              list-type="picture-card"
              :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">发送回馈</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="viewVisible" title="感恩回馈记录" width="500px">
      <div v-if="currentView" class="view-content">
        <div class="feedback-text">{{ currentView.content }}</div>
        <div class="image-gallery" v-if="currentView.images">
          <el-image
              v-for="(img, index) in splitImages(currentView.images)"
              :key="index"
              class="feedback-img"
              :src="'http://localhost:8080' + img"
              :preview-src-list="splitImages(currentView.images).map(i => 'http://localhost:8080' + i)"
              fit="cover"
          />
        </div>
        <div class="feedback-time">发送于: {{ formatTime(currentView.feedback_time) }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const uploadedImages = ref([])

const viewVisible = ref(false)
const currentView = ref(null)

const formData = reactive({
  orderId: null,
  content: '',
  images: ''
})

const rules = reactive({
  content: [{ required: true, message: '请填写感谢感言', trigger: 'blur' }]
})

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''
const splitImages = (imgStr) => imgStr ? imgStr.split(',').filter(i => i.trim()) : []

const fetchList = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const res = await request.get(`/api/feedback/list?recipientId=${userInfo.id}`)
    tableData.value = res || []
  } finally {
    loading.value = false
  }
}

const openFeedbackDialog = (orderId) => {
  formData.orderId = orderId
  formData.content = ''
  formData.images = ''
  uploadedImages.value = []
  dialogVisible.value = true
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    uploadedImages.value.push(res.data)
    formData.images = uploadedImages.value.join(',')
  }
}

const handleRemove = (file) => {
  if (file.response && file.response.data) {
    uploadedImages.value = uploadedImages.value.filter(url => url !== file.response.data)
    formData.images = uploadedImages.value.join(',')
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        await request.post('/api/feedback/submit', { ...formData, recipientId: userInfo.id })
        ElMessage.success('发送成功')
        dialogVisible.value = false
        fetchList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const viewFeedback = (row) => {
  currentView.value = row
  viewVisible.value = true
}

onMounted(() => fetchList())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
.sub-text { font-size: 13px; color: #909399; font-weight: normal; }
.view-content { padding: 10px; }
.feedback-text { font-size: 15px; line-height: 1.6; color: #333; margin-bottom: 20px; white-space: pre-wrap; }
.image-gallery { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 20px; }
.feedback-img { width: 100px; height: 100px; border-radius: 6px; border: 1px solid #ebeef5; }
.feedback-time { text-align: right; font-size: 12px; color: #999; }
</style>
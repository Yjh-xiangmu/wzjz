<template>
  <div class="homepage-container">
    <el-card class="box-card" shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>机构资质与主页信息</span>
          <span class="sub-text">完善信息有助于提升捐助者的信任度</span>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="org-form"
      >
        <el-form-item label="机构/团队名称" prop="orgName">
          <el-input v-model="formData.orgName" placeholder="例如：某某山区支教团队、某某流浪动物救助站" />
        </el-form-item>

        <el-form-item label="机构简介" prop="orgDescription">
          <el-input
              v-model="formData.orgDescription"
              type="textarea"
              :rows="6"
              placeholder="请详细描述您的机构背景、主要服务对象、成立时间等..."
          />
        </el-form-item>

        <el-form-item label="资质证明文件" prop="qualifications">
          <el-upload
              class="upload-demo"
              action="http://localhost:8080/api/file/upload"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
              list-type="picture-card"
              :limit="5"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip text-gray">
                请上传相关的登记证书、授权书或现场工作照片（最多5张）
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitLoading" class="save-btn">保存主页信息</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="box-card" shadow="hover" style="margin-top: 20px;">
      <template #header><div class="card-header">意见反馈 / 投诉建议</div></template>
      <el-row :gutter="30">
        <el-col :span="10">
          <el-form :model="complaintForm" label-width="80px" style="margin-top: 10px;">
            <el-form-item label="反馈内容">
              <el-input type="textarea" v-model="complaintForm.content" :rows="4" placeholder="请描述您的问题或建议..." />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitComplaint" :loading="complaintLoading">提交反馈</el-button>
              <el-button @click="complaintForm.content = ''">清空</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="14">
          <div style="margin-top: 10px; font-weight: bold; margin-bottom: 10px; color: #606266;">我的反馈记录</div>
          <div v-if="myComplaints.length > 0" style="max-height: 300px; overflow-y: auto;">
            <div v-for="item in myComplaints" :key="item.id" class="complaint-item">
              <div class="complaint-header">
                <el-tag size="small" :type="item.status === 1 ? 'success' : 'warning'">
                  {{ item.status === 1 ? '已回复' : '待处理' }}
                </el-tag>
                <span class="complaint-time">{{ formatComplaintTime(item.createTime) }}</span>
              </div>
              <div class="complaint-content">{{ item.content }}</div>
              <div v-if="item.reply" class="complaint-reply">
                <span style="color: #67C23A; font-weight: bold;">管理员回复：</span>{{ item.reply }}
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无反馈记录" :image-size="60" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const complaintForm = reactive({ content: '' })
const complaintLoading = ref(false)
const myComplaints = ref([])

const formatComplaintTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''

const fetchMyComplaints = async () => {
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const res = await request.get(`/api/complaint/myList?userId=${localUser.id}`)
  if (res) myComplaints.value = res
}

const submitComplaint = async () => {
  if (!complaintForm.content.trim()) {
    ElMessage.warning('请填写反馈内容')
    return
  }
  complaintLoading.value = true
  try {
    const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    await request.post('/api/complaint/submit', { userId: localUser.id, content: complaintForm.content })
    ElMessage.success('反馈提交成功，感谢您的建议！')
    complaintForm.content = ''
    fetchMyComplaints()
  } catch (error) {
    console.error('提交反馈失败', error)
  } finally {
    complaintLoading.value = false
  }
}

const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  orgName: '',
  orgDescription: '',
  qualifications: ''
})

// 用于回显图片的数组
const fileList = ref([])
const uploadedImages = ref([])

const rules = reactive({
  orgName: [{ required: true, message: '请填写机构名称', trigger: 'blur' }],
  orgDescription: [{ required: true, message: '请填写机构简介', trigger: 'blur' }]
})

// 拉取当前数据
const fetchInfo = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    formData.id = userInfo.id

    const res = await request.get(`/api/user/info?id=${userInfo.id}`)
    if (res) {
      formData.orgName = res.orgName || ''
      formData.orgDescription = res.orgDescription || ''
      formData.qualifications = res.qualifications || ''

      // 如果之前传过照片，处理照片回显
      if (res.qualifications) {
        uploadedImages.value = res.qualifications.split(',').filter(i => i.trim())
        fileList.value = uploadedImages.value.map(url => ({
          name: '资质图片',
          url: 'http://localhost:8080' + url,
          response: { data: url } // 模拟后端返回格式，方便移除时使用
        }))
      }
    }
  } catch (error) {
    console.error('获取机构信息失败', error)
  } finally {
    loading.value = false
  }
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    uploadedImages.value.push(res.data)
    formData.qualifications = uploadedImages.value.join(',')
  }
}

const handleRemove = (file) => {
  // 兼容刚刚上传的和之前回显的图片
  const path = file.response ? file.response.data : file.url.replace('http://localhost:8080', '')
  uploadedImages.value = uploadedImages.value.filter(url => url !== path)
  formData.qualifications = uploadedImages.value.join(',')
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await request.post('/api/user/updateOrg', formData)
        ElMessage.success('机构信息保存成功')
      } catch (error) {
        console.error('保存失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchInfo()
  fetchMyComplaints()
})
</script>

<style scoped>
.homepage-container {
  max-width: 900px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
.sub-text {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}
.org-form {
  margin-top: 20px;
}
.text-gray {
  color: #909399;
  margin-top: 8px;
}
.save-btn {
  padding: 0 40px;
}
.complaint-item {
  padding: 10px 12px;
  margin-bottom: 10px;
  background: #f9f9f9;
  border-radius: 6px;
  border-left: 3px solid #dcdfe6;
}
.complaint-item:has(.complaint-reply) { border-left-color: #67C23A; }
.complaint-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.complaint-time { font-size: 12px; color: #909399; }
.complaint-content { font-size: 13px; color: #303133; margin-bottom: 6px; }
.complaint-reply { font-size: 13px; color: #606266; background: #f0f9eb; padding: 6px 10px; border-radius: 4px; }
</style>
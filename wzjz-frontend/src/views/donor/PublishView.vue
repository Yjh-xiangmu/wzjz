<template>
  <div class="publish-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>发布待捐助物资</span>
        </div>
      </template>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="物资名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入物资名称，如：冬季棉衣" clearable />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="系统分类" prop="category">
              <el-select v-model="formData.category" placeholder="请选择物资分类" style="width: 100%;">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="捐助数量" prop="quantity">
              <el-input-number v-model="formData.quantity" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="新旧程度" prop="conditionDegree">
              <el-select v-model="formData.conditionDegree" placeholder="请选择新旧程度" style="width: 100%;">
                <el-option label="全新" value="全新" />
                <el-option label="九成新" value="九成新" />
                <el-option label="八成新" value="八成新" />
                <el-option label="七成新及以下" value="七成新及以下" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期至" prop="validityDate">
              <el-date-picker
                  v-model="formData.validityDate"
                  type="date"
                  placeholder="针对食品/药品选填"
                  style="width: 100%;"
                  value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="实拍图片" prop="images">
          <el-upload
              class="upload-demo"
              action="http://localhost:8080/api/file/upload"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              list-type="picture-card"
              :limit="5"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip text-gray-400">最多上传5张照片，格式为jpg/png，每张不超过5MB。请至少上传一张实拍图片。</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="详细说明" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请补充物资的具体情况，如品牌、尺码、适用人群等..."
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading" class="submit-btn">确认发布</el-button>
          <el-button @click="resetForm">重置填写</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const formRef = ref(null)
const loading = ref(false)
const uploadedImageUrls = ref([])
const categories = ref([])

const formData = reactive({
  name: '',
  category: '',
  quantity: 1,
  conditionDegree: '',
  validityDate: '',
  description: '',
  images: ''
})

const rules = reactive({
  name: [{ required: true, message: '请输入物资名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择系统分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  conditionDegree: [{ required: true, message: '请选择新旧程度', trigger: 'change' }],
  images: [{ required: true, message: '请至少上传一张实拍图片', trigger: 'change' }]
})

const handleUploadSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    uploadedImageUrls.value.push(response.data)
    formData.images = uploadedImageUrls.value.join(',')
    formRef.value.clearValidate('images')
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.msg || '图片上传失败')
  }
}

const handleRemove = (uploadFile) => {
  const response = uploadFile.response
  if (response && response.data) {
    uploadedImageUrls.value = uploadedImageUrls.value.filter(url => url !== response.data)
    formData.images = uploadedImageUrls.value.join(',')
    formRef.value.validateField('images')
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (uploadedImageUrls.value.length === 0) {
        ElMessage.warning('物资必须至少包含一张实拍图片，请点击“实拍图片”区域进行上传。')
        formRef.value.validateField('images')
        return
      }

      loading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        const postData = {
          ...formData,
          donorId: userInfo.id
        }

        await request.post('/api/material/publish', postData)
        ElMessage.success('物资发布成功！')
        resetForm()
      } catch (error) {
        console.error('发布失败:', error)
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请将带红星的必填项填写完整')
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
  uploadedImageUrls.value = []
  formData.images = ''
}

onMounted(async () => {
  const res = await request.get('/api/category/list')
  if (res) categories.value = res
})
</script>

<style scoped>
.publish-container {
  max-width: 900px;
  margin: 0 auto;
}
.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.submit-btn {
  padding: 0 30px;
}
</style>
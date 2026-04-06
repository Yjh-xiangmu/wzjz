<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card user-card" shadow="hover" v-loading="loading">
          <div class="avatar-wrapper">
            <el-avatar :size="80" style="background: #409EFF; font-size: 30px;">爱</el-avatar>
          </div>
          <div class="user-info">
            <h3 class="phone">{{ userInfo.phone }}</h3>
            <div class="role-tag">
              <el-tag type="primary" effect="dark" round>爱心捐助人士</el-tag>
            </div>
          </div>
          <el-divider />
          <div class="info-list">
            <div class="info-item">
              <span class="label">当前积分：</span>
              <span class="value highlight">{{ userInfo.points || 0 }} 分</span>
            </div>
            <div class="info-item">
              <span class="label">账号状态：</span>
              <span class="value">
                <el-tag size="small" type="success" v-if="userInfo.status === 0">正常</el-tag>
                <el-tag size="small" type="danger" v-else>禁用</el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span class="value">{{ formatTime(userInfo.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>账号安全设置</span>
            </div>
          </template>

          <el-form
              ref="pwdFormRef"
              :model="pwdForm"
              :rules="pwdRules"
              label-width="100px"
              class="pwd-form"
          >
            <el-form-item label="原密码" prop="oldPwd">
              <el-input
                  v-model="pwdForm.oldPwd"
                  type="password"
                  show-password
                  placeholder="请输入当前使用的密码"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPwd">
              <el-input
                  v-model="pwdForm.newPwd"
                  type="password"
                  show-password
                  placeholder="请输入新密码（至少6位）"
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPwd">
              <el-input
                  v-model="pwdForm.confirmPwd"
                  type="password"
                  show-password
                  placeholder="请再次输入新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitPwdChange" :loading="submitLoading">确认修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
// 如果你的密码在数据库是 md5 加密的，建议前端引入 md5 库处理后再发送
import md5 from 'js-md5'

const router = useRouter()
const loading = ref(false)
const userInfo = ref({})

const pwdFormRef = ref(null)
const submitLoading = ref(false)
const pwdForm = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
})

// 密码一致性校验
const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPwd) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = reactive({
  oldPwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
})

const formatTime = (timeStr) => {
  return timeStr ? timeStr.replace('T', ' ') : ''
}

// 获取最新用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await request.get(`/api/user/info?id=${localUser.id}`)
    if (res) {
      userInfo.value = res
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  } finally {
    loading.value = false
  }
}

// 提交密码修改
const submitPwdChange = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 数据库目前存储的是加密后的密码，发送前进行 md5 加密
        const postData = {
          id: userInfo.value.id,
          oldPwd: md5(pwdForm.oldPwd),
          newPwd: md5(pwdForm.newPwd)
        }

        await request.post('/api/user/updatePwd', postData)
        ElMessage.success('密码修改成功，请重新登录')

        // 清除缓存并跳转登录页
        localStorage.removeItem('userInfo')
        setTimeout(() => {
          router.push('/login')
        }, 1500)

      } catch (error) {
        console.error('密码修改失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  pwdFormRef.value?.resetFields()
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
}
.user-card {
  text-align: center;
  padding-top: 20px;
}
.avatar-wrapper {
  margin-bottom: 15px;
}
.user-info .phone {
  font-size: 20px;
  color: #303133;
  margin: 0 0 10px 0;
}
.role-tag {
  margin-bottom: 20px;
}
.info-list {
  text-align: left;
  padding: 0 10px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
}
.info-item .label {
  color: #909399;
}
.info-item .value {
  color: #303133;
}
.highlight {
  color: #f56c6c !important;
  font-weight: bold;
}
.card-header {
  font-weight: bold;
}
.pwd-form {
  max-width: 400px;
  margin-top: 20px;
}
</style>
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

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card" shadow="hover">
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
                    <span class="complaint-time">{{ formatTime(item.createTime) }}</span>
                  </div>
                  <div class="complaint-content">{{ item.content }}</div>
                  <div v-if="item.reply" class="complaint-reply">
                    <span style="color: #409EFF; font-weight: bold;">管理员回复：</span>{{ item.reply }}
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无反馈记录" :image-size="60" />
            </el-col>
          </el-row>
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

const complaintForm = reactive({ content: '' })
const complaintLoading = ref(false)
const myComplaints = ref([])

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

onMounted(() => {
  fetchUserInfo()
  fetchMyComplaints()
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
.complaint-item {
  padding: 10px 12px;
  margin-bottom: 10px;
  background: #f9f9f9;
  border-radius: 6px;
  border-left: 3px solid #dcdfe6;
}
.complaint-item:has(.complaint-reply) { border-left-color: #409EFF; }
.complaint-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.complaint-time { font-size: 12px; color: #909399; }
.complaint-content { font-size: 13px; color: #303133; margin-bottom: 6px; }
.complaint-reply { font-size: 13px; color: #606266; background: #ecf5ff; padding: 6px 10px; border-radius: 4px; }
</style>
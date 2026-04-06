<template>
  <div class="login-container">
    <div class="color-blob blob-1"></div>
    <div class="color-blob blob-2"></div>
    <div class="color-blob blob-3"></div>

    <div class="login-box">
      <div class="header">
        <h2>{{ isLogin ? '欢迎回来' : '加入爱心公益' }}</h2>
        <p class="subtitle">物资捐助管理系统</p>
      </div>

      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          size="large"
          class="form-content"
      >
        <el-form-item prop="role">
          <el-radio-group v-model="formData.role" class="role-group">
            <el-radio-button label="donor">捐助者</el-radio-button>
            <el-radio-button label="recipient">受助方</el-radio-button>
            <el-radio-button label="admin" :disabled="!isLogin">管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
              v-model="formData.phone"
              placeholder="请输入11位手机号"
              :prefix-icon="User"
              clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <template v-if="!isLogin && formData.role === 'recipient'">
          <el-form-item prop="qualifications">
            <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                list-type="picture-card"
                :limit="1"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip text-red">
                  受助方必须上传机构或个人资质证明，待管理员审核通过后方可登录。
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleSubmit" :loading="loading">
            {{ isLogin ? '登 录' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="toggle-text">
        <span v-if="isLogin">还没有账号？ <a @click="toggleMode">立即注册</a></span>
        <span v-else>已有账号？ <a @click="toggleMode">直接登录</a></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../../utils/request' // 引入我们刚才封装的网络请求工具

const router = useRouter()
const isLogin = ref(true) // true为登录模式，false为注册模式
const formRef = ref(null)
const loading = ref(false)

// 表单数据绑定
const formData = reactive({
  phone: '',
  password: '',
  role: 'donor', // 默认选中捐助者
  qualifications: ''
})

// 手机号正则校验逻辑
const validatePhone = (rule, value, callback) => {
  const reg = /^1[3-9]\d{9}$/
  if (value === '') {
    callback(new Error('手机号不能为空'))
  } else if (!reg.test(value)) {
    callback(new Error('请输入格式正确的11位手机号码'))
  } else {
    callback() // 校验通过必须调用空 callback
  }
}

// 表单校验规则
const rules = reactive({
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  // 切换时重置表单状态，并将角色切回默认的捐助者（防止注册时卡在管理员选项上）
  formRef.value?.resetFields()
  formData.role = 'donor'
}

// 提交表单对接后端
const handleSubmit = async () => {
  if (!formRef.value) return

  // 触发整个表单的规则校验
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isLogin.value) {
          // 【调用登录接口】
          const userData = await request.post('/api/auth/login', formData)
          ElMessage.success('登录成功')

          // 将后端返回的用户信息存入本地
          localStorage.setItem('userInfo', JSON.stringify(userData))

          // 登录成功后跳转到对应的角色工作台（路由还没建的话，点击会没反应或报路由找不到，这是正常的，后续我们会建）
          if (userData.role === 'donor') {
            router.push('/donor')
          } else if (userData.role === 'recipient') {
            router.push('/recipient')
          } else if (userData.role === 'admin') {
            router.push('/admin')
          }

        } else {
          // 【调用注册接口】
          await request.post('/api/auth/register', formData)
          ElMessage.success('注册成功，请直接登录')
          toggleMode() // 注册成功后自动切换回登录面板
        }
      } catch (error) {
        console.error("请求出错:", error)
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请检查填写的内容格式')
    }
  })
}
</script>

<style scoped>
/* 整个页面的容器，采用深浅交替的现代背景色 */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f4f8;
  position: relative;
  overflow: hidden;
}

/* 动态背景光晕特效 */
.color-blob {
  position: absolute;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.6;
}
.blob-1 {
  width: 400px;
  height: 400px;
  background: #ff9a9e;
  top: -100px;
  left: -100px;
  border-radius: 50%;
  animation: float 8s ease-in-out infinite;
}
.blob-2 {
  width: 500px;
  height: 500px;
  background: #fecfef;
  bottom: -150px;
  right: -100px;
  border-radius: 50%;
  animation: float 10s ease-in-out infinite reverse;
}
.blob-3 {
  width: 300px;
  height: 300px;
  background: #a1c4fd;
  bottom: 20%;
  left: 20%;
  border-radius: 50%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

/* 登录/注册 卡片毛玻璃效果 */
.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  z-index: 1;
  transition: all 0.3s ease;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}
.header h2 {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
  letter-spacing: 1px;
}
.header .subtitle {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.form-content {
  margin-top: 20px;
}

/* 角色选择器充满宽度 */
.role-group {
  width: 100%;
  display: flex;
}
:deep(.el-radio-button) {
  flex: 1;
}
:deep(.el-radio-button__inner) {
  width: 100%;
}

.submit-btn {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 2px;
  height: 45px;
  margin-top: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: opacity 0.3s;
}
.submit-btn:hover {
  opacity: 0.9;
}

.toggle-text {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}
.toggle-text a {
  color: #764ba2;
  cursor: pointer;
  font-weight: bold;
  text-decoration: none;
}
.toggle-text a:hover {
  text-decoration: underline;
}

.text-red {
  color: #f56c6c;
  line-height: 1.4;
  margin-top: 5px;
}
</style>
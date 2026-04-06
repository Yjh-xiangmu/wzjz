<template>
  <div class="demands-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>需求管理列表</span>
          <el-button type="primary" icon="Plus" @click="dialogVisible = true">发布新需求</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="所需物资" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="address" label="收货地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.urgency === '特急' ? 'danger' : (scope.row.urgency === '紧急' ? 'warning' : 'info')">
              {{ scope.row.urgency }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">求助中</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="primary">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="danger">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170">
          <template #default="scope">
            {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button link type="danger" size="small" v-if="scope.row.status === 0 || scope.row.status === 1">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布需求求助" width="550px" :before-close="handleClose">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="物资名称" prop="title">
          <el-input v-model="formData.title" placeholder="如：50套冬用棉被" />
        </el-form-item>
        <el-form-item label="所需数量" prop="quantity">
          <el-input-number v-model="formData.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="formData.urgency">
            <el-radio-button label="一般">一般</el-radio-button>
            <el-radio-button label="紧急">紧急</el-radio-button>
            <el-radio-button label="特急">特急</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细收货地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入现场联系人电话" />
        </el-form-item>
        <el-form-item label="背景说明" prop="background">
          <el-input v-model="formData.background" type="textarea" :rows="3" placeholder="请详细描述受益群体的情况..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确认发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  title: '', quantity: 1, urgency: '一般', address: '', contactPhone: '', background: ''
})

const rules = reactive({
  title: [{ required: true, message: '请输入所需物资名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  background: [{ required: true, message: '请输入背景说明', trigger: 'blur' }]
})

const fetchList = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const res = await request.get(`/api/demand/myList?recipientId=${userInfo.id}`)
    tableData.value = res || []
  } catch (error) {
    console.error("获取列表失败", error)
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        await request.post('/api/demand/publish', { ...formData, recipientId: userInfo.id })
        ElMessage.success('需求发布成功，等待审核')
        handleClose()
        fetchList()
      } catch (error) {
        console.error("发布失败", error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleClose = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => fetchList())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
</style>
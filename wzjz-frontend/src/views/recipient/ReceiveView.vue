<template>
  <div class="receive-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>接收确认工作台</span>
          <span class="sub-text">记录社会各界送达的温暖</span>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column label="实拍" width="80">
          <template #default="scope">
            <el-image
                v-if="getFirstImage(scope.row.images)"
                style="width: 40px; height: 40px; border-radius: 4px;"
                :src="'http://localhost:8080' + getFirstImage(scope.row.images)"
                :preview-src-list="['http://localhost:8080' + getFirstImage(scope.row.images)]"
                preview-teleported
                fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column prop="material_name" label="捐助物资" min-width="120" />

        <el-table-column prop="demand_title" label="对应需求项目" min-width="150" />

        <el-table-column prop="donor_phone" label="爱心人士账号" width="120" />

        <el-table-column prop="logistics_status" label="物流状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.logistics_status === 0" type="info">待发货</el-tag>
            <el-tag v-else-if="scope.row.logistics_status === 1" type="warning">运输中</el-tag>
            <el-tag v-else-if="scope.row.logistics_status === 2" type="success">已签收</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="create_time" label="匹配时间" width="160">
          <template #default="scope">
            {{ formatTime(scope.row.create_time) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <span v-if="scope.row.logistics_status === 0" class="wait-text">等待发货</span>
            <el-button
                v-else-if="scope.row.logistics_status === 1"
                type="primary"
                size="small"
                @click="handleReceive(scope.row.order_id)"
            >
              确认收到
            </el-button>
            <span v-else-if="scope.row.logistics_status === 2" class="done-text">交易完成</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)

const getFirstImage = (imgStr) => {
  if (!imgStr) return null
  const imgs = imgStr.split(',').filter(i => i.trim())
  return imgs.length > 0 ? imgs[0] : null
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

const fetchList = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    const res = await request.get(`/api/order/recipientList?recipientId=${userInfo.id}`)
    tableData.value = res || []
  } catch (error) {
    console.error("获取列表失败", error)
  } finally {
    loading.value = false
  }
}

const handleReceive = (orderId) => {
  ElMessageBox.confirm(
      '请务必确认您已实际收到该物资，确认后系统将为捐助者发放爱心积分。是否继续？',
      '签收确认',
      { confirmButtonText: '确定签收', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await request.post(`/api/order/confirmReceive/${orderId}`)
      ElMessage.success('签收成功！')
      fetchList() // 刷新列表
    } catch (error) {
      console.error("签收失败", error)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
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
.done-text {
  color: #67c23a;
  font-size: 13px;
}
</style>
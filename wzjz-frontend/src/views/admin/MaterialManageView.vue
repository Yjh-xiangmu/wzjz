<template>
  <div class="material-manage-container">
    <el-card shadow="never" class="table-card">
      <div class="filter-container" style="margin-bottom: 20px; display: flex; gap: 15px;">
        <el-select v-model="filterStatus" placeholder="按状态筛选物资" clearable @change="fetchMaterials" style="width: 180px;">
          <el-option label="待捐助 (正常展示)" :value="0" />
          <el-option label="已匹配待发货" :value="1" />
          <el-option label="物流运输中" :value="2" />
          <el-option label="已签收完成" :value="3" />
          <el-option label="已违规下架" :value="4" />
        </el-select>
        <el-button type="primary" icon="Refresh" @click="fetchMaterials">刷新列表</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column label="物资主图" width="90" align="center">
          <template #default="scope">
            <el-image
                v-if="getFirstImage(scope.row.images)"
                style="width: 45px; height: 45px; border-radius: 4px;"
                :src="'http://localhost:8080' + getFirstImage(scope.row.images)"
                :preview-src-list="splitImages(scope.row.images).map(i => 'http://localhost:8080' + i)"
                preview-teleported
                fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column prop="name" label="物资名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="donor_phone" label="发布人(捐助者)" width="130" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="condition_degree" label="新旧程度" width="100" />

        <el-table-column prop="status" label="当前状态" width="130" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="success">待捐助</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="warning">已匹配</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="primary">运输中</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="info">已签收</el-tag>
            <el-tag v-else-if="scope.row.status === 4" type="danger">已下架</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="create_time" label="发布时间" width="160">
          <template #default="scope">{{ formatTime(scope.row.create_time) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" type="danger" link size="small" @click="auditAction(scope.row.id, 4)">强制下架</el-button>
            <el-button v-if="scope.row.status === 4" type="success" link size="small" @click="auditAction(scope.row.id, 0)">恢复展示</el-button>
            <el-button link type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="物资详细信息" width="600px">
      <div v-if="currentDetail" style="line-height: 1.8;">
        <p><strong>物资名称：</strong>{{ currentDetail.name }}</p>
        <p><strong>发布人账号：</strong>{{ currentDetail.donor_phone }}</p>
        <p><strong>物资分类：</strong>{{ currentDetail.category }}</p>
        <p><strong>提供数量：</strong>{{ currentDetail.quantity }}</p>
        <p><strong>新旧程度：</strong>{{ currentDetail.condition_degree }}</p>
        <p><strong>有效期：</strong>{{ currentDetail.validity_date || '无限制' }}</p>
        <div style="background-color: #f5f7fa; padding: 10px; border-radius: 6px; margin-top: 10px; margin-bottom: 15px;">
          <strong>补充说明：</strong>
          <p style="margin-top: 5px; white-space: pre-wrap; color: #606266;">{{ currentDetail.description || '无' }}</p>
        </div>
        <strong>所有实拍图片：</strong>
        <div style="margin-top: 10px; display: flex; gap: 10px; flex-wrap: wrap;">
          <el-image
              v-for="(img, index) in splitImages(currentDetail.images)"
              :key="index"
              style="width: 100px; height: 100px; border-radius: 6px; border: 1px solid #eee;"
              :src="'http://localhost:8080' + img"
              :preview-src-list="splitImages(currentDetail.images).map(i => 'http://localhost:8080' + i)"
              preview-teleported
              fit="cover"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const filterStatus = ref(null)

const detailVisible = ref(false)
const currentDetail = ref(null)

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''
const splitImages = (imgStr) => imgStr ? imgStr.split(',').filter(i => i.trim()) : []
const getFirstImage = (imgStr) => splitImages(imgStr)[0] || null

const fetchMaterials = async () => {
  loading.value = true
  try {
    let url = '/api/material/admin/list'
    if (filterStatus.value !== null && filterStatus.value !== '') {
      url += `?status=${filterStatus.value}`
    }
    const res = await request.get(url)
    tableData.value = res || []
  } catch (error) {
    console.error('获取物资列表失败', error)
  } finally {
    loading.value = false
  }
}

const auditAction = (id, status) => {
  const actionText = status === 4 ? '违规强制下架' : '恢复正常展示'
  const confirmType = status === 4 ? 'error' : 'warning'

  ElMessageBox.confirm(`确定要对该物资执行 [${actionText}] 操作吗？`, '安全确认', { type: confirmType })
      .then(async () => {
        await request.post(`/api/material/admin/changeStatus?id=${id}&status=${status}`)
        ElMessage.success('操作执行成功')
        fetchMaterials()
      })
      .catch(() => {})
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

onMounted(() => fetchMaterials())
</script>

<style scoped>
.table-card { border-radius: 8px; }
</style>
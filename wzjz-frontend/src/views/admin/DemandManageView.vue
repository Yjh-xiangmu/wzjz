<template>
  <div class="demand-manage-container">
    <el-card shadow="never" class="table-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核项目" name="pending">
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="项目名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="recipient_phone" label="发起人账号" width="130" />
            <el-table-column prop="org_name" label="机构名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="quantity" label="需求量" width="80" />
            <el-table-column prop="urgency" label="紧急度" width="90">
              <template #default="scope">
                <el-tag :type="scope.row.urgency === '特急' ? 'danger' : (scope.row.urgency === '紧急' ? 'warning' : 'info')">
                  {{ scope.row.urgency }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="提交时间" width="160">
              <template #default="scope">{{ formatTime(scope.row.create_time) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="success" size="small" @click="auditAction(scope.row.id, 1)">通过</el-button>
                <el-button type="danger" size="small" @click="auditAction(scope.row.id, 3)">驳回</el-button>
                <el-button link type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="全部项目记录" name="all">
          <div class="filter-container" style="margin-bottom: 15px;">
            <el-select v-model="filterStatus" placeholder="按状态筛选" clearable @change="fetchDemands" style="width: 150px;">
              <el-option label="待审核" :value="0" />
              <el-option label="求助中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已下架" :value="3" />
            </el-select>
          </div>
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="title" label="项目名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="recipient_phone" label="发起方" width="120" />
            <el-table-column prop="quantity" label="目标数量" width="90" />
            <el-table-column prop="matched_quantity" label="已筹集" width="90">
              <template #default="scope">
                <span style="color: #67c23a; font-weight: bold;">{{ scope.row.matched_quantity || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="当前状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">求助中</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="primary">已完成</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="danger">已下架</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="时间" width="160">
              <template #default="scope">{{ formatTime(scope.row.create_time) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="130" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 1" type="danger" link size="small" @click="auditAction(scope.row.id, 3)">强制下架</el-button>
                <el-button v-if="scope.row.status === 3" type="success" link size="small" @click="auditAction(scope.row.id, 1)">恢复上线</el-button>
                <el-button link type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailVisible" title="需求项目详情" width="600px">
      <div v-if="currentDetail" style="line-height: 1.8;">
        <p><strong>项目名称：</strong>{{ currentDetail.title }}</p>
        <p><strong>发起账号：</strong>{{ currentDetail.recipient_phone }} ({{ currentDetail.org_name || '个人' }})</p>
        <p><strong>需求数量：</strong>{{ currentDetail.quantity }}</p>
        <p><strong>紧急程度：</strong><el-tag size="small">{{ currentDetail.urgency }}</el-tag></p>
        <p><strong>收货地址：</strong>{{ currentDetail.address }}</p>
        <p><strong>联系电话：</strong>{{ currentDetail.contact_phone }}</p>
        <div style="background-color: #f5f7fa; padding: 10px; border-radius: 6px; margin-top: 10px;">
          <strong>背景说明：</strong>
          <p style="margin-top: 5px; white-space: pre-wrap; color: #606266;">{{ currentDetail.background }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('pending')
const filterStatus = ref(null)
const tableData = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const currentDetail = ref(null)

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''

const fetchDemands = async () => {
  loading.value = true
  try {
    let url = '/api/demand/admin/list'
    if (activeTab.value === 'pending') {
      url += '?status=0'
    } else {
      url += filterStatus.value !== null && filterStatus.value !== '' ? `?status=${filterStatus.value}` : ''
    }
    const res = await request.get(url)
    tableData.value = res || []
  } catch (error) {
    console.error('获取需求列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  filterStatus.value = null
  fetchDemands()
}

const auditAction = (id, status) => {
  const actionText = status === 1 ? '审批通过 / 恢复上线' : '驳回 / 强制下架'
  ElMessageBox.confirm(`确定要对该项目执行 [${actionText}] 操作吗？`, '安全确认', { type: 'warning' })
      .then(async () => {
        await request.post(`/api/demand/admin/changeStatus?id=${id}&status=${status}`)
        ElMessage.success('操作执行成功')
        fetchDemands()
      })
      .catch(() => {})
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

onMounted(() => fetchDemands())
</script>

<style scoped>
.table-card { border-radius: 8px; }
</style>
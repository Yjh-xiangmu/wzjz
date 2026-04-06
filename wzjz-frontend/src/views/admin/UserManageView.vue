<template>
  <div class="user-manage-container">
    <el-card shadow="never" class="table-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="受助方待审核" name="pending">
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="phone" label="注册账号" width="150" />
            <el-table-column prop="orgName" label="机构名称" min-width="150" />
            <el-table-column label="资质照片" width="120">
              <template #default="scope">
                <el-image
                    v-if="getFirstImage(scope.row.qualifications)"
                    style="width: 40px; height: 40px; border-radius: 4px;"
                    :src="'http://localhost:8080' + getFirstImage(scope.row.qualifications)"
                    :preview-src-list="splitImages(scope.row.qualifications).map(i => 'http://localhost:8080' + i)"
                    preview-teleported
                    fit="cover"
                />
                <span v-else style="color: #999; font-size: 12px;">未上传</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="170">
              <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="success" size="small" @click="auditAction(scope.row.id, 0)">通过</el-button>
                <el-button type="danger" size="small" @click="auditAction(scope.row.id, 1)">驳回</el-button>
                <el-button link type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="全量用户列表" name="all">
          <div class="filter-container" style="margin-bottom: 15px;">
            <el-select v-model="filterRole" placeholder="按角色筛选" clearable @change="fetchUsers" style="width: 150px; margin-right: 10px;">
              <el-option label="捐助者" value="donor" />
              <el-option label="受助方" value="recipient" />
            </el-select>
          </div>
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="phone" label="手机号/账号" width="150" />
            <el-table-column prop="role" label="角色类型" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'donor' ? 'primary' : (scope.row.role === 'recipient' ? 'warning' : 'info')">
                  {{ scope.row.role === 'donor' ? '捐助者' : (scope.row.role === 'recipient' ? '受助方' : '管理员') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="爱心积分" width="100" />
            <el-table-column prop="status" label="当前状态" width="120">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="success">正常</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="danger">禁用</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="info">待审核</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="170">
              <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" type="danger" link size="small" @click="auditAction(scope.row.id, 1)">封禁账号</el-button>
                <el-button v-if="scope.row.status === 1" type="success" link size="small" @click="auditAction(scope.row.id, 0)">解封恢复</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailVisible" title="受助方机构详情" width="600px">
      <div v-if="currentDetail" style="line-height: 1.8;">
        <p><strong>账号：</strong>{{ currentDetail.phone }}</p>
        <p><strong>机构名称：</strong>{{ currentDetail.orgName || '未填写' }}</p>
        <p><strong>机构简介：</strong>{{ currentDetail.orgDescription || '未填写' }}</p>
        <div>
          <strong>资质证明文件：</strong>
          <div style="margin-top: 10px; display: flex; gap: 10px; flex-wrap: wrap;">
            <el-image
                v-for="(img, index) in splitImages(currentDetail.qualifications)"
                :key="index"
                style="width: 100px; height: 100px; border-radius: 6px; border: 1px solid #eee;"
                :src="'http://localhost:8080' + img"
                :preview-src-list="splitImages(currentDetail.qualifications).map(i => 'http://localhost:8080' + i)"
                preview-teleported
                fit="cover"
            />
          </div>
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
const filterRole = ref('')
const tableData = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const currentDetail = ref(null)

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(0, 16) : ''
const splitImages = (imgStr) => imgStr ? imgStr.split(',').filter(i => i.trim()) : []
const getFirstImage = (imgStr) => splitImages(imgStr)[0] || null

const fetchUsers = async () => {
  loading.value = true
  try {
    let url = '/api/user/admin/list'
    if (activeTab.value === 'pending') {
      url += '?role=recipient&status=2'
    } else {
      url += filterRole.value ? `?role=${filterRole.value}` : ''
    }
    const res = await request.get(url)
    tableData.value = res || []
  } catch (error) {
    console.error('数据获取失败', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  filterRole.value = ''
  fetchUsers()
}

const auditAction = (id, status) => {
  const actionText = status === 0 ? '通过 / 解封' : '驳回 / 封禁'
  ElMessageBox.confirm(`确定要执行 [${actionText}] 操作吗？`, '提示', { type: 'warning' })
      .then(async () => {
        await request.post(`/api/user/admin/changeStatus?id=${id}&status=${status}`)
        ElMessage.success('操作成功')
        fetchUsers()
      })
      .catch(() => {})
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

onMounted(() => fetchUsers())
</script>

<style scoped>
.table-card { border-radius: 8px; }
</style>
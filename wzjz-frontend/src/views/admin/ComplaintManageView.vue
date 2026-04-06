<template>
  <div class="complaint-manage">
    <el-table :data="complaintList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="提交人ID" width="100" />
      <el-table-column prop="content" label="反馈内容" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">
            {{ row.status === 1 ? '已处理' : '待处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reply" label="回复内容" />
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
              size="small"
              type="primary"
              @click="handleReply(row)"
              :disabled="row.status === 1">
            回复处理
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="处理意见反馈">
      <el-form :model="form" label-width="80px">
        <el-form-item label="反馈内容">
          <el-input type="textarea" v-model="form.content" disabled :rows="3" />
        </el-form-item>
        <el-form-item label="回复">
          <el-input type="textarea" v-model="form.reply" :rows="4" placeholder="请输入回复内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确认回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const complaintList = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, content: '', reply: '' })

const loadData = () => {
  request.get('/api/complaint/admin/list').then(res => {
    complaintList.value = res || []
  })
}

const handleReply = (row) => {
  form.value = { id: row.id, content: row.content, reply: row.reply || '' }
  dialogVisible.value = true
}

const submitReply = () => {
  if (!form.value.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }
  request.post('/api/complaint/handle', form.value).then(() => {
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadData()
  })
}

onMounted(() => {
  loadData()
})
</script>
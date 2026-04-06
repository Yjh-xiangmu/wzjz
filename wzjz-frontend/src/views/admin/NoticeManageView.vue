<template>
  <div class="notice-manage">
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 20px;">发布新公告/表彰</el-button>

    <el-table :data="noticeList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.type === '表彰' ? 'success' : 'info'">{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑内容' : '发布内容'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="公告" value="公告" />
            <el-option label="表彰" value="表彰" />
            <el-option label="资讯" value="资讯" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="正文内容">
          <el-input type="textarea" v-model="form.content" :rows="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const noticeList = ref([])
const dialogVisible = ref(false)
const form = ref({ type: '公告', title: '', content: '' })

const loadData = () => {
  request.get('/api/notice/list').then(res => {
    noticeList.value = res || []
  })
}

const handleAdd = () => {
  form.value = { type: '公告', title: '', content: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = () => {
  request.post('/api/notice/save', form.value).then(() => {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该记录吗?', '提示', { type: 'warning' }).then(() => {
    request.delete(`/api/notice/delete/${id}`).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  })
}

onMounted(() => {
  loadData()
})
</script>
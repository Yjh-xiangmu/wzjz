<template>
  <div class="category-manage">
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 20px;">新增分类</el-button>

    <el-table :data="categoryList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sort" label="显示排序" width="120" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="例如：衣物服装" />
        </el-form-item>
        <el-form-item label="显示排序">
          <el-input-number v-model="form.sort" :min="0" label="排序值" />
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

const categoryList = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', sort: 0 })

const loadData = () => {
  request.get('/api/category/list').then(res => {
    categoryList.value = res || []
  })
}

const handleAdd = () => {
  form.value = { id: null, name: '', sort: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = () => {
  if (!form.value.name) {
    ElMessage.warning('分类名称不能为空')
    return
  }
  request.post('/api/category/save', form.value).then(() => {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该分类吗?', '提示', { type: 'warning' }).then(() => {
    request.delete(`/api/category/delete/${id}`).then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
  })
}

onMounted(() => {
  loadData()
})
</script>
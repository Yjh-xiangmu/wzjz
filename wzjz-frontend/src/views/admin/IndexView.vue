<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <template #header>注册用户总数</template>
          <div class="num">{{ stats.userCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <template #header>累计捐赠物资</template>
          <div class="num">{{ stats.materialCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <template #header>求助需求分布</template>
          <div class="num">{{ stats.demandCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 30px;" header="最近平台公告">
      <el-timeline>
        <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.createTime">
          {{ item.title }} 【{{ item.type }}】
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({})
const notices = ref([])

onMounted(() => {
  // 获取统计数据
  request.get('/api/stat/admin/overview').then(res => {
    if (res) stats.value = res
  })
  // 获取最新公告
  request.get('/api/notice/list').then(res => {
    if (res) notices.value = res.slice(0, 5)
  })
})
</script>

<style scoped>
.stat-card { text-align: center; }
.num { font-size: 30px; font-weight: bold; color: #409EFF; padding: 10px 0; }
</style>
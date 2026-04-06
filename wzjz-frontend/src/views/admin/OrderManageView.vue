<template>
  <div class="order-manage">
    <el-table :data="orderList" border style="width: 100%">
      <el-table-column prop="id" label="订单ID" width="80" />
      <el-table-column prop="materialId" label="物资ID" width="100" />
      <el-table-column prop="demandId" label="需求ID" width="100" />
      <el-table-column prop="donorId" label="捐助者ID" width="100" />
      <el-table-column prop="recipientId" label="受助方ID" width="100" />
      <el-table-column label="物流状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.logisticsStatus === 0" type="warning">待发货</el-tag>
          <el-tag v-else-if="row.logisticsStatus === 1" type="primary">运输中</el-tag>
          <el-tag v-else-if="row.logisticsStatus === 2" type="success">已签收</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="logisticsCompany" label="物流公司" />
      <el-table-column prop="logisticsNo" label="物流单号" />
      <el-table-column prop="createTime" label="匹配时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const orderList = ref([])

const loadData = () => {
  request.get('/api/order/admin/list').then(res => {
    orderList.value = res || []
  })
}

onMounted(() => {
  loadData()
})
</script>
<template>
  <div class="chat-container">
    <el-card class="chat-card" shadow="hover" body-style="padding: 0; display: flex; height: calc(100vh - 120px);">
      <div class="chat-sidebar">
        <div class="sidebar-header">沟通记录</div>
        <div class="contact-list" v-loading="listLoading">
          <div
              v-for="item in contactList"
              :key="item.contact_id"
              :class="['contact-item', { active: currentContact && currentContact.contact_id === item.contact_id }]"
              @click="selectContact(item)"
          >
            <el-avatar class="contact-avatar" :size="40" style="background: #E6A23C;">捐</el-avatar>
            <div class="contact-info">
              <div class="contact-name">爱心人士: {{ item.contact_phone }}</div>
            </div>
          </div>
          <el-empty v-if="contactList.length === 0" description="暂无联系人" :image-size="60"></el-empty>
        </div>
      </div>

      <div class="chat-main">
        <template v-if="currentContact">
          <div class="chat-header">
            <span class="chat-title">与 {{ currentContact.contact_phone }} 沟通中</span>
            <el-button type="primary" plain size="small" icon="List" @click="drawerVisible = true">收到的捐赠</el-button>
          </div>
          <div class="message-area" ref="messageAreaRef">
            <div class="message-list">
              <div v-for="msg in messageList" :key="msg.id" :class="['message-item', msg.senderId === userInfo.id ? 'message-right' : 'message-left']">
                <el-avatar v-if="msg.senderId !== userInfo.id" :size="36" class="msg-avatar" style="background: #E6A23C;">捐</el-avatar>
                <div class="message-content">
                  <div class="message-time" v-if="msg.senderId !== userInfo.id">{{ formatTime(msg.createTime) }}</div>
                  <div class="message-bubble">{{ msg.content }}</div>
                  <div class="message-time right-time" v-if="msg.senderId === userInfo.id">{{ formatTime(msg.createTime) }}</div>
                </div>
                <el-avatar v-if="msg.senderId === userInfo.id" :size="36" class="msg-avatar" style="background: #67C23A;">我</el-avatar>
              </div>
            </div>
          </div>
          <div class="input-area">
            <el-input v-model="inputText" type="textarea" :rows="3" resize="none" placeholder="输入留言，按 Enter 发送" @keydown.enter.prevent="sendMessage" />
            <div class="input-action"><el-button type="primary" size="small" @click="sendMessage" :loading="sendLoading">发送</el-button></div>
          </div>
        </template>
        <div v-else class="empty-chat">
          <el-icon class="empty-icon"><ChatDotRound /></el-icon><p>请在左侧选择联系人</p>
        </div>
      </div>
    </el-card>

    <el-drawer v-model="drawerVisible" :title="'来自 ' + (currentContact?.contact_phone || '') + ' 的捐赠记录'" size="400px">
      <div v-loading="recordsLoading" style="padding: 0 10px;">
        <el-card v-for="order in records" :key="order.order_id" style="margin-bottom: 15px;" shadow="never">
          <div style="font-weight: bold; margin-bottom: 8px;">{{ order.material_name }}</div>
          <div style="font-size: 13px; color: #666; margin-bottom: 4px;">对应需求：{{ order.demand_title }}</div>
          <div style="font-size: 13px; color: #666; margin-bottom: 8px;">时间：{{ order.create_time ? order.create_time.replace('T', ' ') : '' }}</div>
          <el-tag size="small" :type="order.logistics_status === 2 ? 'success' : 'warning'">
            {{ order.logistics_status === 0 ? '待发货' : (order.logistics_status === 1 ? '运输中' : '已签收') }}
          </el-tag>
        </el-card>
        <el-empty v-if="records.length === 0" description="暂无记录"></el-empty>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
// 这部分逻辑和捐助者完全一致，唯一不同是获取联系人的接口和传参顺序
import { ref, onMounted, nextTick, watch } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { ChatDotRound, List } from '@element-plus/icons-vue'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const listLoading = ref(false)
const contactList = ref([])
const currentContact = ref(null)
const messageList = ref([])
const inputText = ref('')
const sendLoading = ref(false)
const messageAreaRef = ref(null)
const drawerVisible = ref(false)
const recordsLoading = ref(false)
const records = ref([])

const formatTime = (timeStr) => timeStr ? timeStr.replace('T', ' ').substring(5, 16) : ''

const fetchContactList = async () => {
  listLoading.value = true
  try {
    const res = await request.get(`/api/order/recipientContacts?recipientId=${userInfo.id}`)
    contactList.value = res || []
  } finally {
    listLoading.value = false
  }
}

const selectContact = (contact) => {
  currentContact.value = contact
  fetchHistory(contact.contact_id)
}

const fetchHistory = async (targetId) => {
  try {
    const res = await request.get(`/api/message/history?userId1=${userInfo.id}&userId2=${targetId}`)
    messageList.value = res || []
    scrollToBottom()
  } catch (error) {}
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageAreaRef.value) messageAreaRef.value.scrollTop = messageAreaRef.value.scrollHeight
  })
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return ElMessage.warning('内容不能为空')
  sendLoading.value = true
  try {
    await request.post('/api/message/send', {
      senderId: userInfo.id,
      receiverId: currentContact.value.contact_id,
      content: inputText.value.trim()
    })
    inputText.value = ''
    await fetchHistory(currentContact.value.contact_id)
  } finally {
    sendLoading.value = false
  }
}

watch(drawerVisible, async (newVal) => {
  if (newVal && currentContact.value) {
    recordsLoading.value = true
    try {
      // 注意这里传参：donorId是对方，recipientId是自己
      const res = await request.get(`/api/order/recordsBetween?donorId=${currentContact.value.contact_id}&recipientId=${userInfo.id}`)
      records.value = res || []
    } finally {
      recordsLoading.value = false
    }
  }
})

onMounted(() => fetchContactList())
</script>

<style scoped>
/* 样式与上一个组件完全一致，为了美观直接复用 */
.chat-container { width: 100%; }
.chat-card { border-radius: 8px; }
.chat-sidebar { width: 260px; border-right: 1px solid #ebeef5; background-color: #f8f9fa; display: flex; flex-direction: column; }
.sidebar-header { height: 60px; line-height: 60px; padding: 0 20px; font-weight: bold; border-bottom: 1px solid #ebeef5; background-color: #fff; }
.contact-list { flex: 1; overflow-y: auto; }
.contact-item { display: flex; padding: 15px 20px; cursor: pointer; border-bottom: 1px solid #f0f0f0; transition: background-color 0.2s; align-items: center;}
.contact-item:hover { background-color: #eef2f9; }
.contact-item.active { background-color: #e6f1fc; }
.contact-info { margin-left: 12px; }
.contact-name { font-size: 14px; font-weight: bold; color: #333; }

.chat-main { flex: 1; display: flex; flex-direction: column; background-color: #f5f7fa; }
.empty-chat { height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #909399; }
.empty-icon { font-size: 60px; margin-bottom: 10px; color: #c0c4cc; }
.chat-header { height: 60px; padding: 0 20px; background-color: #fff; border-bottom: 1px solid #ebeef5; display: flex; justify-content: space-between; align-items: center; }
.chat-title { font-size: 16px; font-weight: bold; }

.message-area { flex: 1; padding: 20px; overflow-y: auto; }
.message-item { display: flex; margin-bottom: 20px; }
.message-left { flex-direction: row; }
.message-right { flex-direction: row; justify-content: flex-end; }
.msg-avatar { flex-shrink: 0; }
.message-content { max-width: 60%; display: flex; flex-direction: column; }
.message-left .message-content { margin-left: 12px; align-items: flex-start; }
.message-right .message-content { margin-right: 12px; align-items: flex-end; }
.message-time { font-size: 12px; color: #999; margin-bottom: 4px; }
.right-time { margin-top: 4px; margin-bottom: 0; }
.message-bubble { padding: 10px 14px; border-radius: 8px; font-size: 14px; line-height: 1.5; word-break: break-all; }
.message-left .message-bubble { background-color: #fff; border: 1px solid #ebeef5; color: #333; }
.message-right .message-bubble { background-color: #95ec69; color: #333; }

.input-area { height: 130px; background-color: #fff; border-top: 1px solid #ebeef5; padding: 10px 20px; display: flex; flex-direction: column; }
:deep(.el-textarea__inner) { border: none; box-shadow: none; padding: 0; }
:deep(.el-textarea__inner:focus) { box-shadow: none; }
.input-action { text-align: right; margin-top: 5px; }
</style>
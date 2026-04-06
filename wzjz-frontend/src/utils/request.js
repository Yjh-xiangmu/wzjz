import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 刚才后端跑起来的地址
    timeout: 5000 // 请求超时时间
})

// 响应拦截器：统一处理后端返回的 Result 对象
request.interceptors.response.use(
    response => {
        let res = response.data
        // 我们在后端定义了 code 为 200 是成功
        if (res.code === 200) {
            return res.data || res.msg
        } else {
            // 后端校验失败（比如密码错误、手机号已存在）直接弹窗报错
            ElMessage.error(res.msg || '操作失败')
            return Promise.reject(res.msg)
        }
    },
    error => {
        ElMessage.error('网络请求失败，请检查后端系统是否已启动')
        return Promise.reject(error)
    }
)

export default request
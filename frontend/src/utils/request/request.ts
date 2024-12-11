import axios, {
  type AxiosResponse,
  type InternalAxiosRequestConfig
} from 'axios'
import { ElMessage } from 'element-plus'

interface ApiResponse<T = any> {
  code: number
  data: T
  description: string
  message: string
}

// 创建实例时配置默认值
const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 添加请求拦截器
instance.interceptors.request.use(
  function (config: InternalAxiosRequestConfig) {
    // 在发送请求之前做些什么
    return config
  },
  function (error: unknown) {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
instance.interceptors.response.use(
  function (response: AxiosResponse<ApiResponse<any>>): Promise<any> {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    if (response.data.code !== 20000) {
      ElMessage({
        type: 'error',
        message:
          response.data.description || response.data.message || '哎呀，出错了'
      })
      return Promise.reject(response.data)
    }
    // 返回 response.data
    return Promise.resolve(response.data)
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    ElMessage({
      type: 'error',
      message:
        (error as any).response.data.description ||
        (error as any).response.data.message ||
        '哎呀，出错了'
    })
    return Promise.reject(error)
  }
)

export default instance

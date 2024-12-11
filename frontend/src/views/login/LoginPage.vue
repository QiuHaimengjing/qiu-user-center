<script setup lang="ts">
import FormCard from '@/views/login/components/FormCard.vue'
import { userLoginService } from '@/api/user'
import { useUserStore } from '@/stores/index'
import { useRouter } from 'vue-router'
import type { UserLoginParams, UserLoginRes } from '@/api/user/types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const title: string = '用户登录'
const tips: string = '请输入用户名和密码进行登录'
const userStore = useUserStore()

// 用户登录的方法
const userLogin = async (formData: UserLoginParams) => {
  const res: UserLoginRes = await userLoginService(formData)
  if (res.data !== null) {
    ElMessage({
      message: '登录成功',
      type: 'success',
      duration: 3000
    })
    userStore.setUserInfo(res.data)
    router.push('/')
  }
}
</script>

<template>
  <FormCard :title="title" :tips="tips" @get-userLogin="userLogin"></FormCard>
</template>

<style scoped lang="less"></style>

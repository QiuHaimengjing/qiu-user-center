<script setup lang="ts">
import FormCard from '@/views/login/components/FormCard.vue'
import { userRegisterService } from '@/api/user'
import { useRouter } from 'vue-router'
import type { UserRegisterRes } from '@/api/user/types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const title: string = '用户注册'
const tips: string = '请输入用户名和密码进行注册'

const userRegister = async (formData: any) => {
  const res: UserRegisterRes = await userRegisterService(formData)
  if (res.data === true) {
    ElMessage({
      message: '注册成功',
      type: 'success',
      duration: 2000
    })
    router.push('/login')
  }
}
</script>

<template>
  <FormCard
    :title="title"
    :tips="tips"
    @get-userRegister="userRegister"
  ></FormCard>
</template>

<style scoped lang="less"></style>

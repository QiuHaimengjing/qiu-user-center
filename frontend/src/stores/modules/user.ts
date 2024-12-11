import { ref } from 'vue'
import { defineStore } from 'pinia'

type UserInfo = {
  avatarUrl: string
  createTime: Record<string, unknown>
  email: string
  gender: number
  id: number
  phone: string
  userAccount: string
  userRole: number
  userStatus: number
  username: string
}

export const useUserStore = defineStore(
  'user',
  () => {
    const userInfo = ref<UserInfo | undefined>()
    const setUserInfo = (newUserInfo: UserInfo): void => {
      userInfo.value = newUserInfo
    }
    const getUserInfo = (): UserInfo | undefined => {
      return userInfo.value
    }
    const removeUserInfo = (): void => {
      userInfo.value = undefined
    }
    return { userInfo, setUserInfo, getUserInfo, removeUserInfo }
  },
  // 持久化
  {
    persist: true
  }
)

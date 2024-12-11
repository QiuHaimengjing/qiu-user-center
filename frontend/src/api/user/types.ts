export interface UserLoginParams {
  userAccount: string
  userPassword: string
}

export interface UserLoginRes {
  code: number
  data: {
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
  description: string
  message: string
}

export interface UserRegisterParams {
  userAccount: string
  userPassword: string
  verifyPassword: string
}

export interface UserRegisterRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface UserLogoutRes {
  code: number
  data: boolean
  description: string
  message: string
}

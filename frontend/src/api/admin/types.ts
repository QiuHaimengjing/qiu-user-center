export interface GetAllUserParams {
  pageNO: number
  pageSize: number
}

export interface GetAllUserRes {
  code: number
  data: {
    total: number
    users: {
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
    }[]
  }
  description: string
  message: string
}

export interface DeleteUserRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface UpdateUserByIdParams {
  avatarUrl?: string
  email?: string
  gender?: number
  id: number
  phone?: string
  userAccount?: string
  userRole?: number
  userStatus?: number
  username?: string
}

export interface UpdateUserByIdRes {
  code: number
  data: boolean
  description: string
  message: string
}

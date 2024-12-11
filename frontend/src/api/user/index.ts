import request from '@/utils/request/request'
import type {
  UserLoginParams,
  UserLoginRes,
  UserLogoutRes,
  UserRegisterParams,
  UserRegisterRes
} from './types'

export const userLoginService = (
  data: UserLoginParams
): Promise<UserLoginRes> => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export const userRegisterService = (
  data: UserRegisterParams
): Promise<UserRegisterRes> => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export const userLogoutService = (): Promise<UserLogoutRes> => {
  return request.post('/user/logout')
}

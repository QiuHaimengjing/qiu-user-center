import request from '@/utils/request/request'
import type {
  DeleteUserRes,
  GetAllUserParams,
  GetAllUserRes,
  UpdateUserByIdParams,
  UpdateUserByIdRes
} from './types'

export const getUserListService = (
  data: GetAllUserParams
): Promise<GetAllUserRes> => {
  return request({
    url: '/user/all',
    method: 'post',
    data
  })
}

export const deleteUserService = (id: number): Promise<DeleteUserRes> => {
  return request({
    url: '/user/delete',
    method: 'delete',
    params: { id }
  })
}

export const updateUserByIdService = (
  data: UpdateUserByIdParams
): Promise<UpdateUserByIdRes> => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

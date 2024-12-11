<script setup lang="ts">
import { ref } from 'vue'
import {
  getUserListService,
  updateUserByIdService,
  deleteUserService
} from '@/api/admin'
import { formatTime } from '@/utils/time/formatTime'
import type { GetAllUserRes, UpdateUserByIdParams } from '@/api/admin/types'
import type { UserTableData } from './types'
import AdminCard from './components/AdminCard.vue'
import avatar from '@/assets/images/kiana.jpg'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableRef = ref()
const tableData = ref<UserTableData[]>([])
const avatarList: any = ref([])
const total = ref()

// 获取用户列表
const defaultQueryParams = {
  pageNO: 1,
  pageSize: 5,
  refresh: true
}
const queryParams = ref({ ...defaultQueryParams })

const getUserList = async () => {
  const res: GetAllUserRes = await getUserListService(queryParams.value)
  tableData.value = res.data.users
  // 头像列表
  avatarList.value = tableData.value.map((item: any) => {
    return item.avatarUrl || avatar
  })
  total.value = res.data.total
}
getUserList()

// 分页
const pageChange = () => {
  getUserList()
}

// 修改用户信息
const dialogFormVisible = ref(false)
const genderStr = ref('')
const userRole = ref('')
const defaultUpdateParams: UpdateUserByIdParams = {
  id: 0,
  userAccount: '',
  username: '',
  gender: 0,
  phone: '',
  email: '',
  userRole: 0,
  userStatus: 0
}
const updateParams = ref<UpdateUserByIdParams>({ ...defaultUpdateParams })
const showUpdateDialog = (row: any) => {
  dialogFormVisible.value = true
  // 只要修改的数据
  updateParams.value = {
    id: row.id,
    userAccount: row.userAccount,
    username: row.username,
    gender: row.gender,
    phone: row.phone,
    email: row.email,
    userRole: row.userRole,
    userStatus: row.userStatus
  }
  // 处理性别显示
  if (updateParams.value.gender === 1) {
    genderStr.value = '男'
  } else if (updateParams.value.gender === 0) {
    genderStr.value = '女'
  } else {
    genderStr.value = '未知'
  }
  // 处理用户角色显示
  if (updateParams.value.userRole === 0) {
    userRole.value = '普通用户'
  } else {
    userRole.value = '管理员'
  }
}
const updateUserInfo = async () => {
  // 处理性别
  if (genderStr.value === '男') {
    updateParams.value.gender = 1
  } else if (genderStr.value === '女') {
    updateParams.value.gender = 0
  } else {
    updateParams.value.gender = 2
  }
  // 处理用户角色
  if (userRole.value === '普通用户') {
    updateParams.value.userRole = 0
  } else {
    updateParams.value.userRole = 1
  }
  const res = await updateUserByIdService(updateParams.value)
  if (res.code !== 20000) {
    return
  }
  ElMessage.success('修改成功')
  await getUserList()
  dialogFormVisible.value = false
}

// 删除用户
const deleteUserInfo = (row: any) => {
  ElMessageBox.confirm('你确定要删除该用户信息吗?此操作不可逆', '温馨提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      const res = await deleteUserService(row.id)
      if (res.code !== 20000) {
        return
      }
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
      await getUserList()
    })
    .catch(() => {})
}
</script>

<template>
  <div>
    <AdminCard title="用户管理">
      <el-table ref="tableRef" :data="tableData" style="width: 100%" border>
        <el-table-column type="selection" width="55" />
        <el-table-column label="账号" width="150">
          <template #default="{ row }">
            <el-text truncated>
              {{ row.userAccount }}
            </el-text>
          </template>
        </el-table-column>
        <el-table-column label="昵称" width="150">
          <template #default="{ row }">
            <el-text truncated>
              {{ row.username }}
            </el-text>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="100" align="center">
          <template #default="scope">
            <el-image
              :src="scope.row.avatarUrl || avatar"
              style="width: 3rem; height: 3rem"
              fit="cover"
              preview-teleported
              :preview-src-list="avatarList"
              :initial-index="scope.$index"
              hide-on-click-modal
            />
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          label="电话"
          width="160"
        ></el-table-column>
        <el-table-column
          prop="email"
          label="邮箱"
          width="180"
        ></el-table-column>
        <el-table-column
          prop="userRole"
          label="角色"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary" v-if="row.userRole === 0">普通用户</el-tag>
            <el-tag type="success" v-if="row.userRole === 1">管理员</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="userStatus"
          label="状态"
          width="80"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.userStatus === 0 ? 'success' : 'error'"
              disable-transitions
              >{{ row.userStatus === 0 ? '正常' : '异常' }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="140">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" min-width="100">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="showUpdateDialog(row)"
              >详情</el-button
            >
            <el-button
              link
              type="danger"
              size="small"
              @click="deleteUserInfo(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNO"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20, 30]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @change="pageChange"
        class="pagination"
      />
    </AdminCard>
    <!-- 编辑弹出框 -->
    <el-dialog v-model="dialogFormVisible" title="修改用户信息" width="500">
      <el-form :model="updateParams" label-width="auto">
        <el-form-item label="账号">
          <el-input v-model="updateParams.userAccount" autocomplete="off" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="updateParams.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="genderStr" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="未知" value="未知" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="updateParams.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="updateParams.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userRole" placeholder="选择角色">
            <el-option label="普通用户" value="普通用户" />
            <el-option label="管理员" value="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-switch
            v-model="updateParams.userStatus"
            :active-value="0"
            :inactive-value="1"
            class="ml-2"
            style="
              --el-switch-on-color: #13ce66;
              --el-switch-off-color: #ff4949;
            "
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="updateUserInfo"> 保存 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="less">
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}
</style>

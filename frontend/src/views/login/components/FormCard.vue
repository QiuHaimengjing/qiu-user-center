<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormRules } from 'element-plus'
import router from '@/router'
import LayoutFooter from '@/views/layouts/components/LayoutFooter.vue'

type RuleForm = {
  userAccount: string
  userPassword: string
  verifyPassword: string
}

// 获取组件传递的参数
const props = defineProps({
  title: {
    require: true,
    type: String
  },
  tips: {
    require: true,
    type: String
  }
})

// 表单的值
const form = ref()
const defaultFormData = {
  userAccount: '',
  userPassword: '',
  verifyPassword: ''
}
const formData = ref({
  userAccount: '',
  userPassword: '',
  verifyPassword: ''
})
// 验证规则
const rules = reactive<FormRules<RuleForm>>({
  userAccount: [
    { required: true, message: '用户名是必填的', trigger: 'blur' },
    { min: 4, message: '用户名不得小于4位', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名必须是字母、数字或下划线组成',
      trigger: 'blur'
    }
  ],
  userPassword: [
    { required: true, message: '密码是必填的', trigger: 'blur' },
    { min: 6, message: '密码不得小于6位', trigger: 'blur' }
  ],
  verifyPassword: [
    { required: true, message: '密码是必填的', trigger: 'blur' },
    { min: 6, message: '密码不得小于6位', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.value.userPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }
    }
  ]
})

// 注册按钮样式
const type = ref('')
// 判断登录还是注册
const login = ref(true)
const isLogin = () => {
  formData.value = { ...defaultFormData }
  if (props.title === '用户登录') {
    login.value = true
    type.value = 'default'
  } else {
    login.value = false
    type.value = 'primary'
  }
}
isLogin()

// 登录注册方法
const emit = defineEmits(['get-userLogin', 'get-userRegister'])
// 登录方法
const userLogin = async () => {
  await form.value.validate()
  emit('get-userLogin', formData.value)
}
// 注册方法
const userRegister = async () => {
  if (login.value) {
    router.push('/register')
    return
  }
  await form.value.validate()
  emit('get-userRegister', formData.value)
}
</script>

<template>
  <el-row>
    <!-- 左侧内容 -->
    <el-col :span="12" class="leftcontainer">
      <div style="margin-top: 1rem">
        <!-- 圆形头像 -->
        <img src="@/assets/images/kiana.jpg" class="myAvatar" />
        <span class="intro">湫的用户中心</span>
      </div>
    </el-col>

    <!-- 右侧内容 -->
    <el-col :span="12" class="rightcontainer">
      <div class="intro">一位来自乡下开发者的用户中心</div>
      <div class="title">{{ props.title }}</div>
      <el-card
        shadow="hover"
        class="card"
        :style="!login ? { height: '50%' } : {}"
      >
        <div class="welcome">{{ props.tips }}</div>
        <el-form
          ref="form"
          :model="formData"
          :rules="rules"
          status-icon
          label-width="120px"
          label-position="right"
          style="margin-top: 2rem"
        >
          <el-form-item label="用户名" prop="userAccount">
            <el-input
              autocomplete="off"
              v-model="formData.userAccount"
              style="width: 80%; height: 2.4rem"
            />
          </el-form-item>
          <el-form-item label="密码" prop="userPassword">
            <el-input
              type="password"
              autocomplete="off"
              v-model="formData.userPassword"
              style="width: 80%; height: 2.4rem"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="verifyPassword" v-if="!login">
            <el-input
              type="password"
              autocomplete="off"
              v-model="formData.verifyPassword"
              style="width: 80%; height: 2.4rem"
              show-password
            />
          </el-form-item>
          <el-form-item v-if="login">
            <el-button
              type="primary"
              style="width: 80%; height: 2.4rem"
              @click="userLogin"
              >登录</el-button
            >
          </el-form-item>
          <el-form-item>
            <el-button
              style="width: 80%; height: 2.4rem"
              :type="type"
              @click="userRegister"
              >注册</el-button
            >
          </el-form-item>
          <el-form-item v-if="!login">
            <el-button
              style="width: 80%; height: 2.4rem"
              @click="router.push('/login')"
              >去登录</el-button
            >
          </el-form-item>
        </el-form>
      </el-card>
      <LayoutFooter />
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
@font-YaHei: 'Microsoft YaHei';

.leftcontainer {
  min-height: 100vh;
  background: url('@/assets/images/yoimiya.jpg');
  background-size: cover;
  background-position: center;
  // 阴影
  box-shadow: 0px 0px 0.625rem rgba(0, 0, 0, 0.2);

  .myAvatar {
    width: 4rem;
    height: 4rem;
    border-radius: 50%;
    pointer-events: none;
    margin-left: 1rem;
    vertical-align: middle;
  }

  .intro {
    margin-left: 1rem;
    font-size: 1.25rem;
    font-weight: 700;
    color: #fff;
    letter-spacing: 0.1rem;
    font-family: @font-YaHei;
  }
}

.rightcontainer {
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .intro {
    align-self: center;
    font-weight: 700;
    width: 100%;
    height: 3rem;
    line-height: 3rem;
    text-align: center;
    font-family: @font-YaHei;
    color: #303133;
    // 毛玻璃效果
    box-shadow: 0 0 0.625rem rgba(0, 0, 0, 0.5);
    background: linear-gradient(
      to right bottom,
      rgba(255, 255, 255, 0.8),
      rgba(255, 255, 255, 0.7),
      rgba(255, 255, 255, 0.6)
    );
    backdrop-filter: blur(0.8125rem);
  }

  .title {
    align-self: center;
    font-size: 2.2rem;
    font-weight: 700;
    letter-spacing: 1rem;
    font-family: @font-YaHei;
    color: #303133;
  }

  .card {
    width: 80%;
    height: 40%;
    align-self: center;
    padding-top: 3.125rem;
    padding-bottom: 3.125rem;

    .welcome {
      text-align: center;
      font-size: 1.25rem;
      font-weight: 700;
      font-family: @font-YaHei;
      color: #303133;
    }
  }
}

.footer {
  display: flex;
  margin-bottom: 4rem;
  justify-content: center;
}
</style>

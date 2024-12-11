<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores'
import LayoutFooter from './components/LayoutFooter.vue'
import { userLogoutService } from '@/api/user'
import avatar from '@/assets/images/kiana.jpg'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

// 控制菜单
const isCollapse = ref<boolean>(false)
const collapseHandler = () => {
  isCollapse.value = !isCollapse.value
}

const route = useRoute()
const router = useRouter()
const path = ref<string>(route.path)

// 面包屑
const breadcrumbData: any = ref([])
const getCrumbData = () => {
  breadcrumbData.value = route.matched
}
const onLinkClick = (item: any) => {
  router.push(item.path)
}

// 获取'/'下的所有路由（即菜单）
const allMenu = router.getRoutes().filter((item) => item.path === '/')
let menu: any = allMenu[0].children

// 递归获取菜单不隐藏的菜单
const getShowMenu = (menu: any) => {
  return menu.filter((item: any) => {
    if (item.meta && item.meta.hidden) {
      return false
    } else {
      if (item.children && item.children.length > 0) {
        item.children = getShowMenu(item.children)
      }
      return true
    }
  })
}
// 获取当前用户角色
const role = userStore.userInfo?.userRole
// 递归获取菜单，管理员和普通用户
if (role === 1) {
  menu = getShowMenu(menu)
} else {
  menu = getShowMenu(menu)
  menu = menu.filter((item: any) => {
    if (item.meta && item.meta.role && item.meta.role === 'admin') {
      return false
    } else {
      return true
    }
  })
}

watch(
  () => route.path,
  () => {
    path.value = route.path
    getCrumbData()
  },
  {
    immediate: true
  }
)

// 退出系统
const logout = () => {
  ElMessageBox.confirm('你确定要退出吗', '退出提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      userLogoutService()
      ElMessage({
        type: 'success',
        message: '已退出'
      })
      userStore.removeUserInfo()
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<template>
  <el-container style="height: 100vh">
    <el-menu
      :default-active="path"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      style="height: 100%"
      background-color="#031529"
      text-color="#fff"
      router
    >
      <template v-for="oneItem in menu" :key="oneItem.path">
        <el-sub-menu
          v-if="oneItem.children"
          :index="oneItem.path"
          :key="oneItem.path"
        >
          <template #title>
            <component
              :is="oneItem.meta.icon"
              style="width: 1rem; height: 1rem; margin-right: 0.625rem"
            ></component>
            <span>{{ oneItem.meta.title }}</span>
          </template>
          <el-menu-item
            v-for="twoItem in oneItem.children"
            :key="twoItem.path"
            :index="twoItem.path"
          >
            <component
              :is="oneItem.meta.icon"
              style="width: 1rem; height: 1rem; margin-right: 0.625rem"
            ></component>
            <span>{{ twoItem.meta.title }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="oneItem.path">
          <component
            :is="oneItem.meta.icon"
            style="width: 1rem; height: 1rem; margin-right: 0.625rem"
          ></component>
          <span>{{ oneItem.meta.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
    <el-container style="width: 100%">
      <el-header class="header">
        <div class="expand">
          <el-icon
            size="25"
            class="icon"
            @click="collapseHandler"
            :style="
              isCollapse
                ? { transform: 'rotateY(0deg)' }
                : { transform: 'rotateY(180deg)' }
            "
            ><Expand
          /></el-icon>
        </div>
        <el-breadcrumb separator="/" class="breadcrumb">
          <transition-group name="breadcrumb">
            <el-breadcrumb-item
              v-for="(item, index) in breadcrumbData"
              :key="item.path"
            >
              <span
                v-if="index === breadcrumbData.length - 1"
                class="notChoose"
                >{{ item.meta.title }}</span
              >
              <span v-else @click="onLinkClick(item)" class="isChoose">{{
                item.meta.title
              }}</span>
            </el-breadcrumb-item>
          </transition-group>
        </el-breadcrumb>
        <div class="user">
          <el-dropdown class="dropdown" trigger="click">
            <div class="user-info">
              <el-avatar
                :size="35"
                :src="userStore.userInfo?.avatarUrl || avatar"
                class="avatar"
              />
              <el-text truncated style="width: 5rem">
                {{ userStore.userInfo?.username }}
              </el-text>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="logout"
                  >退出系统</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main style="background-color: #f5f7f9">
        <router-view v-slot="{ Component }">
          <transition name="slide" mode="out-in">
            <component :is="Component" :key="$route.path" />
          </transition>
        </router-view>
        <div style="height: 1rem"></div>
        <LayoutFooter />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped lang="less">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 12.5rem;
  min-height: 25rem;
}
.header {
  display: flex;
  z-index: 999;
  justify-content: start;
  align-items: center;
  padding-right: 0;
  box-shadow: 0 0 0.625rem rgba(0, 0, 0, 0.5);
  background: linear-gradient(
    to right bottom,
    rgba(255, 255, 255, 0.8),
    rgba(255, 255, 255, 0.7),
    rgba(255, 255, 255, 0.6)
  );
  backdrop-filter: blur(0.8125rem);
  .expand {
    margin-top: 0.3125rem;
    .icon {
      cursor: pointer;
      transition: all 1s ease;
    }
  }
  .breadcrumb {
    margin-left: 0.625rem;
    .notChoose {
    }
    .isChoose {
      font-weight: 700;
      cursor: pointer;
      transition: all 0.5s ease;
      &:hover {
        color: #409eff;
      }
    }
  }
  .user {
    margin-left: auto;
    height: 100%;
    width: 8rem;
    :deep(.el-tooltip__trigger:focus-visible) {
      outline: unset;
    }
    .dropdown {
      height: 100%;
      width: 100%;
      .user-info {
        display: flex;
        align-items: center;
        height: 100%;
        width: 100%;
        transition: all 0.5s ease;
        cursor: pointer;
        .avatar {
          vertical-align: middle;
          margin-right: 0.3rem;
          margin-left: 0.3rem;
        }
        &:hover {
          background-color: #f6f6f6;
        }
      }
    }
  }
}
.breadcrumb-move,
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}
.breadcrumb-enter-from,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(3.125rem);
}
.breadcrumb-leave-active {
  position: absolute;
}
.slide-enter-active,
.slide-leave-active {
  transition:
    opacity 0.5s ease,
    transform 0.2s ease;
}
.slide-enter-to,
.slide-leave-from {
  opacity: 0.9;
}
.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateX(-30%);
}
</style>

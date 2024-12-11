<script setup lang="ts"></script>

<template>
  <div class="footer">
    <div class="blog">
      <span>
        <el-link
          :underline="false"
          href="https://blog.invictusqiu.top"
          target="_blank"
          >我的博客</el-link
        >
      </span>
      <el-divider direction="vertical" />
      <span>
        <el-link
          :underline="false"
          href="https://github.com/QiuHaimengjing"
          target="_blank"
          >GitHub<i class="iconfont icon-github"></i
        ></el-link>
      </span>
    </div>
  </div>
</template>

<style scoped lang="less">
.footer {
  display: flex;
  margin-bottom: 20px;
  justify-content: center;
}
</style>

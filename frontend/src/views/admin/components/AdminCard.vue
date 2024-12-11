<script setup lang="ts">
defineProps({
  title: {
    require: true,
    type: String
  }
})
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>{{ title }}</span>
      </div>
    </template>
    <slot></slot>
  </el-card>
</template>

<style scoped lang="less">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
}
.box-card {
  width: 100%;
  min-height: 80vh;
}
</style>

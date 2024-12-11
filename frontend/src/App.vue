<script setup lang="ts">
import SideMenu from '@/views/SideMenu.vue'
import { ref, type VNodeRef } from 'vue'


const sideMenu = ref<VNodeRef | null>(null);

</script>

<template>
  <SideMenu ref="sideMenu"/>
  <router-view @tableListUpdateRequest="sideMenu.updateTablesList();"
    style="position: relative;"
  >
  </router-view>
</template>

<style lang="scss">

@use '@/shared/styles/main' as *;

body{
  background-color: $background-color-base;
  color: $text-color-base;
}
#app {
  position: relative;
  //max-width: 1800px;
  display: flex;
  flex-direction: row;
}
</style>

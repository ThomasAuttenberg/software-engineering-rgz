<script setup lang="ts">

const isShown = defineModel('isShown');

</script>

<template>
  <Teleport to="body" v-if="isShown">
  <div class="ui-modal-window-darkener"/>
  <div class="ui-modal-window">
    <div class="ui-modal-window__header">
      <slot name="header">Default Value</slot>
      <i class="pi pi-times ui-modal-window__header__cross" @click="isShown=false"/>
    </div>
    <div class="ui-modal-window__content">
      <slot name="content"></slot>
    </div>
    <slot name="footer"></slot>
  </div>
  </Teleport>
</template>

<style scoped lang="scss">
.ui-modal-window-darkener{
  position: absolute;
  top: 0;
  height: 100vh;
  width: 100vw;
  z-index: 9999;
  backdrop-filter: blur(10px);
  animation: ui-modal-window-darkener-appearing 1s;
  @keyframes ui-modal-window-darkener-appearing {
    0%{backdrop-filter: blur(0px);}
    100%{backdrop-filter: blur(10px);}
  }
}
.ui-modal-window{
  position: absolute;
  z-index: 10000;
  top: 50vh;
  left: 50vw;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: $box-shadow-over-base;
  &__header{
    padding: 0 20px;
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: $background-color-light;
    &__cross{
      &:hover{
        transform: scale(1.1);
        cursor: pointer;
      }
    }
  }
  &__content{
    width: 100%;
    background: $background-color-base;
  }
}
</style>

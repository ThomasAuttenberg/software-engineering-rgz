<script lang="ts" setup>
const props = defineProps({
  disabled: {type: Boolean, default: false},
  isLoading: {type: Boolean, default: false}
})
const emit = defineEmits(['click']);
const handleClick = ()=>{
  if(!props.disabled) emit('click');
}
</script>
<template>
 <button :class="['ui-pretty-button', {'disabled': isLoading || disabled}]" @click="handleClick">
   <slot v-if="!isLoading"/>
   <i v-else class="pi pi-spin pi-spinner"/>
 </button>
</template>
<style lang="scss" scoped>
.disabled{
  &:hover{
    cursor: not-allowed;
  }
}
.ui-pretty-button{
  width: 100%;
  padding: 20px;
  border-radius: 10px;
  border: none;
  &:not(.disabled):hover{
    cursor: pointer;
    transform: scale(1.01);
  }
  & > pi{
    color: $text-color-base;
  }
}
</style>

<script setup lang="ts">

defineProps({
  placeholder: {type: String, required: false},
  type: {type: String, default: "text"},
})

const model = defineModel();

</script>

<template>
  <input v-model="model" class="ui-pretty-input" :placeholder :type>
</template>

<style scoped lang="scss">
.ui-pretty-input{
  border-radius: 10px;
  padding: 8px;
  height: 40px;
  font-weight: bold;
  &:focus{
    outline: none;
  }
  &::placeholder{
    text-align: center;
  }
}
</style>

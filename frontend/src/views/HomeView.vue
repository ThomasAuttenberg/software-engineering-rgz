<script setup lang="ts">

import { ref, watch } from 'vue'
import PrettyInput from '@/shared/ui/PrettyInput.vue'
import { TableRepository } from '@/shared/repositories/tableRepository'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'

const mode = ref<"default" | "creating">("default");
const isForwardButtonShown = ref<boolean>(false);
const tableNameModel = ref<string>("");
watch(tableNameModel, (newVal) => {
  if(newVal.length > 0){
    isForwardButtonShown.value = true;
  }else{
    isForwardButtonShown.value = false;
  }
})
const emit = defineEmits(['tableListUpdateRequest']);

const error = ref(false);
const onCreatingButtonClick = ()=>{
  TableRepository.createTable(tableNameModel.value).then(() => {
    emit('tableListUpdateRequest');
    mode.value = "default";
  }).catch(()=>{
    error.value = true;
  })
}

const toggleViewMode = (newMode : "default" | "creating") => {
  if(newMode == "creating") {
    mode.value = "creating";
  }else{
    mode.value = "default";
    tableNameModel.value = "";
    isForwardButtonShown.value = false;
  }
}
</script>

<template>
  <div class="home-view__container">
    <NotificationSlider :visible="error">
      <template v-slot:header>Ошибка создания</template>
      Произошла ошибка при создании таблицы. Возможно такая уже есть?
    </NotificationSlider>
    <div :class="['home-view__content', {'home-view__content--creating': mode === 'creating'}]">
      <div class="home-view__content__items">
        <i class="pi pi-caret-left home-view__content__back" @click="toggleViewMode('default');"/>
        <div :class="['home-view__content__hello', {'home-view__content__hello--creating': mode === 'creating'}]">
          <h1>TableViewer</h1>
        </div>
        <div class="home-view__content__creating-form">
          <PrettyInput v-model="tableNameModel" class="home-view__content__creating-form__input" placeholder="Имя таблицы"/>
        </div>
        <i
          :class="[
            'pi pi-caret-right home-view__content__forward',
            {'home-view__content__forward--shown': isForwardButtonShown}
          ]"
           @click="onCreatingButtonClick"
        />
      </div>
      <div class="home-view__content__actions">
        <div class="home-view__content__actions">
          <div class="home-view__content__actions__create" @click="toggleViewMode('creating')">
            <i class="pi pi-plus" style="font-size: 30px"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="scss">
.home-view__container {
  position: relative;
  width: 100%;
  height: 100%;
}
.home-view__content{
  position: absolute;
  top: 50%;
  left: 50%;
  width: 300px;
  justify-content: center;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  gap: 30px;
  &__items{
    justify-content: center;
    display: flex;
    gap: 15px;
    align-items: center;
  }
  &__back,&__forward{
    display: none;
    font-size: 25px;
    &:hover{
      cursor: pointer;
      transform: scale(1.1);
    }
    &--shown{
      position: absolute;
      right: -50px;
      display: flex;
      animation: home-view__forward-button-appearing 0.4s;
      @keyframes home-view__forward-button-appearing {
        0%{opacity: 0;} 100%{opacity: 100;}
      }

    }
  }
  &--creating{
    width: 80%;
    transition: 200ms ease-in-out;
    flex-direction: row;
    align-items: center;
    justify-content: center;
  }
  &--creating &__items{
    position: absolute;
  }
  &--creating  &__actions{
    display: none;
  }
  &--creating  &__back{
    display: flex;
  }
  &--creating  &__creating-form{
    display: flex;
    transition: 200ms ease-in-out;
  }
  &__creating-form{
    display: none;
    &__input{
      border: none;
      background: $text-color-base;
      width: 300px;
      animation: appearing-02b52eb9 0.5s ease-in-out;
      @keyframes appearing-02b52eb9 {
        0%{width:50px}100%{width:300px}
      }
    }
  }
  &__hello{
    text-align: center;
    &--creating{
      text-align: start;

    }
  }
  &__actions{
    display: flex;
    justify-content: center;
    &__create{
      width: 100px;
      height: 100px;
      display: flex;
      justify-content: center;
      align-items: center;
      background: $background-color-additional;
      box-shadow: $box-shadow-over-base;
      &:hover{
        transform: scale(1.05);
        cursor: pointer;
      }
    }
  }
}
</style>

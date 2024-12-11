<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { type TableList, TableRepository } from '@/shared/repositories/tableRepository'
import TablesList from '@/entities/SideMenu/TablesList/TablesList.vue'
import { useTableStore } from '@/shared/stores/currentTable'
import router from '@/router'
import PrettyInput from '@/shared/ui/PrettyInput.vue'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'
import debounce from 'lodash.debounce'

const tableList = ref<TableList>([]);
const goToTable = (table:string): void => {
  console.log("Side menu goto"+table);
  router.push(`/table/${table}`);
}

const deletingError = ref(false);
const deleteTable = (table:string): void => {
  TableRepository.deleteTable(table).then(()=>{
    if(useTableStore().currentTable === table){
      router.push("/");
    }
    updateTablesList();
  }).catch(()=>{
    deletingError.value = true;
  })
}

const updateTablesList = ()=>{
  TableRepository.getTablesList().then(res => {
    if(res.status == 200){
      tableList.value = res.data;
      filterList(inputModel.value);
    }
  })
}
defineExpose({updateTablesList});

const inputModel = ref<string>("");
const tableListFiltered = ref<Array<string>>([]);
const filterList = (val:string) => {
  tableListFiltered.value = tableList.value.filter((item)=>item.toLowerCase().includes(val.toLowerCase()));
}
watch(inputModel, debounce(()=>{
  filterList(inputModel.value);
},200))

onMounted(()=>{
  updateTablesList();
})
</script>

<template>
  <div class="side-menu__container">
    <NotificationSlider :visible="deletingError" :duration="5000">
      Произошла ошибка во время удаления таблицы.
      <template v-slot:header>Ошибка удаления</template>
    </NotificationSlider>
    <div class="side-menu__container__input-wrapper">
      <PrettyInput
        v-model="inputModel"
        class="side-menu__container__input"
        placeholder="Поиск..."
      />
    </div>
    <div class="side-menu__container__header">
      Таблицы
    </div>
    <TablesList :tableList="tableListFiltered" @go-to-table="goToTable" @delete-table="deleteTable"/>
  </div>
</template>

<style scoped lang="scss">
.side-menu__container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  box-shadow: $box-shadow-over-base;
  height: 100vh;
  width: 30vw;
  min-width: 200px;
  max-width: 400px;
  &__header{
    width: 100%;
    height: 50px;
    font-size: 22px;
    font-weight: 900;
    align-items: center;
    display: flex;
    justify-content: center;
    box-shadow: $box-shadow-over-base;
  }
  &__input{
    width: 100%;
    background: $background-color-base;
    color: $text-color-base;
  }
  &__input-wrapper{
    padding: 20px;
    width: 100%;
  }
}
</style>

<script lang="ts" setup>
import TableViewer from '@/entities/TableView/TableViewer.vue'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  type Table,
  TableRepository
} from '@/shared/repositories/tableRepository'
import { useTableStore } from '@/shared/stores/currentTable'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'
import router from '@/router'
import ModalWindow from '@/shared/ui/ModalWindow.vue'
import DropdownList from '@/shared/ui/DropdownList.vue'
import PrettyInput from '@/shared/ui/PrettyInput.vue'
import PrettyButton from '@/shared/ui/PrettyButton.vue'
import {
  type NavigationGuard,
  onBeforeRouteLeave,
  onBeforeRouteUpdate,
  type RouteLocationNormalizedGeneric, useRoute,
} from 'vue-router'

const table = ref<Table | null>(null);
const currentTableName = ref<string | null>(null);
const store = useTableStore();
const route = useRoute();

const loadTable = ()=>{
  currentTableName.value = route.params.name as string;
  store.setCurrentTable(currentTableName.value);
  if(currentTableName.value){
    TableRepository.getTable(currentTableName.value).then((response)=>{
      if(response.status === 200){
        table.value = response.data;
      }
    });
  }
}
const leaveConfirmation = ref(false);
const leaveTo = ref<RouteLocationNormalizedGeneric | null>(null);
const updateGuard : NavigationGuard = (to, from, next) => {
  if (wasTableModified.value) {
    leaveConfirmation.value = true;
    leaveTo.value = to;
    next(false);
    console.log("prevented");
  } else {
    console.log("emitted next");
    console.log(to.path)
    store.clearCurrentTable();
    next();
  }
}
onBeforeRouteUpdate(updateGuard);
onBeforeRouteLeave(updateGuard);
const leaveConfirmationActions = {
  stay: ()=>{leaveConfirmation.value = false; leaveTo.value=null;},
  save: ()=>{saveTable(); watch(wasTableModified, ()=>router.push(leaveTo.value as RouteLocationNormalizedGeneric)) },
  withoutSaving: ()=> {wasTableModified.value=false; router.push(leaveTo.value as RouteLocationNormalizedGeneric);},
}

const saveTableNotification = ref(false);
const saveErrorNotification = ref(false);
const saveTable = ()=>{
  TableRepository.updateTable(currentTableName.value as string).then((response)=>{
    if(response.status === 200){
      saveTableNotification.value = true;
      wasTableModified.value = false;
    }else{
      saveErrorNotification.value = true;
    }
  })
}
const createColumnNotification = ref(false);
const createColumn = ()=>{
  TableRepository.addColumn(
    currentTableName.value as string,
    {type:columnCreatingMW.type, name:columnCreatingMW.name as "Integer | Date | GeoPosition | Time | Number"}
  ).then((response)=>{
    if(response.status === 200){
      createColumnNotification.value = true;
      columnCreatingMW.name = '';
      columnCreatingMW.type = 0;
      toggleSchemeEditingBlock();
      wasTableModified.value=true;
      loadTable();
    }
  })
}
const createRow = ()=>{
  TableRepository.addRow(currentTableName.value as string).then((response)=>{
    if(response.status === 200){
      loadTable();
      wasTableModified.value=true;
    }
  })
}

const deleteColumn = (val:{col:number})=>{
  TableRepository.removeColumn(currentTableName.value as string, val.col).then((response)=>{
    if(response.status === 200){
      wasTableModified.value=true;
      loadTable();
    }
  })
}

const deleteRow = (val:{row: number})=>{
  TableRepository.removeRow(currentTableName.value as string, val.row).then((response)=>{
    if(response.status === 200){
      wasTableModified.value=true;
      loadTable();
    }
  })
}

const wasTableModified = ref(false);

onMounted(()=>{loadTable();});
watch(()=>route.params.name, (newVal)=>{
  if(newVal != currentTableName.value){
    wasTableModified.value = false;
    leaveConfirmation.value = false;
    leaveTo.value = null;
    loadTable();
  }
})
const visible = ref(false);
const onValueUpdate = (value: {row:number, col:number, value:string}) => {
  TableRepository.setValue(currentTableName.value as string, value.row, value.col, value.value).then(
    () => {
      (table.value as Table).data[value.row][value.col] = value.value;
      loadTable();
      wasTableModified.value = true;
    }
  ).catch(() => {
    visible.value = true;
  });
}


const columnTypes = [
  {value: 'Integer', label: 'Целое число'},
  {value: 'Date', label: 'Дата'},
  {value: 'GeoPosition', label: 'Геопозиция'},
  {value: 'Time', label: 'Время'},
  {value: 'Number', label: 'Число'}
];

const columnCreatingMW = reactive({
  isShown: false,
  type: 0 as any,
  name: '' as string,
});
const columnCreatingMWFilled = computed(()=> columnCreatingMW.type !== 0 && columnCreatingMW.name.length > 0);

const isSchemeEditingBlockOpened = ref(false);
const toggleSchemeEditingBlock = ()=>{
  isSchemeEditingBlockOpened.value = !isSchemeEditingBlockOpened.value;
}
</script>
<template>
  <div class="table-view__wrapper">
<!--    Панель управления-->
    <div class="table-view__panel">
      <div class="table-view__panel__edit-go-back">
        <i class="pi pi-backward table-view__panel__edit-go-back" @click="router.push({name: 'home'})"/>
      </div>
      <div class="table-view__panel__edit-save">
        <Transition name="fade">
          <i class="pi pi-save table-view__panel__edit-save" @click="saveTable" v-if="wasTableModified"/>
        </Transition>
      </div>
      <div class="table-view__panel__edit-scheme-block">
        <i :class="['pi pi-plus table-view__panel__edit-scheme-block__add-scheme-member', {'panelOpened':isSchemeEditingBlockOpened}]" @click="toggleSchemeEditingBlock"/>
      </div>
      <div :class="['table-view__panel__edit-scheme-block__add-scheme-member--options',{'visible': isSchemeEditingBlockOpened}]">
        <div v-if="table?.columns ? table.columns.length > 0 : false" class="table-view__panel__edit-scheme-block__add-scheme-member--options__option" @click="createRow">Строка</div>
        <div v-else style="height: 10px"/>
        <div class="table-view__panel__edit-scheme-block__add-scheme-member--options__option" @click="columnCreatingMW.isShown=true">Колонка</div>
      </div>
    </div>
    <ModalWindow v-model:is-shown="leaveConfirmation">
      <template v-slot:header>
        Не потеряйте свои данные !
      </template>
      <template v-slot:content>
        <div class="table-view__leave-confirmation__content">
          Если закрыть таблицу сейчас, изменения могут быть потеряны.
          <div class="table-view__leave-confirmation__content__buttons">
            <PrettyButton class="table-view__leave-confirmation__button" @click="leaveConfirmationActions.save">Сохранить</PrettyButton>
            <PrettyButton class="table-view__leave-confirmation__button" @click="leaveConfirmationActions.withoutSaving">Не сохранять</PrettyButton>
            <PrettyButton class="table-view__leave-confirmation__button" @click="leaveConfirmationActions.stay">Отменить</PrettyButton>
          </div>
        </div>
      </template>
    </ModalWindow>
    <ModalWindow v-model:is-shown="columnCreatingMW.isShown" class="table-view__add-column-window">
      <template v-slot:content>
        <div class="table-view__add-column-window__content">
          <div class="table-view__add-column-window__content__inline">
            <span>Тип данных:</span>
            <DropdownList v-model:model-value="columnCreatingMW.type" :options="columnTypes"></DropdownList>
          </div>
          <div class="table-view__add-column-window__content__inline">
            <span>Название:</span>
            <PrettyInput class="table-view__add-column-window__content__inline__input" v-model="columnCreatingMW.name" />
          </div>
          <div class="table-view__add-column-window__content__button">
            <PrettyButton @click="createColumn" class="table-view__add-column-window__content__button__button" :disabled="!columnCreatingMWFilled">Создать</PrettyButton>
          </div>
        </div>
      </template>
      <template v-slot:header>
        Создание колонки
      </template>
    </ModalWindow>
    <NotificationSlider v-model:visible="createColumnNotification" :duration="2000">
      Колонка успешно добавлена
      <template v-slot:header>
        Изменение схемы
      </template>
    </NotificationSlider>
    <NotificationSlider v-model:visible="saveErrorNotification" :duration="2000">
      Не удалось сохранить таблицу
      <template v-slot:header>
        Загадочные дела
      </template>
    </NotificationSlider>
    <NotificationSlider v-model:visible="saveTableNotification" :duration="2000">
      Таблица успешно сохранена
      <template v-slot:header>
        Сохранено
      </template>
    </NotificationSlider>
    <NotificationSlider v-model:visible="visible" :duration="2000">
      Значение установлено некорректно
      <template v-slot:header>
        Ошибка
      </template>
    </NotificationSlider>
    <div v-if="table?.columns && table.columns.length > 0" class="table-view__table-viewer">
      <TableViewer @delete-column="deleteColumn" @delete-row="deleteRow" v-if="table" :table="table" @updateCell="onValueUpdate"/>
    </div>
    <div v-else class="table-view__table-viewer--empty">
      <span style="display: flex; gap: 15px;">
        <i class="pi pi-file"></i>
        Здесь пока ничего нет
      </span>
      <div class="table-view__table-viewer--empty__guide">
        <div class="table-view__table-viewer--empty__guide__row">
          <i class="pi pi-plus"/>
          <span>Найдите эту кнопку, чтобы создать первую колонку</span>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="scss">
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  width: 0;
}
.table-view{
  width: 100%;
  height: 100%;
  &__leave-confirmation{
    &__content{
      display: flex;
      flex-direction: column;
      gap: 30px;
      padding: 50px;
      &__buttons{
        display: flex;
        flex-direction: column;
        gap: 5px;
      }
    }
    &__button{
      background: $background-color-additional;
      color: $text-color-base;
      font-weight: 600;
      padding: 12px;
      border-radius: 5px;
    }

  }
  &__add-column-window{
    &__content{
      display: flex;
      flex-direction: column;
      gap: 25px;
      padding: 30px;
      width: 500px;
      &__button{
        &__button{
          background: $background-color-additional;
          color: $text-color-base;
          &.disabled{
            background: $background-color-light;
          }
        }
      }
      &__inline{
        font-size: 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        & > span{
          font-size: 18px;
          font-weight: bold;
        }
        &__input{
          background-color: $background-color-light;
          color: $text-color-base;
          cursor: pointer;
          border: none;
          width: 200px;
          border-radius: 5px;
        }

      }
    }
  }
  &__panel{
    font-size: 24px;
    align-items: center;
    padding: 30px;
    display: flex;
    flex-direction: row;
    gap: 30px;
    height: 100px;
    box-shadow: $box-shadow-over-base;
    & i:hover{
      font-size: 27px;
      transform: scale(1.05);
      cursor: pointer;
    }
    &__edit-scheme-block{
      &__add-scheme-member{
        rotate: 0;
        transition: rotate 0.2s;
        &.panelOpened{
          rotate: 45deg;
          &:hover{
            cursor: pointer;
            transform: scale(1.05);
          }
        }
        &--options{
          width: 0;
          height: 0;
          overflow: hidden;
          border-left: solid 2px $background-color-light;
          padding-left: 10px;
          font-size: 16px;
          transition: 0.5s;
          &.visible{
            height: 40px;
            width: 100px;
          }
          &__option{
            &:hover{
              cursor: pointer;
              transform: scale(1.05);
            }
          }
        }
      }
    }
  }
  &__wrapper{
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
  &__table-viewer{
    padding: 50px;
    overflow: auto;
    width: 99%;
    max-height: calc(100vh - 100px);
    &--empty{
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-size: 25px;
      font-weight: bold;
      display: flex;
      align-items: center;
      gap: 20px;
      flex-wrap: wrap;
      &__guide{
        display: flex;
        justify-content: end;
        font-size: 15px;
        font-weight: normal;
        &__row{
          box-shadow: $box-shadow-over-base;
          display: flex;
          gap: 10px;
          align-items: center;
          width: 300px;
          border-left: 2px solid $background-color-additional;
          padding: 10px;
          & > .pi{
            font-weight: 900;
            font-size: 20px;
          }
        }
      }
    }
  }
}
</style>

<script setup lang="ts">
import type { TableList } from '@/shared/repositories/tableRepository'
import type { PropType } from 'vue'
import { useTableStore } from '@/shared/stores/currentTable'

const store = useTableStore();
defineProps({
  tableList: {type: Object as PropType<TableList>, default: null}
})
defineEmits(['goToTable','deleteTable']);

</script>

<template>
<div class="sm-tables-list">
  <TransitionGroup name="list" tag="div">
  <div :class="['sm-tables-list__table',{'selected':table == store.currentTable}]" v-for="(table) in tableList" :key="table">
    <div class="sm-tables-list__table__name" @dblclick="$emit('goToTable', table)">
      {{table}}
    </div>
    <div class="sm-tables-list__table__details">
      <i class="pi pi-ban sm-tables-list__table__details__delete" @click="$emit('deleteTable', table)"/>
      <i class="pi pi-caret-right sm-tables-list__table__details__goto" @click="$emit('goToTable', table)"/>
    </div>
  </div>
  </TransitionGroup>
</div>
</template>

<style scoped lang="scss">
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(60px);
}
.sm-tables-list{
  display: flex;
  padding-top: 20px;
  flex-direction: column;
  width: 100%;
  &__table{
    --background-color: $background-color-light;
    font-size: 20px;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    height: 25px;
    &:hover > &__details{
      display: flex;
      align-items: center;
      gap: 10px;
    }
    &.selected{
      background-color: $background-color-additional;
    }
    &__details{
      display: none;
      &__delete, &__goto{
        &:hover{
          cursor: pointer;
          transform: scale(1.1);
        }
      }

    }
    &__name{
      width: calc(100% - 20px);
      text-wrap: nowrap;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      &:hover{
        cursor: pointer;
      }
    }
  }
}
</style>

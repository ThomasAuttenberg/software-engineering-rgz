import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTableStore = defineStore('table', () => {
  const currentTable = ref<string | null>(null);

  function setCurrentTable(tableName: string) {
    currentTable.value = tableName;
  }

  function clearCurrentTable() {
    currentTable.value = null;
  }

  return {
    currentTable,
    setCurrentTable,
    clearCurrentTable,
  };
});

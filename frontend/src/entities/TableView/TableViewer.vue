<template>
  <div class="table-viewer">
    <div class="table-viewer__container" ref="tableContainer">
      <table class="table-viewer__table">
        <thead>
        <tr>
          <th
            v-for="(column, colIndex) in table.columns"
            :key="colIndex"
            @contextmenu.prevent="showContextMenu($event, -1, colIndex)"
            :style="{ width: columnWidths[colIndex] + 'px' }"
            class="table-viewer__th"
            @dblclick.prevent="autoResizeColumn(colIndex)"
          >
            <div class="table-viewer__th-content">
              {{ column.name }}
              <div class="table-viewer__th-content__type">{{column.typeName}}</div>
              <!-- Ручка для изменения ширины столбца -->
              <div
                class="table-viewer__resize-handle"
                @mousedown.prevent="initResize($event, colIndex, 'column')"
              ></div>
            </div>
          </th>
        </tr>
        </thead>
        <tbody>
        <tr
          v-for="(row, rowIndex) in table.data"
          :key="rowIndex"
          :style="{ height: rowHeights[rowIndex] + 'px' }"
          class="table-viewer__tr"
        >
          <td
            v-for="(cell, colIndex) in row"
            :key="colIndex"
            :style="{ width: columnWidths[colIndex] + 'px' }"
            class="table-viewer__td"
          >
            <div
              v-if="!isEditing(rowIndex, colIndex)"
              class="table-viewer__cell-content"
              @click="startEditing(rowIndex, colIndex)"
              @contextmenu.prevent="showContextMenu($event, rowIndex, colIndex)"
              tabindex="0"
              @keydown.enter.prevent="startEditing(rowIndex, colIndex)"
              @keydown.space.prevent="startEditing(rowIndex, colIndex)"
              role="button"
              aria-label="Редактировать ячейку"
            >
              {{ cell }}
            </div>
            <input
              v-else
              v-model="editableCell.value"
              @blur="stopEditing(rowIndex, colIndex)"
              @keydown.enter.prevent="stopEditing(rowIndex, colIndex)"
              class="table-viewer__input"
              ref="editableInput"
              aria-label="Редактировать значение"
              type="text"
            />
            <!-- Ручка для изменения ширины столбца -->
            <div
              class="table-viewer__resize-handle"
              @mousedown.prevent="initResize($event, colIndex, 'column')"
            ></div>
            <!-- Ручка для изменения высоты строки -->
            <div
              class="table-viewer__resize-handle-row"
              @mousedown.prevent="initResize($event, rowIndex, 'row')"
            ></div>
          </td>
        </tr>
        </tbody>
      </table>
      <!-- Добавляем дополнительное пространство справа и снизу для удобства скроллинга -->
      <div class="table-viewer__spacer"></div>
    </div>

    <!-- Контекстное меню -->
    <div
      v-click-outside="hideContextMenu"
      v-if="contextMenuVisible"
      class="table-viewer__context-menu"
      :style="{ top: contextMenuY + 'px', left: contextMenuX + 'px' }"
    >
      <ul>
        <li @click="deleteColumn">Удалить столбец</li>
        <li v-show="contextMenuTarget.row!=-1" @click="deleteRow">Удалить строку</li>
      </ul>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import type { Table } from '@/shared/repositories/tableRepository'

const props = defineProps<{
  table: Table
}>()

const emit = defineEmits<{
  (e: 'update-cell', payload: { row: number, col: number, value: string }): void
  (e: 'delete-column', payload: { col: number }): void
  (e: 'delete-row', payload: { row: number }): void
}>()

// Рефы и состояния
const tableContainer = ref<HTMLElement | null>(null)

// Инициализация ширин столбцов и высот строк
const columnWidths = ref<number[]>([])
const rowHeights = ref<number[]>([])

// Редактируемая ячейка
const editableCell = reactive({
  row: null as number | null,
  col: null as number | null,
  value: ''
})

// Переменные для ресайза
const resizing = ref(false)
const resizeType = ref<'column' | 'row' | null>(null)
const resizeIndex = ref<number | null>(null)
const startPos = ref<number>(0)
const startSize = ref<number>(0)

// Переменные для контекстного меню
const contextMenuVisible = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const contextMenuTarget = reactive({
  row: null as number | null,
  col: null as number | null
})

// Инициализация размеров и синхронизация с добавлением/удалением столбцов и строк
const initializeSizes = () => {
  // Синхронизация ширин столбцов
  if (props.table.columns.length > columnWidths.value.length) {
    const additional = props.table.columns.length - columnWidths.value.length
    for (let i = 0; i < additional; i++) {
      columnWidths.value.push(150) // Стандартная ширина
    }
  } else if (props.table.columns.length < columnWidths.value.length) {
    columnWidths.value.splice(props.table.columns.length)
  }

  // Синхронизация высот строк
  if (props.table.data.length > rowHeights.value.length) {
    const additional = props.table.data.length - rowHeights.value.length
    for (let i = 0; i < additional; i++) {
      rowHeights.value.push(40) // Стандартная высота
    }
  } else if (props.table.data.length < rowHeights.value.length) {
    rowHeights.value.splice(props.table.data.length)
  }
}

// Автоматическое изменение размера столбца при двойном клике
const autoResizeColumn = (colIndex: number) => {
  let maxWidth = 50 // Минимальная ширина
  tableContainer.value?.querySelectorAll(`tbody tr td:nth-child(${colIndex + 1})`).forEach(td => {
    const content = td.textContent || ''
    const contentWidth = getTextWidth(content, '16px Arial') + 16 // Плюс отступы
    if (contentWidth > maxWidth) {
      maxWidth = contentWidth
    }
  })
  // Также учтём ширину заголовка столбца
  const header = tableContainer.value?.querySelector(`thead tr th:nth-child(${colIndex + 1})`)
  if (header) {
    const headerContent = header.textContent || ''
    const headerWidth = getTextWidth(headerContent, '16px Arial') + 16
    if (headerWidth > maxWidth) {
      maxWidth = headerWidth
    }
  }
  columnWidths.value[colIndex] = Math.min(maxWidth, 500) // Максимальная ширина 500px
}

// Функция для расчёта ширины текста
const getTextWidth = (text: string, font: string): number => {
  const canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement('canvas'))
  const context = canvas.getContext('2d')
  if (context) {
    context.font = font
    return context.measureText(text).width
  }
  return 0
}
getTextWidth.canvas = null as HTMLCanvasElement | null

// Начало изменения размера
const initResize = (
  event: MouseEvent,
  index: number,
  type: 'column' | 'row'
) => {
  resizing.value = true
  resizeType.value = type
  resizeIndex.value = index
  startPos.value = type === 'column' ? event.clientX : event.clientY
  startSize.value =
    type === 'column' ? columnWidths.value[index] : rowHeights.value[index]

  // Добавление слушателей для отслеживания движения мыши и отпускания кнопки
  window.addEventListener('mousemove', handleResize)
  window.addEventListener('mouseup', stopResize)
}

// Обработка изменения размера
const handleResize = (event: MouseEvent) => {
  if (!resizing.value || !resizeType.value || resizeIndex.value === null)
    return

  const delta =
    resizeType.value === 'column'
      ? event.clientX - startPos.value
      : event.clientY - startPos.value

  if (resizeType.value === 'column') {
    const newWidth = Math.max(startSize.value + delta, 50) // Минимальная ширина 50px
    columnWidths.value[resizeIndex.value] = newWidth
  } else if (resizeType.value === 'row') {
    const newHeight = Math.max(startSize.value + delta, 30) // Минимальная высота 30px
    rowHeights.value[resizeIndex.value] = newHeight
  }
}

// Завершение изменения размера
const stopResize = () => {
  resizing.value = false
  resizeType.value = null
  resizeIndex.value = null
  window.removeEventListener('mousemove', handleResize)
  window.removeEventListener('mouseup', stopResize)
}

// Начало редактирования ячейки
const startEditing = (row: number, col: number) => {
  editableCell.row = row
  editableCell.col = col
  editableCell.value = props.table.data[row][col]

  // Фокус на поле ввода после рендера
  nextTick(() => {
    const inputs = tableContainer.value?.querySelectorAll(
      `.table-viewer__input`
    )
    if (inputs && inputs.length > 0) {
      const input = inputs[inputs.length - 1] as HTMLInputElement
      input.focus()
      input.select()
    }
  })
}

// Проверка, редактируется ли ячейка
const isEditing = (row: number, col: number) => {
  return editableCell.row === row && editableCell.col === col
}

// Завершение редактирования ячейки
const stopEditing = (row: number, col: number) => {
  if (isEditing(row, col)) {
    emit('update-cell', {
      row,
      col,
      value: editableCell.value
    })
    editableCell.row = null
    editableCell.col = null
    editableCell.value = ''
  }
}

// Показать контекстное меню
const showContextMenu = (event: MouseEvent, row: number, col: number) => {
  contextMenuVisible.value = true
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  contextMenuTarget.row = row
  contextMenuTarget.col = col
}

// Скрыть контекстное меню
const hideContextMenu = () => {
  if (contextMenuVisible.value) {
    contextMenuVisible.value = false
    contextMenuTarget.row = null
    contextMenuTarget.col = null
  }
}

// Удалить столбец
const deleteColumn = () => {
  if (contextMenuTarget.col !== null) {
    emit('delete-column', { col: contextMenuTarget.col })
    hideContextMenu()
  }
}

// Удалить строку
const deleteRow = () => {
  if (contextMenuTarget.row !== null) {
    emit('delete-row', { row: contextMenuTarget.row })
    hideContextMenu()
  }
}

// Инициализация при монтировании
onMounted(() => {
  initializeSizes()
})

// Очистка при размонтировании
onBeforeUnmount(() => {
  window.removeEventListener('mousemove', handleResize)
  window.removeEventListener('mouseup', stopResize)
})

// Обновление размеров при изменении таблицы
watch(
  () => props.table,
  () => {
    initializeSizes()
  },
  { deep: true }
)
</script>


<style scoped lang="scss">
.table-viewer {
  width: 100%;
  &__th-content{
    &__type{
      padding-top: 10px;
      font-size: 13px;
      color: $background-color-base;
    }
  }
  &__name {
    text-align: center;
    margin-bottom: 20px;
    font-size: 24px;
    color: #333;
  }

  &__container {
    position: relative;
    padding-bottom: 10px; // Добавляет пространство снизу для удобства скроллинга
    padding-right: 10px; // Добавляет пространство справа для удобства скроллинга
  }

  &__table {
    border-collapse: collapse;
    table-layout: fixed; // Позволяет управлять шириной столбцов через CSS
    width: max-content; // Позволяет таблице расширяться при увеличении ширины столбцов
  }

  &__th,
  &__td {
    border: 1px solid #ddd;
    padding: 8px;
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    min-width: 50px;
    height: 40px;
  }

  &__th {
    background-color: $background-color-additional;
    font-weight: bold;
    user-select: none; // Запрещает выделение текста при перетаскивании
    position: relative;
  }

  &__tr:nth-child(even) {
    background-color: $background-color-additional;
    & .table-viewer__input{
      &:focus {
        outline: 2px solid $background-color-base;
      }
    }
  }

  &__cell-content {
    width: 100%;
    height: 100%;
    cursor: pointer;
    padding-right: 10px; // Для места под ручку ресайза
    display: flex;
    align-items: center;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__input {
    width: 100%;
    height: 100%;
    border: none;
    padding: 0 8px;
    margin: 0;
    box-sizing: border-box;
    font-size: 16px;
    outline: none;
    background-color: transparent;
    color: #333;

    &:focus {
      outline: 2px solid $background-color-additional;
      color: $text-color-base;
    }
  }

  &__resize-handle {
    position: absolute;
    top: 0;
    right: 0;
    width: 5px;
    height: 100%;
    cursor: col-resize;
    user-select: none;
    z-index: 1;
    background-color: transparent;

    &:hover {
      background-color: rgba(0, 0, 0, 0.1);
    }
  }

  &__resize-handle-row {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 5px;
    cursor: row-resize;
    user-select: none;
    z-index: 1;
    background-color: transparent;

    &:hover {
      background-color: rgba(0, 0, 0, 0.1);
    }
  }

  &__spacer {
    width: 20px; // Добавляет пространство справа
    height: 20px; // Добавляет пространство снизу
  }

  // Стили для контекстного меню
  &__context-menu {
    position: fixed;
    background-color: $background-color-base;
    border: 1px solid #ccc;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    width: 150px;
    border-radius: 4px;
    overflow: hidden;

    ul {
      list-style: none;
      margin: 0;
      padding: 5px 0;

      li {
        padding: 8px 12px;
        cursor: pointer;
        transition: background-color 0.2s;

        &:hover {
          background-color: $background-color-additional;
        }
      }
    }
  }
}
</style>

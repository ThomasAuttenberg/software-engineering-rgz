<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="notification"
      :class="{ 'notification--enter': entering, 'notification--leave': leaving }"
      @click="handleClose"
    >
      <div class="notification__header">
        <slot name="header">Уведомление</slot>
        <button class="notification__close" @click.stop="handleClose">&times;</button>
      </div>
      <div class="notification__content">
        <slot>Это содержимое уведомления.</slot>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps<{
  visible: boolean
  duration?: number
}>()

const emit = defineEmits(['update:visible'])

const entering = ref(false)
const leaving = ref(false)
const timer = ref<number | null>(null)

const handleClose = () => {
  leaving.value = true
  entering.value = false
  if (timer.value) {
    clearTimeout(timer.value)
  }
  // Длительность анимации закрытия
  setTimeout(() => {
    emit('update:visible', false)
  }, 300) // Соответствует времени анимации
}

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      entering.value = true
      leaving.value = false
      // Автоматическое закрытие через duration
      if (props.duration) {
        timer.value = setTimeout(() => {
          handleClose()
        }, props.duration)
      }
    } else {
      entering.value = false
      leaving.value = false
      if (timer.value) {
        clearTimeout(timer.value)
      }
    }
  }
)

onMounted(() => {
  if (props.visible) {
    entering.value = true
    if (props.duration) {
      timer.value = setTimeout(() => {
        handleClose()
      }, props.duration)
    }
  }
})

onBeforeUnmount(() => {
  if (timer.value) {
    clearTimeout(timer.value)
  }
})
</script>

<style scoped lang="scss">
.notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 300px;
  background-color: #fff;
  border-left: 5px solid $background-color-light;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0;
  transform: translateX(100%);
  transition: all 0.3s ease-in-out;
  z-index: 1000000;
}

.notification--enter {
  opacity: 1;
  transform: translateX(0);
}

.notification--leave {
  opacity: 0;
  transform: translateX(100%);
}

.notification__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: $background-color-additional;
  color: #fff;
  padding: 10px;
}

.notification__close {
  background: none;
  border: none;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
}

.notification__content {
  padding: 15px;
  color: #333;
}
</style>

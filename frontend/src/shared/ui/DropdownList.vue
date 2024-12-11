<template>
  <div class="dropdown" @click="toggleDropdown">
    <div class="dropdown__selected">
      <span>{{ selectedLabel }}</span>
      <svg class="dropdown__arrow" viewBox="0 0 24 24">
        <path d="M7 10l5 5 5-5z" />
      </svg>
    </div>
    <ul v-if="isOpen" class="dropdown__list">
      <li
        v-for="option in options"
        :key="option.value"
        class="dropdown__item"
        :class="{ 'dropdown__item--selected': option.value === modelValue }"
        @click.stop="selectOption(option)"
      >
        {{ option.label }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits, type PropType } from 'vue'

interface Option<T> {
  value: T;
  label: string;
}

const props = defineProps({
  options: {
    type: Array as () => Option<any>[],
    required: true,
  },
  modelValue: {
    type: [String, Number, null] as PropType<string | number | null>,
    default: null,
  },
  placeholder: {
    type: String,
    default: 'Выберите вариант',
  },
});

const emit = defineEmits(['update:modelValue']);

const isOpen = ref(false);

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const selectOption = (option: Option<any>) => {
  emit('update:modelValue', option.value);
  isOpen.value = false;
};

const selectedLabel = computed(() => {
  const selectedOption = props.options.find((o) => o.value === props.modelValue);
  return selectedOption ? selectedOption.label : props.placeholder;
});
</script>

<style lang="scss" scoped>
.dropdown {
  position: relative;
  width: 200px;

  &__selected {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    background-color: $background-color-light;
    border: 1px solid $box-shadow-over-base;
    border-radius: 4px;
    cursor: pointer;

    &:hover {
      background-color: $background-color-additional;
    }
  }

  &__arrow {
    width: 16px;
    height: 16px;
    fill: #333;
    transition: transform 0.2s;

    &[data-open='true'] {
      transform: rotate(180deg);
    }
  }

  &__list {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    margin: 0;
    padding: 0;
    list-style: none;
    background-color: $background-color-light;
    border: 1px solid $box-shadow-over-base;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    z-index: 10;
  }

  &__item {
    padding: 10px;
    cursor: pointer;

    &:hover {
      background-color: $background-color-additional;
    }

    &--selected {
      //background-color: $text-color-light;
      font-weight: bold;
    }
  }
}
</style>

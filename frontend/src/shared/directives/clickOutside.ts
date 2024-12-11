import type { DirectiveBinding } from 'vue';

// Расширяем интерфейс HTMLElement, чтобы TypeScript не выдавал ошибку
declare global {
  interface HTMLElement {
    __clickOutsideHandler__?: (event: MouseEvent) => void;
  }
}

function handleClickOutside(event: MouseEvent, el: HTMLElement, binding: DirectiveBinding) {
  if (!(el === event.target || el.contains(event.target as Node))) {
    binding.value(event); // Вызываем переданную функцию
  }
}

export default {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    el.__clickOutsideHandler__ = (event: MouseEvent) => handleClickOutside(event, el, binding);
    document.addEventListener('click', el.__clickOutsideHandler__!);
  },
  unmounted(el: HTMLElement) {
    if (el.__clickOutsideHandler__) {
      document.removeEventListener('click', el.__clickOutsideHandler__);
      delete el.__clickOutsideHandler__;
    }
  },
};

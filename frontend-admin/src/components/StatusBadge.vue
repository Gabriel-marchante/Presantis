<script setup>
import { computed } from 'vue';

const props = defineProps({
  status: {
    type: String,
    required: true
  }
});

const statusDetails = computed(() => {
  switch (props.status.toUpperCase()) {
    case 'PENDIENTE':
      return { class: 'badge-pending', label: 'Pendiente' };
    case 'APROBADA':
    case 'ACTIVA':
      return { class: 'badge-approved', label: props.status === 'ACTIVA' ? 'Activa' : 'Aprobada' };
    case 'RECHAZADA':
    case 'FINALIZADA':
      return { class: 'badge-rejected', label: props.status === 'FINALIZADA' ? 'Finalizada' : 'Rechazada' };
    default:
      return { class: 'badge-pending', label: props.status };
  }
});
</script>

<template>
  <span class="badge" :class="statusDetails.class">
    {{ statusDetails.label }}
  </span>
</template>

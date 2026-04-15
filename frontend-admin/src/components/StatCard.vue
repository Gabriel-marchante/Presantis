<script setup>
defineProps({
  title: String,
  value: [String, Number],
  icon: Object,
  trend: String,
  trendPositive: Boolean
});
</script>

<template>
  <div class="stat-card glass-panel">
    <div class="stat-header">
      <h3 class="stat-title">{{ title }}</h3>
      <div class="stat-icon" :class="{ 'positive': trendPositive, 'neutral': trend === undefined }">
        <component :is="icon" size="24" />
      </div>
    </div>
    <div class="stat-value">{{ value }}</div>
    <div v-if="trend" class="stat-trend" :class="{ 'text-success': trendPositive, 'text-danger': !trendPositive }">
      {{ trend }}
      <span class="trend-label">vs último mes</span>
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-glow);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-title {
  margin: 0;
  font-size: 1rem;
  color: var(--text-muted);
  font-weight: 500;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.positive {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}
.stat-icon.neutral {
  background: rgba(59, 130, 246, 0.1);
  color: var(--primary);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1;
}

.stat-trend {
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
}

.text-success { color: var(--success); }
.text-danger { color: var(--danger); }

.trend-label {
  color: var(--text-muted);
  font-weight: 400;
}
</style>

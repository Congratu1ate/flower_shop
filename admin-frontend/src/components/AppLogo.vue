<template>
  <div class="logo-wrap">
    <svg class="flower" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <circle cx="32" cy="32" r="6" fill="currentColor" />
      <path d="M32 10c6 0 10 6 10 10s-4 6-10 6-10-2-10-6 4-10 10-10z" fill="currentColor" opacity="0.9"/>
      <path d="M32 48c6 0 10-6 10-10s-4-6-10-6-10 2-10 6 4 10 10 10z" fill="currentColor" opacity="0.9"/>
      <path d="M10 32c0-6 6-10 10-10s6 4 6 10-2 10-6 10-10-4-10-10z" fill="currentColor" opacity="0.9"/>
      <path d="M54 32c0 6-6 10-10 10s-6-4-6-10 2-10 6-10 10 4 10 10z" fill="currentColor" opacity="0.9"/>
    </svg>
  </div>
</template>

<script setup>
</script>

<style scoped>
.logo-wrap { display: flex; justify-content: center; }
.flower { color: var(--brand-pink); width: 48px; height: 48px; }
</style>
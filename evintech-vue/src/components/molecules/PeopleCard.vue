<script setup lang="ts">
import Linkedin from '../icons/Linkedin.vue';
  defineProps<{
    id: Number,
    name: string;
    position: string;
    imageSrc: string;
    linkedinUrl: string;
  }>();
</script>

<template>
  <div class="text-sm font-medium cursor-pointer group focus:bg-epic-gray-50">
    <div class="relative w-full mb-1.5 overflow-hidden rounded-md ar-3/4 bg-white/80">
      <img
        :src="imageSrc"
        class="object-cover w-full h-full transition-opacity duration-200 hover:opacity-90"
      />
    </div>
    <div class="py-6">
      <div class="truncate mb-1">{{ name }}</div>
      <div class="text-white/50">
        {{ position }}</div>
    </div>
    <a :href=linkedinUrl target="_blank"><Linkedin class="social-link"></Linkedin></a>
  </div>
</template>


<style>
.social-link {
  @apply h-9 w-9 text-white/70 hover:text-epic-blue transition duration-200 cursor-pointer;
}
</style>

<script setup lang="ts">
  import { IEvent } from "@/store/events";

  defineProps<{
    eventList: IEvent[];
  }>();
</script>

<template>
  <div class="grid grid-cols-2 gap-4 mt-3 lg:grid-cols-4 lg:mt-5">
    <EventCard
      class="mb-8"
      v-for="{
        eventName,
        category,
        urlImage,
        description,
        imageSrc,
        price
      } of eventList"
      :key="eventName"
      v-bind="{ eventName,category,urlImage,description,imageSrc,price }"
    />
  </div>
  <BrowsePagination />
</template>

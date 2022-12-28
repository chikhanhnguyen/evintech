<script setup lang="ts">
import { filters, filterCheckedCount, ResetFilter, getFilters, setFilters, refreshFilters } from "@/store/filter";

import { Filter, FilterValue } from "@/data/filter";
import { Ref, ref, computed } from "vue";
import { getAllCategories } from "@/store/events";

defineProps<{
  paddingx?: string;
}>();

  const updateFilters = async () => {
    await refreshFilters();
  }
  updateFilters();

</script>

<template>
  <div
    class="grid grid-cols-2 pb-4 text-sm  border-b border-white/10"
    :class="paddingx || 'px-1'"
  >
    <h2>Filtre ({{ filterCheckedCount }})</h2>
    <button
      v-if="filterCheckedCount > 0"
      @click="ResetFilter"
      class="hidden w-full h-full text-xs  uppercase md:block"
    >Réinitialiser</button>
  </div>
  <div class="min-h-60">
    <Collapse
      v-for="filter of filters"
      :key="filter.name"
      open
      :paddingx="paddingx"
      :name="filter.name"
      :lists="filter.lists"
    />
  </div>
</template>

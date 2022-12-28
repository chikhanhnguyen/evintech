<script setup lang="ts">
  import { ref, computed } from "vue";
  import { useRouter } from 'vue-router';
  import { state } from "@/store";

  const props = defineProps<{
    query: string;
  }>();

  const router = useRouter();

  const openMobileSearch = () => {
    state.showMobileSearch = true;
    state.showOverlay = true;
  };

  const submit = () => {
    router.push("/search?query=" + props.query)
  }
</script>

<template>
  <div>
    <button @click="openMobileSearch" class="md:hidden text-white/80 mt-1.5">
      <Search class="w-5 h-5" />
    </button>
    <div class="items-center hidden w-64 gap-3 md:flex text-white/80">
      <div class="absolute pl-3 -translate-y-1/2 top-1/2">
        <Search />
      </div>
      <input
        type="text"
        v-model="query"
        class="py-2.5 pr-3 w-full bg-epic-gray-200 focus:bg-epic-gray-100 text-sm font-medium focus:outline-none pl-10 placeholder-white/50"
        placeholder="Chercher"
        @keyup.enter="submit()"
      />
    </div>
  </div>
  <MobileSearch />
</template>

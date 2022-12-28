<script setup lang="ts">
  import { mobileFilterOption, openFilterOption } from "@/store/filter";
  //
  import { getFilteredCategories } from "@/store/filter";
  import { Ref, ref, toRefs } from "vue";
  import { computed, onMounted, watch } from 'vue';
  import { getEventsByCategories, IEvent } from "@/store/events";
  //
  const sortByList = [
    "Hot",
    "Plus récent",
    "Plus ancien"
  ];
  //
  const eventListRef: Ref<IEvent[]> = ref([]);
  let selectedCategories: string[] = [];
  //

  onMounted(async () => {
    showScroll(window);
    window.addEventListener("resize", (e: any) => {
      showScroll(e.target);
    });
    //
    await applyFilter();
  });

  const showScroll = (e: any) => {
    if (mobileFilterOption.value === true && e.innerWidth < 768) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
      mobileFilterOption.value = false;
    }
  };
  //
  const applyFilter = async () => {
    try {
        selectedCategories = getFilteredCategories();
        const res = await getEventsByCategories(selectedCategories);
        eventListRef.value = res;
    } catch (error) {
        console.log(error);
    }
  }
  //
</script>

<template>
  <div class="grid grid-cols-12 mt-6 mb-16">
    <div class="col-span-12 px-5 md:pl-0 md:pr-7 lg:col-span-9">
      <div class="flex items-center justify-between">
        <Dropdown name="Trier par" :lists="sortByList" />
        <button @click="openFilterOption" class="md:hidden flex items-center gap-2 text-[13px]">
          Filtre
          <FilterIcon />
        </button>
      </div>
      <BrowseResult :eventList="eventListRef" />
    </div>
    <div class="hidden col-span-3 lg:block">
      <BrowseFilterEventList/>
      <br><br>
      <button
        type="submit"
        class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110"
        @click="applyFilter">
        <p>Appliquer</p>
      </button>
    </div>
  </div>
  <MobileFilterOption v-show="mobileFilterOption" />
  
</template>

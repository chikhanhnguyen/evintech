<script setup lang="ts">
  import { onMounted, ref, toRefs } from 'vue';
  import { useRouter } from 'vue-router';
  import { addFavEvent, deleteFavEvent, isFavorited } from "../../store/authentication";

  const isFav = ref(false);

  const props = defineProps<{
    eventName: string;
    category: string;
    urlImage: string;
    description: string;
    imageSrc: string;
    price: number
  }>();

  const { eventName } = toRefs(props);

  const router = useRouter();
  const showEvent = () => {
    router.push(`/event/${props.eventName}`);
  };

  const addFav = async (eventName: string) => {
    await addFavEvent(eventName);
    isFav.value = await checkFav(eventName);
  }

  const deleteFav = async (eventName: string) => {
    await deleteFavEvent(eventName);
    isFav.value = await checkFav(eventName);
  }
  
  const checkFav = async (eventName: string) => {
    return await isFavorited(eventName);
  }

  onMounted(async () => {
    isFav.value = await checkFav(eventName.value);
  })

</script>

<template>
  <div class="text-sm font-medium cursor-pointer group focus:bg-epic-gray-50">
    <div class="relative w-full mb-1.5 overflow-hidden rounded-md ar-3/4 bg-white/80">
      <img
        :src="imageSrc"
        @click="showEvent"
        class="object-cover w-full h-full transition-opacity duration-200 hover:opacity-90"
      />
      <div class="absolute hidden top-3 right-3 group-hover:block">
        <AddIcon v-if="!isFav" class="w-5 h-5" @click="addFav(eventName)"/>
        <CrossIcon v-else class="w-5 h-5" @click="deleteFav(eventName)"/>
      </div>
    </div>
    <div class="py-4">
      <div class="truncate mb-0.5">{{ eventName }}</div>
      <div class="text-white/50">{{ category }}</div>
    </div>
  </div>
</template>

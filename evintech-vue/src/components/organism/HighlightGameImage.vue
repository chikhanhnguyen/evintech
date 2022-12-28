<script setup lang="ts">

import { useRouter } from 'vue-router';
import { deleteFavEvent, addFavEvent, isFavorited } from '../../store/authentication';
import { onMounted } from 'vue';

defineProps<{
  image: string;
  status: string;
  description: string;
  price: string;
}>();

const title = ""

const router = useRouter();

const showEvent = () => {
  router.push(`/event/${title}`);
};

const isFav = false;

  const addFav = async (eventName: string) => {
    await addFavEvent(eventName);
    await checkFav(eventName);
  }

  const deleteFav = async (eventName: string) => {
    await deleteFavEvent(eventName);
    await checkFav(eventName);
  }
  
  const checkFav = async (eventName: string) => {
    await isFavorited(eventName);
  }

  console.log(eventName.value)

  onMounted(() => {
    this.isFav = checkFav(eventName.value);
  })

</script>

<template>
  <div
    class="relative w-full h-full bg-center bg-cover rounded-2xl"
    :style="{ backgroundImage: `url(${image})` }"
  >
    <div
      class="absolute inset-0 bg-gradient-to-t md:bg-gradient-to-r from-black/80 md:from-black/40 to-transparent"
    ></div>
    <div
      class="absolute bottom-8 left-5 md:left-8 text-sm  md:font-bold w-[80%] md:w-[40%]"
    >
      <div class="md:text-shadow mb-1.5 font-bold uppercase text-xxs md:text-xs">{{ status }}</div>
      <p class="font-bold md:text-shadow">{{ description }}</p>
      <!-- <div class="flex flex-col gap-1 mt-6 md:text-shadow md:flex-row">
        <span class="">Starting at</span>
        <span>IDR {{ price }}</span>
      </div> -->
      <div class="items-center hidden h-12 gap-2 mt-3 md:flex">
        <BaseButton @click="showEvent"
          class="px-10 text-xs  text-black bg-white hover:text-black/80"
        >Découvrir</BaseButton>
        <BaseButton class="flex items-center justify-center gap-2 px-5 hover:bg-white/10">
          <i class="fa-solid fa-heart-circle-plus"></i>
          <span class="font-bold text-xxs text-shadow w-30">Ajouter aux favoris</span>
        </BaseButton>
      </div>
    </div>
    <button @click="showEvent"
      class="absolute flex items-center justify-center w-16 transition duration-150 rounded-md top-2 left-2 h-13 hover:bg-white/10 md:hidden group"
    >
      <div
        class="absolute z-30 top-[112%] right-0 text-white/90 text-sm font-medium tracking-wide px-3 py-2 rounded-md bg-epic-gray-200 shadow-lg whitespace-nowrap hidden group-hover:block transition duration-150"
      >Découvrir</div>
      <i class="fa-solid fa-circle-info"></i>
    </button>
    <button
      class="absolute flex items-center justify-center w-16 transition duration-150 rounded-md top-2 right-2 h-13 hover:bg-white/10 md:hidden group"
    >
    <div v-if="!this.isFav">
      <div
        class="absolute z-30 top-[112%] right-0 text-white/90 text-sm font-medium tracking-wide px-3 py-2 rounded-md bg-epic-gray-200 shadow-lg whitespace-nowrap hidden group-hover:block transition duration-150"
      >Ajouter aux favoris</div>
      <i class="fa-solid fa-heart-circle-plus"></i>
    </div>
    <div v-else>
      <div
        class="absolute z-30 top-[112%] right-0 text-white/90 text-sm font-medium tracking-wide px-3 py-2 rounded-md bg-epic-gray-200 shadow-lg whitespace-nowrap hidden group-hover:block transition duration-150"
      >Supprimer des favoris</div>
      <i class="fa-solid fa-heart-circle-minus"></i>
    </div>
    </button>
  </div>
</template>

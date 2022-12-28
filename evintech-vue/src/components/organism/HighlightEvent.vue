<script setup lang="ts">
  import { ref } from "vue";
  import { getTopEvents } from "@/store/events";
  import { HIGHLIGHT_GAME_AUTOPLAY_DELAY } from "@/constant";

  // Swiper
  import { Swiper, SwiperSlide } from "swiper/vue";
  import SwiperCore, { Autoplay, Thumbs, Pagination } from "swiper";
  import "swiper/css";
  import "swiper/css/thumbs";
  import "swiper/css/pagination";

  SwiperCore.use([Autoplay, Thumbs, Pagination]);

  const thumbsSwiper = ref(null);
  const autoplayDelay = ref(HIGHLIGHT_GAME_AUTOPLAY_DELAY);
  const activeIndex = ref(0);

  function setThumbsSwiper(swiper: any) {
    thumbsSwiper.value = swiper;
  }

  const handleSlideChange = (s: any) => {
    activeIndex.value = s.activeIndex;
  };

  const highlightEvents = await getTopEvents(3);
</script>

<template>
  <div class="flex flex-col md:flex-row h-[32rem] sm:h-[28rem] w-full">
    <Swiper
      v-bind="{
        spaceBetween: 10,
        navigation: true,
        thumbs: { swiper: thumbsSwiper },
        autoplay: {
          delay: autoplayDelay,
          disableOnInteraction: false,
        },
        breakpoints: {
          768: { pagination: false },
        },
        pagination: { clickable: true },
      }"
      @slideChange="handleSlideChange"
      class="w-full md:w-[80%] h-full pl-5 md:pl-0 pr-5 md:pr-0 pb-8 md:pb-0 cursor-pointer"
    >
      <SwiperSlide
        v-for="{ imageSrc, eventName, category, description } of highlightEvents"
        :key="eventName"
      >
        <HighlightEventImage v-bind="{ imageSrc, eventName, category, description }" />
      </SwiperSlide>
    </Swiper>
    <Swiper
      v-bind="{
        direction: `vertical`,
        spaceBetween: 10,
        slidesPerView: 6,
        autoplay: {
          delay: 7000,
        },
        watchSlidesProgress: true,
      }"
      @swiper="setThumbsSwiper"
      class="hidden h-full pl-4 cursor-pointer md:block discover-swiper w-max"
    >
      <SwiperSlide v-for="({ imageSrc, eventName }, index) of highlightEvents" :key="eventName">
        <HighlightEventList
          :activeIndex="activeIndex"
          :isActive="index === activeIndex"
          v-bind="{ imageSrc, eventName }"
        />
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<style>
.swiper-pagination {
  @apply flex md:hidden items-center justify-center gap-1;
}

.swiper-pagination .swiper-pagination-bullet.swiper-pagination-bullet-active {
  @apply bg-white;
}
.swiper-pagination .swiper-pagination-bullet {
  @apply bg-white/50 w-1.5 h-1.5;
}
</style>


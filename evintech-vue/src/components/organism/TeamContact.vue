<script setup lang="ts">
import { Swiper, SwiperSlide } from "swiper/vue";
import SwiperCore, { Navigation } from "swiper";
import "swiper/css";
import { Ref, ref } from "vue";

interface IPeople {
  id: Number,
  name: string;
  position: string;
  imageSrc: string;
  linkedinUrl: string;
}

SwiperCore.use([Navigation]);
//
const peopleList: Ref<IPeople[]> = ref([])
const getPeoples = () => {
  peopleList.value=[
        { 
          id: 1,
          name: "Darel Martin DOUPAMBY MATOKA MIKIELA",
          position: "CEO",
          imageSrc: "https://media-exp1.licdn.com/dms/image/C4D03AQGdyoYoEhpjMg/profile-displayphoto-shrink_400_400/0/1640109144550?e=1662595200&v=beta&t=2u4ExRAv0k80KZEu6OIZX-2ifPmT9mpkDtBweQvRcRg",
          linkedinUrl: "https://www.linkedin.com/in/darel-martin-doupamby-matoka-mikiela-69a938187/"
        },
        { 
          id: 2,
          name: "Mehdi Blal",
          position: "Developpeur API et Gestion de la base de donnée",
          imageSrc: "../src/assets/images/icons/mehdi.jpg",
          linkedinUrl: "https://www.linkedin.com/in/mehdi-blal-404a871a1/"
        },
        { 
          id: 3,
          name: "NGUYEN Khanh Chi",
          position: "Lead Designer",
          imageSrc: "https://media-exp1.licdn.com/dms/image/C4D03AQEbWzYFKLRYSg/profile-displayphoto-shrink_400_400/0/1643059067656?e=1662595200&v=beta&t=kvQdTVShZ-Ss5Man0LlymjxBTrZGrVZJaQ_uY56fHIE",
          linkedinUrl: "https://www.linkedin.com/in/khanh-chi-nguyen-9841b4230/"
        },
        { 
          id: 4,
          name: "Yannick Han",
          position: "COO",
          imageSrc: "https://media-exp1.licdn.com/dms/image/C4E03AQHQ1AeBc4IjJg/profile-displayphoto-shrink_400_400/0/1643284213907?e=1662595200&v=beta&t=26GkZpM2PfuAS4vjyGnph3kIn3TJcWrXeKpKq-4Fo5c",
          linkedinUrl: "https://www.linkedin.com/in/yannick-han-aaa296219/"
        },
        { 
          id: 5,
          name: "Marcel Ngassa Kondjio",
          position: "CSO",
          imageSrc: "https://media-exp1.licdn.com/dms/image/C4E03AQHwxoYpkae_Xg/profile-displayphoto-shrink_400_400/0/1621593213404?e=1662595200&v=beta&t=muYxTrHkDF_6ucTmg1IqhgjSbY-Rh9xqslwQDJg9qDA",
          linkedinUrl: "https://www.linkedin.com/in/marcel-ngassa-kondjio-1b1824212/"
        },
    ]
}
getPeoples();
//

const swiperOptions = {
  navigation: {
    prevEl: ".gameonsale-prev",
    nextEl: ".gameonsale-next",
  },
  spaceBetween: 20,
  breakpoints: {
    0: {
      slidesPerGroup: 1,
      slidesPerView: 1,
    },
    480: {
      slidesPerGroup: 1,
      slidesPerView: 2,
    },
    768: {
      slidesPerGroup: 2,
      slidesPerView: 3,
    },
    1024: {
      slidesPerGroup: 3,
      slidesPerView: 4,
    },
    1280: {
      slidesPerGroup: 5,
      slidesPerView: 6,
    },
  },
}
</script>

<template>
  <div class="flex justify-between px-5 mt-12 mb-5 md:px-0">
    <h1 class="item-center">NOTRE EQUIPE</h1>
  </div>
  <div>
    <Swiper v-bind="swiperOptions" class="w-full h-full pl-5 md:pl-3 pr-36 md:pr-0">
      <SwiperSlide
            v-for="{
                id,
                name,
                position,
                imageSrc,
                linkedinUrl
            } of peopleList"
            :key="id"
        >
          <PeopleCard v-bind="{ id, name, position, imageSrc, linkedinUrl }" />
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<style scoped>
.button-navigate {
  @apply h-7 w-7 grid place-items-center rounded-full bg-epic-gray-200 cursor-pointer;
}
</style>

<script setup lang="ts">
import { Tab } from "@headlessui/vue";
import { state, resetState } from "@/store";
import { onMounted, toRefs, ref } from "vue";
import {  onAuthStateChanged, getAuth, sendEmailVerification } from "firebase/auth";

const { showMobileTabMenu, showOverlay } = toRefs(state);

const openMobileTabMenu = () => {
  state.showMobileTabMenu = !state.showMobileTabMenu;
  state.showOverlay = !state.showOverlay;
};

const { tabMenuIndex } = toRefs(state);
</script>

<template>
  <div class="flex w-10 md:hidden"></div>
  <button
    @click="openMobileTabMenu"
    class="relative z-40 flex flex-col items-center justify-center flex-grow mx-2 text-white border border-transparent md:hidden focus:border-white/10"
  >
    <div class="text-[8.5px] font-bold uppercase opacity-60 tracking-wide">Evin'tech</div>
    <div class="flex items-center text-[13px] tracking-wide font-bold -mt-1">
      {{ tabMenuIndex === 0 ? "À la une" : "Tous les évènements" }}
      <div class="transition-transform duration-200" :class="showMobileTabMenu && '-rotate-180'">
        <ArrowDown class="w-4 h-4" />
      </div>
    </div>
  </button>
  <div
    :class="showMobileTabMenu && showOverlay ? 'flex' : 'hidden'"
    class="absolute top-20 left-0 md:static z-30 md:z-auto md:flex items-center flex-col md:flex-row gap-10 md:gap-5 text-white text-sm py-8 bg-epic-black md:bg-transparent w-full h-[max-content]"
  >
    <Tab v-slot="{ selected }" as="template">
      <button
        @click="resetState"
        class=" hover:text-white"
        :class="!selected && 'text-white/40'"
      >Tous les évènements</button>
    </Tab>
  </div>
</template>

<style scoped>
.col-sm-12 {
    position: relative;
    min-height: 1px;
    padding-right: 15px;
    padding-left: 15px;
}
.alert {
    position: relative;
    padding: 0.75rem 1.25rem;
    margin-bottom: 1rem;
    border: 1px solid transparent;
    border-radius: 0.25rem;
}
.alert-dismissible .close {
    position: absolute;
    top: 0;
    right: 0;
    padding: 0.75rem 1.25rem;
    color: inherit;
}
.alert>.start-icon {
    margin-right: 0;
    min-width: 20px;
    text-align: center;
}

.alert>.start-icon {
    margin-right: 5px;
}

.alert-simple.alert-success
{
  border: 1px solid rgba(36, 241, 6, 0.46);
    background-color: rgba(7, 149, 66, 0.12156862745098039);
    box-shadow: 0px 0px 2px #259c08;
    color: #0ad406;
  text-shadow: 2px 1px #00040a;
  transition:0.5s;
  cursor:pointer;
}
.alert-simple.alert-info
{
  border: 1px solid rgba(6, 44, 241, 0.46);
    background-color: rgba(7, 73, 149, 0.12156862745098039);
    box-shadow: 0px 0px 2px #0396ff;
    color: #0396ff;
  text-shadow: 2px 1px #00040a;
  transition:0.5s;
  cursor:pointer;
}

.alert-simple.alert-warning
{
      border: 1px solid rgba(241, 142, 6, 0.81);
    background-color: rgba(220, 128, 1, 0.16);
    box-shadow: 0px 0px 2px #ffb103;
    color: #ffb103;
    text-shadow: 2px 1px #00040a;
  transition:0.5s;
  cursor:pointer;
}

.alert-warning:hover{
  background-color: rgba(220, 128, 1, 0.33);
  transition:0.5s;
}

.warning
{
      font-size: 18px;
    color: #ffb40b;
    text-shadow: none;
}

.alert-simple.alert-danger
{
  border: 1px solid rgba(241, 6, 6, 0.81);
    background-color: rgba(220, 17, 1, 0.16);
    box-shadow: 0px 0px 2px #ff0303;
    color: #ff0303;
    text-shadow: 2px 1px #00040a;
  transition:0.5s;
  cursor:pointer;
}

.alert-simple.alert-primary
{
  border: 1px solid rgba(6, 241, 226, 0.81);
    background-color: rgba(1, 204, 220, 0.16);
    box-shadow: 0px 0px 2px #03fff5;
    color: #03d0ff;
    text-shadow: 2px 1px #00040a;
  transition:0.5s;
  cursor:pointer;
}

.alert:before {
    content: '';
    position: absolute;
    width: 0;
    height: calc(100% - 44px);
    border-left: 1px solid;
    border-right: 2px solid;
    border-bottom-right-radius: 3px;
    border-top-right-radius: 3px;
    left: 0;
    top: 50%;
    transform: translate(0,-50%);
      height: 20px;
}

.fa-times
{
-webkit-animation: blink-1 2s infinite both;
	        animation: blink-1 2s infinite both;
}

@-webkit-keyframes blink-1 {
  0%,
  50%,
  100% {
    opacity: 1;
  }
  25%,
  75% {
    opacity: 0;
  }
}
@keyframes blink-1 {
  0%,
  50%,
  100% {
    opacity: 1;
  }
  25%,
  75% {
    opacity: 0;
  }
}
</style>


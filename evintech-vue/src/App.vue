<template>
    <router-view></router-view>
</template>
<script setup lang="ts">
import { onAuthStateChanged } from "firebase/auth";

import { getAuth } from "firebase/auth";

import { onMounted, ref } from "vue";

const isLoggedIn = ref(false);
const auth = getAuth();
let user = auth.currentUser;
onMounted (() => {
    onAuthStateChanged(auth, (user) => {
        if(user) {
            isLoggedIn.value = true;
        } else {
            isLoggedIn.value = false;
        }
    });
});
</script>
<style>
    ::-webkit-scrollbar {
        display: none;
    }
</style>

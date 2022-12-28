<template>
    <MainLayout>
        <main class="py-13 md:ml-6">
            <div class="flex md:container items-center justify-center w-full py-10 text-sm select-none">
                <div class="flex flex-col md:flex-row h-[32rem] sm:h-[28rem] w-full">
                    <div class="item-center relative w-full h-full ">
                        <label style="font-size: 10vh;"><strong>Paiment réussi !</strong></label>
                    </div>
                </div> 
            </div>
            <div class="flex md:container py-2">
                <router-link to="/" class="item-center w-full h-12 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                    Revenir à la page d'accueil
                </router-link>
            </div>
        </main>
    </MainLayout>
</template>
<script setup lang="ts">
    import { useRouter } from 'vue-router';

</script>

<style scoped>
    .item-center {
    display: grid;
    grid-auto-flow: column;
    gap: 4px;
    align-items: center;
    justify-items: center;
    }
</style>
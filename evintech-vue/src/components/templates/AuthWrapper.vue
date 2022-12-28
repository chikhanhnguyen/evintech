<script setup lang="ts">
defineProps<{
  title: string;
  isSignUp?: boolean;
}>();
</script>

<template>
  <div class="flex items-center justify-center w-full py-10 text-sm select-none">
    <div
      class="w-full sm:w-[470px] rounded bg-epic-gray-200 h-[90%] px-5 py-14 sm:p-14 grid place-items-center text-white"
    >
      <EpicGames class="w-12 h-12" />
      <h1 class="w-full mb-5 text-xs font-bold uppercase mt-7">{{ title }}</h1>
      <slot />
      <div class="grid place-items-center text-[13px] gap-2">
        <!-- <a href="#" class="mb-4 underline">Privacy Policy</a> -->
        <div>
          <span class="mr-1 text-white/60">
            {{
              isSignUp
                ? "Vous avez un compte?"
                : "Vous n'avez pas de compte?"
            }}
          </span>
          <router-link v-if="isSignUp" to="/signin" class="underline">Se connecter</router-link>
          <router-link v-else to="/signup" class="underline">Créer un compte</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

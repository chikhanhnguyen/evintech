<script setup lang="ts">
interface Props {
  title: string;
  active?: boolean;
  to?: string;
}

withDefaults(defineProps<Props>(), {
  to: "#",
});
</script>

<template>
  <li class="relative h-full px-8 md:px-3.5 group overflow-hidden py-4"> 
    <router-link 
      :to="to"
      class="flex un items-center h-full tracking-widest group-hover:opacity-100"
      id="navItems"
    >{{ title }}</router-link>
  </li>
</template>

<style scoped>
  
  #navItems:hover::after {
    width: 100%;
  }
  
  #navItems::after {
    
    content: '';
    width: 0px;
    height: 3px;
    display: block;
    background: rgba(0, 125, 252, var(--tw-bg-opacity));
    transition: 300ms;
  }

  #navItems {
    display: inline-block;  
  }

  .router-link-exact-active {
    font-weight: 700;
    color: rgba(0, 125, 252, var(--tw-bg-opacity));
  }
  
</style>

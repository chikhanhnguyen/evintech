import { createRouter, createWebHistory } from "vue-router";
import { onAuthStateChanged, getAuth } from "firebase/auth";
import { state } from "@/store";
import Home from "@/pages/home.vue";
import Admin from "@/pages/admin.vue";
import Favoris from "@/pages/favoris.vue";
import Contact from "@/pages/contact.vue";
import SignIn from "@/pages/signin.vue";
import SignUp from "@/pages/signup.vue";
import CreateEvent from "@/pages/createevent.vue";
import UserProfile from "@/pages/userprofile.vue";
import Event from "@/pages/event.vue";
import Search from "@/pages/search.vue";
import Payment from "@/pages/paymentsucess.vue";
import PrivateChat from "@/pages/privateChat.vue";

const routes = [
  { path: "/signin", component: SignIn },
  { path: "/signup", component: SignUp },
  { path: "/", component: Home, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/createevent", component: CreateEvent, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/profile", component: UserProfile, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/event/:eventName", component: Event, 
    meta: {
      requiresAuth: true,
    } 
  },
  { 
    path: "/search", component: Search, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/contact", component: Contact, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/admin", component: Admin, 
    meta: {
      requiresAuth: true,
    } 
  },
    { path: "/favoris", component: Favoris, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path: "/successPay", component: Payment, 
    meta: {
      requiresAuth: true,
    } 
  },
  { path:'/messagerie', component: PrivateChat,
    meta: {
      requiresAuth: true,
    }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 };
  },
});

router.beforeEach(async (to, from, next) => {
  if(to.matched.some((record) => record.meta.requiresAuth)) { 
    if (await getCurrentUser()) {
      next();
    } else {
      next("/signin");
    }
  } else {
    next();
  }
},)

const getCurrentUser = () => {
  return new Promise((resolve, reject) => {
    const removeListener = onAuthStateChanged(
      getAuth(),
      (user) => {
        removeListener();
        resolve(user);
      },
      reject
    )
  })
}

export default router;

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import VueChatScroll from 'vue-chat-scroll'

import { initializeApp } from "firebase/app";

import router from "@/router";
import App from "./App.vue";
import "./assets/css/tailwind.css";

const app = createApp(App);

/*
const firebaseConfig = {
  apiKey: "AIzaSyBolGfG8nyp35XfraLA-v5q1o_sK2ayvww",
  authDomain: "evin-tech.firebaseapp.com",
  projectId: "evin-tech",
  storageBucket: "evin-tech.appspot.com",
  messagingSenderId: "1012544905577",
  appId: "1:1012544905577:web:708181e5d3d1ce62695df8",
  measurementId: "G-327LY6X5EH"
};*/

const firebaseConfig = {
  apiKey: "AIzaSyCCaHJeKZ7ojyVCnK8yMWcEa8BD77I9Ros",
  authDomain: "evintechv2.firebaseapp.com",
  databaseURL: "https://evintechv2-default-rtdb.europe-west1.firebasedatabase.app/",
  projectId: "evintechv2",
  storageBucket: "evintechv2.appspot.com",
  messagingSenderId: "262953430867",
  appId: "1:262953430867:web:dbcac622165a1ef539bba6"
};

initializeApp(firebaseConfig);

app.use(createPinia())
app.use(router)

app.mount('#app')
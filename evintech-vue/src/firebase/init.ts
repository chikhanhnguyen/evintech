import { initializeApp } from "firebase/app";
import { getFirestore } from "@firebase/firestore";
//import { getAuth} from 'firebase/auth';

const firebaseConfig = {
    apiKey: "AIzaSyBolGfG8nyp35XfraLA-v5q1o_sK2ayvww",
    authDomain: "evin-tech.firebaseapp.com",
    projectId: "evin-tech",
    storageBucket: "evin-tech.appspot.com",
    messagingSenderId: "1012544905577",
    appId: "1:1012544905577:web:708181e5d3d1ce62695df8",
    measurementId: "G-327LY6X5EH"
  };

  
  
  // Initialize Firebase
  const app = initializeApp(firebaseConfig);
  
  const fb = getFirestore(app);
  
  
  export default fb;


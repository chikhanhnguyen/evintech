<script setup lang="ts">
  import { ref, computed } from "vue";
  import { getAuth, signInWithEmailAndPassword, GoogleAuthProvider, signInWithPopup } from 'firebase/auth';
  import { useRouter } from 'vue-router';
  import useRefValidation from "@/hooks/useRefValidation";
  import { logInUser, LogWithGoogle } from "@/store/authentication";

  const [ email, setEmail, emailError ] = useRefValidation();
  const [ password, setPassword, passwordError ] = useRefValidation();

  const errMsg = ref();

  const rememberMe = ref(false);

  const validInput = computed(() => !!(email.value && password.value));

  const router = useRouter();

  const isSendingConnection = ref(false);

  const submitUser = async () => {
    isSendingConnection.value = true;
    await logInUser({username: email.value, password: password.value});
    isSendingConnection.value = false;
    router.push('/');
  }

  const auth = getAuth();

  const submitGoogleUser = async (user) => {
    isSendingConnection.value = true;
    await LogWithGoogle(user);
    isSendingConnection.value = false;
    router.push('/');
  }

  // Connexion avec Google
  const signInWithGoogle = async () => {
      const provider = new GoogleAuthProvider();
      await signInWithPopup(getAuth(), provider)
      .then(async (result) => {
        // let token = result.user.getIdToken().then((response) => { return response});
        await submitGoogleUser(result.user);
      })
      .catch((error) => {
        alert(error.message);
      });
  };

  // Connexion avec email et mdp
  const signIn = () => {
    signInWithEmailAndPassword(auth, email.value, password.value)
    .then((data) => {
      submitUser()
    })
    .catch(error => {
      console.log(error.code)
      switch (error.code) {
          case "auth/invalid-email":
              errMsg.value = "L'email est invalide";
              break;
          case "auth/user-not-found":
              errMsg.value = "L'email saisi n'est associé à aucun compte"; 
              break;
          case "auth/wrong-password":
              errMsg.value = "Mot de passe incorrecte";
              break;
          default:
              errMsg.value = "L'email ou le mot de passe sont incorrectes";
              break;
      }
    });
  }

</script>

<template>
  <AuthWrapper title="Se connecter avec le compte d'EVIN'TECH">
    <form @submit.prevent action="#" class="w-full mb-8">
      <div class="space-y-6">
        <FloatingInput
          name="email"
          title="Adresse e-mail"
          :input="email"
          :error="emailError"
          @value="(val: string) => setEmail(val)"
        />
        <FloatingInput
          name="password"
          title="Mot de passe"
          isPassword
          showAndHidden
          autocomplete="true"
          :input="password"
          :error="passwordError"
          @value="(val: string) => setPassword(val)"
        />
        <p class="text-red-400" v-if="errMsg">{{ errMsg }}</p>
      </div>
      <div class="flex items-center justify-between text-[13px] mt-6 mb-10">
        <Checkbox :value="rememberMe">Se souvenir de moi</Checkbox>
        <a href="#" class="underline">Mot de passe oublié ?</a>
      </div>
      <div class="space-y-6">
        <button
          type="submit"
          :disabled="!validInput || isSendingConnection"
          class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110"
          @click="signIn"
        > 
          <p v-if="!isSendingConnection">Se connecter</p>
          <p v-else>Connexion en cours ...</p>
        </button>
        <button
          v-if="!isSendingConnection"
          type="submit"
          class="w-full h-14 bg-epic-blue uppercase font-bold hover:brightness-110 login-with-google-btn" 
          @click="signInWithGoogle"
          >Sign In With Google</button>
      </div>
    </form>
  </AuthWrapper>
</template>
<style scoped>
  .login-with-google-btn {
      
    padding: 12px 16px 12px 42px;
    border-radius: 3px;

    font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Oxygen,Ubuntu,Cantarell,"Fira Sans","Droid Sans","Helvetica Neue",sans-serif;
    
    background-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTgiIGhlaWdodD0iMTgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNMTcuNiA5LjJsLS4xLTEuOEg5djMuNGg0LjhDMTMuNiAxMiAxMyAxMyAxMiAxMy42djIuMmgzYTguOCA4LjggMCAwIDAgMi42LTYuNnoiIGZpbGw9IiM0Mjg1RjQiIGZpbGwtcnVsZT0ibm9uemVybyIvPjxwYXRoIGQ9Ik05IDE4YzIuNCAwIDQuNS0uOCA2LTIuMmwtMy0yLjJhNS40IDUuNCAwIDAgMS04LTIuOUgxVjEzYTkgOSAwIDAgMCA4IDV6IiBmaWxsPSIjMzRBODUzIiBmaWxsLXJ1bGU9Im5vbnplcm8iLz48cGF0aCBkPSJNNCAxMC43YTUuNCA1LjQgMCAwIDEgMC0zLjRWNUgxYTkgOSAwIDAgMCAwIDhsMy0yLjN6IiBmaWxsPSIjRkJCQzA1IiBmaWxsLXJ1bGU9Im5vbnplcm8iLz48cGF0aCBkPSJNOSAzLjZjMS4zIDAgMi41LjQgMy40IDEuM0wxNSAyLjNBOSA5IDAgMCAwIDEgNWwzIDIuNGE1LjQgNS40IDAgMCAxIDUtMy43eiIgZmlsbD0iI0VBNDMzNSIgZmlsbC1ydWxlPSJub256ZXJvIi8+PHBhdGggZD0iTTAgMGgxOHYxOEgweiIvPjwvZz48L3N2Zz4=);
    background-color: rgba(0, 125, 252, var(--tw-bg-opacity));
    background-repeat: no-repeat;
    background-position: 25% 50%;
  }
</style>

<script setup lang="ts">
  import { ref, computed } from "vue";
  import useRefValidation from "@/hooks/useRefValidation";
  import { getAuth, createUserWithEmailAndPassword, sendEmailVerification } from 'firebase/auth';
  import { useRouter } from 'vue-router';
  import { register } from "@/store/authentication";

  const [ userFirstName, setUserFirstName, userFirstNameError ] = useRefValidation()
  const [ userName, setUserName, userNameError ] = useRefValidation()
  const [ email, setEmail, emailError ] = useRefValidation()
  const [ password, setPassword, passwordError ] = useRefValidation()

  const validInput = computed(() => 
    !!(userFirstName && userName && email && password)
  );

  const errEmail = ref();
  const errMsg = ref();
  const router = useRouter();
  const auth = getAuth();

  const registerUser = async (uid: string) => {
    await register(
      {
        userId: uid,
        userFirstName: userFirstName.value, 
        userName: userName.value, 
        email: email.value, 
        password: password.value
      },
    );
    router.push('/signin');
  }

  const signUp = () => {
        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email.value)) {
            errEmail.value = "";
            createUserWithEmailAndPassword(auth, email.value, password.value)
            .then((data) => {
              registerUser(auth.currentUser.uid);
              sendEmailVerification(auth.currentUser)
                .then(() => {
              });
              router.push('/signin');
            })
            .catch((error) => {
              switch (error.code) {
                  case "auth/email-already-in-use":
                      errMsg.value = "L'email entré est déjà associé à un compte";
                      break;
                  default:
                      errMsg.value = "L'email ou le mot de passe est incorrecte";
                      break;
              };
            });
          } else {
            errEmail.value = "L'email entrée n'est pas valide !";
          }
    };

</script>

<template>
  <AuthWrapper title="Créer votre compte ENVIN'TECH" isSignUp>
    <form @submit.prevent action="#" class="w-full mb-8">
      <div class="space-y-6">
        <div class="grid grid-cols-2 gap-4">
          <FloatingInput
            name="firstName"
            title="Prénom"
            :input="userFirstName"
            :error="userFirstNameError"
            @value="(val:string) => setUserFirstName(val)"
            isRequired
          />
          <FloatingInput
            name="lastName"
            title="Nom"
            :input="userName"
            :error="userNameError"
            @value="(val:string) => setUserName(val)"
            isRequired
          />
        </div>
        <FloatingInput
          name="email"
          title="Adresse e-mail"
          :input="email"
          :error="emailError"
          @value="(val:string) => setEmail(val)"
          isRequired
        />
        <p class="text-red-400" v-if="errEmail">{{ errEmail }}</p>
        <FloatingInput
          name="password"
          title="Mot de passe"
          isPassword
          showAndHidden
          autocomplete="false"
          isRequired
          :input="password"
          :error="passwordError"
          @value="(val:string) => setPassword(val)"
        />
        <p class="text-red-400" v-if="errMsg">{{ errMsg }}</p>
      </div>
      <br>
      <button
        type="submit"
        :disabled="!validInput"
        class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110"
        @click="signUp"
      >S'inscrire</button>
    </form>
  </AuthWrapper>
</template>

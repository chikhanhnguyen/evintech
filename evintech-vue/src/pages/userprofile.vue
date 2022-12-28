<template>
  <MainLayout>
    <div class="page-holder bg-gray-100">
      <div class="container-fluid px-lg-4 px-xl-5 contentDiv">
        <!-- Page Header-->
        <div class="page-header mb-4">
          <h1 class="page-heading">Profil</h1>
        </div>
        <section>
          <div class="row">
            <div class="col-lg-4">
              <div class="card card-profile mb-4">
                <div class="card-header" style="background-image: url(https://therichpost.com/wp-content/uploads/2021/05/bootstrap5-carousel-slider-img1.jpg);"> </div>
                <div class="card-body text-center">
                  <link href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" rel="stylesheet" />
                  <div class="userprofile card-profile-img">
                    <img :src="profilePictureSrc">
                    <label>
                      <i class="fa fa-edit"></i>
                      <input type="file" @change="onPhotoProfileSelected" accept="image/jpeg" />
                    </label>
                  </div>

                  <h3 class="mb-3">{{userFirstName}} {{userName}}</h3>
                  <p class="mb-4">{{role}}</p>
                  <!-- <button class="btn btn-outline-dark btn-sm"><span class="fab fa-twitter"></span>Suivre</button> -->
                </div>
              </div>
            </div>
            <div class="col-lg-8">
              <form class="card mb-4">
                <div class="card-header">
                  <h4 class="card-heading">Modifier Profil</h4>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col-sm-6 col-md-4">
                      <div class="mb-4">
                        <label class="form-label">Nom</label>
                        <input v-model="userName" class="form-control" type="text" placeholder="Username">
                      </div>
                    </div>
                    <div class="col-sm-6 col-md-6">
                      <div class="mb-4">
                        <label class="form-label">Prénom</label>
                        <input v-model="userFirstName" class="form-control" type="text" placeholder="Prénom">
                      </div>
                    </div>
                    <div class="col-sm-6 col-md-6">
                      <div class="mb-4">
                        <label class="form-label">Adresse e-mail</label>
                        <input v-model="email" class="form-control" type="email" placeholder="Email" disabled>
                      </div>
                    </div>
                    <div class="mb-3"> 
                      <label class="form-label">Mot de passe</label>
                      <input v-model="password" class="form-control" type="password" placeholder="Mot de passe">
                    </div>
                  
                    <div class="mb-3"> 
                    <label class="form-label">Bio</label>
                    <textarea v-model="biography" class="form-control" rows="8"></textarea>
                  </div>
                  </div>
                </div>
                </form>
                <div class="card-footer text-end">
                  <button 
                    :disabled="modifyingUser"
                    @click="modifUser(userInfo.userId)"
                    class="btn btn-primary"
                    type="submit">
                      <p v-if="!modifyingUser">Update Profil</p>
                      <p v-else>En cours de modification ...</p>
                  </button>
                </div>
                <div class="card-footer text-end">
                  <button @click="deleteUser(userInfo.userId)" class="btn btn-primary" type="submit">Delete Account</button>
                </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </MainLayout>  
</template>

<script setup lang="ts">
  import { getLoggedInUserInfo, modifAccount, deleteAccount } from "@/store/authentication";
  import { ref } from "vue";
  import { useRouter } from "vue-router";
  let userInfo = getLoggedInUserInfo();
  const email = ref(userInfo.email);
  const password = ref(userInfo.password);
  const userName = ref(userInfo.userName);
  const userFirstName = ref(userInfo.userFirstName);
  const role = ref(userInfo.role);
  const biography = ref(userInfo.biography);
  const profilePictureSrc = ref(userInfo.profilePictureSrc);

  const router = useRouter();

  const modifyingUser = ref(false);
  const modifUser = async (uid) => {
    modifyingUser.value = true;
    await modifAccount({
      userId: uid,
      userFirstName: userFirstName.value, 
      userName: userName.value, 
      email: email.value,
      password: password.value,
      biography: biography.value
    }, selectedUserImageFile.value)
    modifyingUser.value = false;
    router.push('/profile/')
  };

  const deleteUser = async (uid) => {
      await deleteAccount({
        userId: uid,
        userFirstName: userFirstName.value, 
        userName: userName.value, 
        email: email.value, 
        password: password.value,
        biography: ""
      })
      router.push('/signin')
  };

  // user photo profile
  const selectedUserImageFile = ref(null);
  const onPhotoProfileSelected = (event: any) => {
      const file = event.target.files[0];
      selectedUserImageFile.value = file;
      profilePictureSrc.value = URL.createObjectURL(file);
  }
</script>

<style scoped>
  @import "bootstrap/dist/css/bootstrap.min.css";
  
  :is(h1, h2, h3, h4, h5, h6)
  {
    color: black;
  }

  .form-label {
    color: black;
  }

  .mb-4 {
    color: red;
  }

  .card-header:first-child {
    border-radius: calc(1rem - 1px) calc(1rem - 1px) 0 0;
  }
  .card-header {
    position: relative;
    padding: 2rem 2rem;
    border-bottom: none;
    background-color: white;
    box-shadow: 0 0.125rem 0.25rem rgb(0 0 0 / 8%);
    z-index: 2;
  }
  .card {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    border: none;
    box-shadow: 0 0.5rem 1rem rgb(0 0 0 / 15%);
    border-radius: 1rem;
  }
  
  .avatar.avatar-lg {
    width: 5rem;
    height: 5rem;
    line-height: 5rem;
  }
  .avatar {
    display: inline-block;
    position: relative;
    width: 3rem;
    height: 3rem;
    text-align: center;
    border: #dee2e6;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 0.5rem 1rem rgb(0 0 0 / 15%);
    line-height: 3rem;
  }
  .form-control
  {
    color: #343a40;
  }
  .page-heading {
    text-transform: uppercase;
    letter-spacing: 0.2em;
    font-size: xx-large;
    font-weight: 200;
  }
  .contentDiv
  {
    padding-top: 4rem;
  }
  .card-profile .card-header {
    height: 9rem;
    background-position: center center;
    background-size: cover;
  }
  
  /* profile picture */
  input[type="file"] {
      display: none;
  }
  
/* profile picture */
  .card-profile-img {
    position: relative;
    object-fit: cover;
    width:8em;
    height:8em;
    max-width: 8rem;
    max-height: 8rem;
    margin-top: -6rem;
    margin-bottom: 1rem;
    border: 2px solid #fff;
    border-style: solid;
    border-color: #FFFFFF;
    border-radius: 100%;
    box-shadow: 0 0 2px 2px #B8B8B8;
    z-index: 2;
  }

  .userprofile {
    position: relative;
    object-fit: cover;
    width: 8em;
    height: 8em;
    max-width: 8rem;
    max-height: 8rem;
    margin-top: -6rem;
    margin-bottom: 1rem;
    border: 4px solid #fff;
    border-style: solid;
    border-color: #FFFFFF;
    border-radius: 100%;
    box-shadow: 0 0 2px 2px #B8B8B8;
    z-index: 2;
  }

  .userprofile img {
    height: 100%;
    width: 100%;
    border-radius: 100%;
  }

  .userprofile i {
    position: absolute;
    top: 20px;
    right: -7px;
    /* border: 1px solid; */
    border-radius: 50%;
    /* padding: 11px; */
    height: 30px;
    width: 30px;
    display: flex !important;
    align-items: center;
    justify-content: center;
    background-color: white;
    color: cornflowerblue;
    box-shadow: 0 0 8px 3px #B8B8B8;
  }

  .userprofile input[type="file"] {
      display: none;
  }
</style>>
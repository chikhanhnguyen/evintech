<template>
    <MainLayout>
        <main v-if="DataIsLoaded" class="py-13 md:ml-6">
            <div class="list-wrapper">
                <ul class="list" style="margin-left: -13rem">
                    <h1 style="font-size: x-large;"><strong>Liste des utilisateurs :</strong></h1>
                    <div v-if="UsersTab !== ''">
                        <li v-for="user in UsersTab" :key="user">
                            <div v-if="user !== '' && user !== null " class="list-item" style="width: 50rem">
                            <div class="card-profile-img">
                                <img :src="user.profilePictureSrc">
                            </div>
                            <div  class="list-item-content hover">
                                <h4>{{ user.userFirstName }} {{ user.userName }}</h4>
                                <p style="font-size: small;">{{ user.role }}</p>
                            </div>
                            <div class="list-item-content" style="margin-top: 8px;">
                                <button @click="StudentRoleSet(user.userId)" type="submit" style="margin: 6px; width: 6rem;" class="h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                                    Set Student
                                </button>
                                <button @click="OrganizerRoleSet(user.userId)" type="submit" style="margin: 6px; width: 6rem;" class="h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                                    Set Organizer
                                </button>
                                <button @click="AdminRoleSet(user.userId)" type="submit" style="margin: 6px; width: 6rem;" class="h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                                    Set Admin
                                </button>
                                <i @click="deleteAccount(user.userId)" class="fa-solid fa-trash-can hover" style="cursor: pointer; margin:10px;"></i>
                            </div>
                            </div>
                        </li>
                    </div>
                    <div v-else>
                        <li class="list-item">
                            <div class="list-item-content">
                                <h4>Aucun utilisateurs &#128528;</h4>
                            </div>
                        </li>
                    </div>
                </ul>
            </div>
            <div class="list-wrapper">
                <ul class="list" style="margin-left: -13rem">
                    <h1 style="font-size: x-large;"><strong>Liste des évènements :</strong></h1>
                    <div v-if="EventsTab !== ''">
                        <li v-for="event in EventsTab" :key="event" class="list-item" style="width: 25rem">
                            <div class="list-item-content hover">
                                <h4>{{ event.eventName }}</h4>
                                <p style="font-size: small">{{ event.category }}</p>
                            </div>
                            <div class="list-item-content" style="margin-top: 8px;">
                                <i @click="DeleteEvent(event.eventName)" class="fa-solid fa-trash-can hover" style="cursor: pointer; margin:10px;"></i>
                            </div>
                        </li>
                    </div>
                    <div v-else>
                        <li class="list-item">
                            <div class="list-item-content">
                                <h4>Aucun évènement &#128528;</h4>
                            </div>
                        </li>
                    </div>
                </ul>
            </div>
        </main>
    </MainLayout>
</template>
<script lang="ts">
import { getAllUsers, deleteAccountByAdmin, setRoleAdmin, setRoleOrganizer, setRoleStudent } from "../store/authentication";
import { getAllEvents, DeleteEventByAdmin } from "../store/events";
export default ({
    data() {
        return {
            DataIsLoaded: false,
            UsersTab: [],
            EventsTab: [],
        }
    },
    async mounted() {
        const AllUsers = await getAllUsers();
        this.UsersTab = AllUsers;
        const AllEvents = await getAllEvents();
        this.EventsTab = AllEvents;
        this.DataIsLoaded = true;
    },
    methods: {
        async deleteAccount(userId : string) {
            await deleteAccountByAdmin(userId);
            this.UsersTab = await getAllUsers();
        },
        async AdminRoleSet(userId : string) {
            await setRoleAdmin(userId);
            const AllUsers = await getAllUsers();
            this.UsersTab = AllUsers;
        },
        async OrganizerRoleSet(userId : string) {
            await setRoleOrganizer(userId);
            const AllUsers = await getAllUsers();
            this.UsersTab = AllUsers;
        },
        async StudentRoleSet(userId : string) {
            await setRoleStudent(userId);
            const AllUsers = await getAllUsers();
            this.UsersTab = AllUsers;
        },
        async DeleteEvent(eventName: string) {
            await DeleteEventByAdmin(eventName);
            const AllEvents = await getAllEvents();
            this.EventsTab = AllEvents;
        }
    },
})

</script>
<style scoped>
.hover:hover {
    color: rgba(0, 125, 252, var(--tw-bg-opacity));
}

.list-wrapper {
    max-width: 400px;
    margin: 50px auto;
}

.list {
    border-radius: 2px;
    list-style: none;
    padding: 10px 20px;
}

.list-item {
    display: flex;
    padding-bottom: 5px;
    border: 1px solid rgb(170, 170, 170);
    border-radius: 1rem;
    box-shadow: 0 0 10px rgb(102, 36, 181);
    margin: 5px;
}

.list-item-content {
    margin-left: 20px;
}

.list-item-content h4 {
    margin: 0;
}

.list-item-content h4 {
    margin-top: 10px;
    font-size: 18px;
}

  .card-profile-img {
    position: relative;
    object-fit: cover;
    overflow: hidden;
    width:4em;
    height:4em;
    max-width: 3rem;
    max-height: 3rem;
    margin-left: 0.5rem;
    margin-top: 0.4rem;
    margin-bottom: 0.1rem;
    border: 4px solid #fff;
    border-style: solid;
    border-color: #FFFFFF;
    border-radius: 100%;
    box-shadow: 0 0 0px 0px #B8B8B8;
  }
</style>
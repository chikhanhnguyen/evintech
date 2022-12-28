<template>
    <MainLayout>
        <main v-if="DataIsLoaded" class="py-13 md:ml-6">
            <div class="list-wrapper">
                <ul class="list" style="margin-left: -10rem">
                    <h2>Evenement favoris :</h2>
                    <div v-if="FavsTab.length !== 0">
                        <li v-for="fav in FavsTab" :key="fav" class="list-item"
                            style="width: 40rem">
                            <div class="list-item-content hover">
                                <h4>{{ fav.eventName }}</h4>
                                <p style="font-size: small;">{{ fav.category }} | {{ fav.price }} €</p>
                            </div>
                            <div class="list-item-content" style="margin-top: 8px;">
                                <button @click="deleteFav(fav.eventName)" type="submit" style="margin: 6px; width: 7rem;" class="h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                                    Supprimer des favoris
                                </button>
                            </div>
                        </li>
                    </div>
                    <div v-else>
                        <li class="list-item">
                            <div class="list-item-content">
                                <h4>Aucun évènement en favoris &#128528;</h4>
                            </div>
                        </li>
                    </div>
                </ul>
            </div>
        </main>
    </MainLayout>
</template>
<script lang="ts">
import { getLoggedInUserInfo, getAllFavByUser , deleteFavEvent } from "../store/authentication";
export default ({
    data() {
        return {
            DataIsLoaded: false,
            FavsTab: [],
            UserId: "",
        }
    },
    async mounted() {
        let userInfo = await getLoggedInUserInfo();
        this.UserId = userInfo.userId;
        this.FavsTab = await getAllFavByUser(userInfo.userId);
        this.DataIsLoaded = true;
    },
    methods: {
        async deleteFav(eventName: string) {
            await deleteFavEvent(eventName);
            this.FavsTab = await getAllFavByUser(this.UserId);
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
</style>
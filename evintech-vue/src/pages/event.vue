<template>
    <MainLayout>
        <main class="py-13 md:ml-6">
            <div v-if="!DataIsLoaded" class="flex md:container justify-center py-2 text-sm ">
                <PopupAlert message="En cours de chargement ..." alertType="2"></PopupAlert>
            </div>
            <div v-else>
                <div v-if="EventNotFound" class="flex md:container justify-center py-2 text-sm ">
                    <PopupAlert message="Evénement pas trouvé !!!" alertType="4"></PopupAlert>
                </div>
                <div v-if="!EventNotFound && IsNew" class="flex md:container justify-center py-2 text-sm ">
                    <PopupAlert message="Nouvel évenement a été créé" alertType="1"></PopupAlert>
                </div>
                <div class="flex md:container justify-center w-full py-6 text-sm">
                    <div class="flex flex-col md:flex-row h-[32rem] sm:h-[28rem] w-full">
                        <div class="relative w-full h-full bg-center bg-cover rounded-2xl"
                            :style="{ backgroundImage: `url(${ImageSrc})` }">
                            <div
                                class="absolute inset-0 rounded-2xl bg-gradient-to-t md:bg-gradient-to-r from-black/80 md:from-black/40 to-transparent">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="flex justify-center py-2 text-sm ">
                    <div @click="showContent" class="item-center">
                        <label><strong>Nom : {{ EventName }}</strong></label>
                    </div>
                </div>
                <div class="flex justify-center py-2 text-sm ">
                    <div class="item-center">
                        <label><strong>Catégorie : {{ Category }}</strong></label>
                    </div>
                </div>
                <div class="flex justify-center py-2 text-sm ">
                    <div class="item-center text-center">
                        <label><strong>Description :<br /> {{ Description }}</strong></label>
                    </div>
                </div>
                <div class="flex justify-center py-2 text-sm ">
                    <div class="item-center">
                        <label><strong>Prix : {{ Price }} €</strong></label>
                    </div>
                </div>
                <div v-if="DateIsValid" class="flex justify-center py-2 text-sm ">
                    <div class="item-center">
                        <label>
                            <strong>DEBUT des inscriptions : 
                                <strong>Le {{ DateEvent }}</strong>
                            </strong>
                        </label>
                        <label>
                            <strong>FIN des inscriptions : 
                                <strong>Le {{ DateCloseEvent }}</strong>
                            </strong>
                        </label>
                    </div>
                </div>
                <div class="flex justify-center py-3 text-sm buttonspace">
                    <div
                        class="h-full uppercase transition duration-200 rounded-md w-max whitespace-nowrap flex items-center justify-center gap-2 px-5">
                        <svg width="1em" height="1em" viewBox="0 0 24 10" class="w-1 h-10"></svg>
                        <i class="fa-solid fa-heart"></i>
                        <span class="font-bold text-xxs text-shadow w-30">
                            Nombres de likes : {{ numberOfLikes.length }}
                        </span>
                    </div>
                </div>
                <div class="flex justify-center py-3 text-sm buttonspace">
                    <button v-if="!isLiked" @click="addFavEvent"
                        class="h-full uppercase transition duration-200 rounded-md w-max whitespace-nowrap flex items-center justify-center gap-2 px-5 hover:bg-white/10"
                        style="border-width: thin">
                        <svg width="1em" height="1em" viewBox="0 0 24 10" class="w-1 h-10"></svg>
                        <i class="fa-solid fa-heart-circle-plus"></i>
                        <span class="font-bold text-xxs text-shadow w-30">
                            AIMER
                        </span>
                    </button>
                    <button v-else @click="deleteFavEvent"
                        class="h-full uppercase transition duration-200 rounded-md w-max whitespace-nowrap flex items-center justify-center gap-2 px-5 hover:bg-white/10"
                        style="border-width: thin">
                        <svg width="1em" height="1em" viewBox="0 0 24 10" class="w-1 h-10"></svg>
                        <i class="fa-solid fa-heart-circle-minus"></i>
                        <span class="font-bold text-xxs text-shadow w-30">
                            NE PAS AIMER
                        </span>
                    </button>
                    <button @click="showAttendees"
                        class="h-full uppercase transition duration-200 rounded-md w-max whitespace-nowrap flex items-center justify-center gap-2 px-5 hover:bg-white/10"
                        style="border-width: thin">
                        <svg width="1em" height="1em" viewBox="0 0 24 10" class="w-1 h-10"></svg>
                        <i class="fa-solid fa-users"></i>
                        <span class="font-bold text-xxs text-shadow w-30">
                            AFFICHER LES PARTICIPANTS

                        </span>
                    </button>
                    <button @click="showLikers"
                        class="h-full uppercase transition duration-200 rounded-md w-max whitespace-nowrap flex items-center justify-center gap-2 px-5 hover:bg-white/10"
                        style="border-width: thin">
                        <svg width="1em" height="1em" viewBox="0 0 24 10" class="w-1 h-10"></svg>
                        <i class="fa-solid fa-users"></i>
                        <span class="font-bold text-xxs text-shadow w-30">
                            VOIR LES LIKES
                        </span>
                    </button>
                </div>
                <div v-if="this.AttendeesShowed" class="list-wrapper">
                    <ul class="list">
                        <h2>Participants :</h2>
                        <div v-if="AttendeesTab.length !== 0">
                            <li v-for="attendee in AttendeesTab" :key="attendee.userFirstName" class="list-item"
                                style="width: 20rem">
                                <div class="list-item-content hover">
                                    <h4>{{ attendee.userFirstName }} {{ attendee.userName }}</h4>
                                </div>
                            </li>
                        </div>
                        <div v-else>
                            <li class="list-item">
                                <div class="list-item-content">
                                    <h4>Aucun participant &#128528;</h4>
                                </div>
                            </li>
                        </div>
                    </ul>
                </div>
                <div v-if="this.LikersShowed" class="list-wrapper">
                    <ul class="list">
                        <h2>Likers :</h2>
                        <div v-if="LikersTab.length !== 0">
                            <li v-for="liker in LikersTab" :key="liker" class="list-item"
                                style="width: 20rem">
                                <div class="list-item-content hover">
                                    <h4>{{ liker.userFirstName }} {{ liker.userName }}</h4>
                                </div>
                            </li>
                        </div>
                        <div v-else>
                            <li class="list-item">
                                <div class="list-item-content">
                                    <h4>Aucun liker &#128528;</h4>
                                </div>
                            </li>
                        </div>
                    </ul>
                </div>
                <div class="container">
                    <label for="name"><strong>Écrire un commentaire</strong></label>
                    <div class="form__group">
                        <input v-model="CommentInput" type="input" class="form__field" placeholder="Commentaire"
                            name="comment" id='comment' />
                        <label for="name" class="form__label">Commentaire</label>
                        <button @click="writeComment" type="submit"
                            class="w-20 h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">Envoyer</button>
                    </div>
                </div>
                <div v-for="comment in CommentsTab" :key="comment.id" class="container">
                    <div class="comment__container opened" v-bind:id="comment.idParent">
                        <div class="comment__card ">
                            <h1 class="comment__title"><strong>{{ comment.user.userFirstName }} {{ comment.user.userName
                            }}</strong></h1>
                            <p> {{ comment.content }}
                            </p>
                            <div class="comment__card-footer">
                                <div class="show-replies hover">
                                    Afficher les réponses
                                    <i v-if="!this.Show" class="fa-solid fa-square-caret-down"></i>
                                    <i v-else class="fa-solid fa-square-caret-up"></i>
                                </div>
                                <div class="show-answerinput hover" style="cursor: pointer">Répondre</div>
                                <i v-if="comment.user.userId == this.userId" @click="deleteComment(comment.id)"
                                    class="fa-solid fa-trash-can hover" style="cursor: pointer"></i>
                            </div>
                            <div class="form__group showBlock">
                                <input v-model="AnswerInput" type="input" class="form__field" placeholder="Réponse"
                                    name="reponse" id='reponse' />
                                <label for="name" class="form__label">Réponse</label>
                                <button @click="writeAnswer(comment.idParent)" type="submit"
                                    class="w-20 h-8 bg-epic-blue text-[11px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">Envoyer</button>
                            </div>
                        </div>
                        <div v-for="answer in comment.responses" :key="answer.id" class="comment__container"
                            v-bind:idLink="comment.idParent" id="first-reply">
                            <div class="comment__card">
                                <h1 class="comment__title"><strong>{{ answer.user.userFirstName }} {{ answer.user.userName
                                }}</strong></h1>
                                <p> {{ answer.content }}
                                </p>
                                <div class="comment__card-footer">
                                    <i v-if="answer.user.userId == this.userId" @click="deleteComment(answer.id)"
                                        class="fa-solid fa-trash-can hover" style="cursor: pointer"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="!isJoined">
                    <div v-if="Price == 0" class="flex md:container justify-center py-2 text-sm container">
                        <button v-if="!IsExpired"
                            @click="joinEvent(); sendEmail()" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                            Participer
                        </button>
                        <button v-else
                            :disabled="IsExpired" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                            Terminé
                        </button>
                    </div>
                    <div v-else class="flex md:container justify-center py-2 text-sm ">
                        <button v-if="!IsExpired"
                            @click="paymentEvent" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                            Payer
                        </button>
                        <button v-else
                            :disabled="IsExpired" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                            Terminé
                        </button>
                    </div>
                </div>
                <div v-else>
                    <div class="flex md:container justify-center py-2 text-sm container">
                        <button v-if="!IsExpired"
                            @click="unjoinEvent" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">Ne
                            plus participer</button>
                        <button v-else
                            :disabled="IsExpired" type="submit"
                            class="w-full h-14 bg-epic-blue text-[13px] rounded uppercase font-bold disabled:cursor-default disabled:opacity-40 hover:brightness-110">
                            Terminé
                        </button>
                    </div>
                </div>
                <div v-if="IsExpired" class="flex md:container justify-center py-2 text-sm ">
                    <PopupAlert message="L'évènement est terminé" alertType="3"></PopupAlert>
                </div>
                <div style="text-align: right; padding-right: 5%;">
                    <i v-if="this.eventUserId == this.userId" @click="deleteEvent" class="fa-solid fa-trash-can hover"
                        style="cursor: pointer"></i>
                </div>
            </div>
        </main>
    </MainLayout>
</template>

<script lang="ts">
import { getEventByName, payEvent, participateEvent, unparticipateEvent, likeEvent, deleteLikeEvent, showLikesByEvent, isParticipated, isLiked, showAttendeesByEvent, GetCommentsByEvent, commentEvent, AnswerComment, DeleteComment, DeleteEvent } from "@/store/events";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { getLoggedInUserInfo } from "../store/authentication";
import moment from 'moment';
import emailjs from '@emailjs/browser'; 

export default ({
    data() {
        let isNew: boolean = this.$route.query.isNew === "true";
        const router = useRouter();
        return {
            DataIsLoaded: false,
            EventNotFound: false,
            IsNew: isNew,
            EventName: "",
            Category: "",
            Description: "",
            ImageSrc: "",
            Price: 0,
            DateEvent: "",
            DateCloseEvent: "",
            IsExpired: false,
            DateIsValid: false,
            isJoined: false,
            isLiked: false,
            numberOfLikes: [],
            showContainers: "",
            comments: "",
            AttendeesShowed: false,
            AttendeesTab: [],
            LikersShowed: false,
            LikersTab: [],
            CommentsTab: [],
            Show: false,
            CommentInput: "",
            AnswerInput: "",
            userId: "",
            eventUserId: "",
            userFirstName: "",
            userName: "",
            userMail: "",
        }
    },
    async mounted() {
        let userInfo = getLoggedInUserInfo();
        this.userId = userInfo.userId;
        this.userMail = userInfo.email;
        this.userFirstName = userInfo.userFirstName;
        this.userName = userInfo.userName;
        let eventName: string = this.$route.params.eventName;
        const event = await getEventByName(eventName);
        this.eventUserId = event.creator.userId;
        this.AttendeesTab = await showAttendeesByEvent(eventName);
        this.LikersTab = await showLikesByEvent(eventName);
        const comments = await GetCommentsByEvent(eventName);
        this.CommentsTab = comments;
        const CommentContent = ref();
        this.CommentInput = CommentContent.value;
        const AnswerContent = ref();
        this.AnswerInput = AnswerContent.value;
        this.EventNotFound = event.eventName === "";
        this.EventName = event.eventName;
        this.Category = event.category;
        this.Description = event.description;
        this.ImageSrc = event.imageSrc;
        this.Price = event.price;
        this.DateIsValid = moment(event.dateEvent).isValid() && moment(event.dateCloseEvent).isValid();
        this.DateEvent = moment(event.dateEvent).local().format('DD-MM-YYYY à HH:mm');
        this.DateCloseEvent = moment(event.dateCloseEvent).local().format('DD-MM-YYYY à HH:mm');
        this.IsExpired = this.DateIsValid && (moment(event.dateCloseEvent) < moment());
        this.numberOfLikes = await showLikesByEvent(this.EventName);
        this.isJoined = await isParticipated(this.EventName);
        this.isLiked = await isLiked(this.EventName);
        this.AttendeesShowed = false;
        this.LikersShowed = false;
        this.Show = false;
        this.DataIsLoaded = true;
    },
    methods: {
        async deleteEvent() {
            let eventName: string = this.$route.params.eventName;
            await DeleteEvent(eventName);
            this.$router.push('/');
        },
        async deleteComment(id: number) {
            let eventName: string = this.$route.params.eventName;
            await DeleteComment(eventName, id);
            const comments = await GetCommentsByEvent(eventName);
            this.CommentsTab = comments;
            console.log("Success");
        },
        async writeComment() {
            let eventName: string = this.$route.params.eventName;
            await commentEvent(eventName, this.CommentInput);
            const comments = await GetCommentsByEvent(eventName);
            this.CommentsTab = comments;
            this.CommentInput = "";

        },
        async writeAnswer(idParent: number) {
            let eventName: string = this.$route.params.eventName;
            await AnswerComment(eventName, idParent, this.AnswerInput);
            const comments = await GetCommentsByEvent(eventName);
            this.CommentsTab = comments;
            this.AnswerInput = "";
        },
        async paymentEvent() {
            let user = getLoggedInUserInfo();
            let url = await payEvent({ description: this.EventName + "/" + user.userId, price: this.Price })
            //this.joinEvent()
            location.href = url;
        },
        async joinEvent() {
            await participateEvent(this.EventName);
            let eventName: string = this.$route.params.eventName;
            const attendees = await showAttendeesByEvent(eventName);
            this.AttendeesTab = attendees;
            this.isJoined = await isParticipated(this.EventName);
        },

        async unjoinEvent() {
            await unparticipateEvent(this.EventName);
            let eventName: string = this.$route.params.eventName;
            const attendees = await showAttendeesByEvent(eventName);
            this.AttendeesTab = attendees;
            this.isJoined = false;
        },
        async addFavEvent() {
            await likeEvent(this.EventName);
            const likers = await showLikesByEvent(this.EventName);
            this.numberOfLikes = likers;
            this.LikersTab = likers;
            this.isLiked = true
        },
        async deleteFavEvent() {
            await deleteLikeEvent(this.EventName);
            const likers = await showLikesByEvent(this.EventName);
            this.numberOfLikes = likers;
            this.LikersTab = likers;
            this.isLiked = false;
        },
        showContent() {
            const showContainer = this.containersSelector;
            const showAnswerContainer = this.answercontainersSelector;

            showAnswerContainer.forEach(btn => btn.addEventListener("click", (e) => {
                let parentContainer = e.target.closest(".comment__container");
                let childrenContainer = parentContainer.querySelectorAll(".form__group");
                console.log(childrenContainer);
                childrenContainer.forEach(child => child.classList.toggle("showBlock"));
            }))

            showContainer.forEach(btn => btn.addEventListener("click", (e) => {
                let parentContainer = e.target.closest(".comment__container");
                let _id = parentContainer.id;
                if (_id) {
                    let childrenContainer = parentContainer.querySelectorAll(
                        `[idLink="${_id}"]`
                    );
                    childrenContainer.forEach(child => child.classList.toggle("opened"));
                    this.Show = !this.Show
                }
            }))
        },
        sendEmail() {
            let eventName: string = this.$route.params.eventName;
            let fullName = this.userFirstName + " " + this.userName;
            const templateParams = {
                name: fullName,
                eventName: eventName,
                to_email: this.userMail,
            };

            emailjs.send('service_fgkliyr','template_i43w3br', templateParams, 'lvfXHDD1QdyC6AbOP')
                .then((response) => {
                    console.log('SUCCESS!', response.status, response.text);
                }, (err) => {
                    console.log('FAILED...', err);
                }
            );
        },
        showAttendees() {
            this.AttendeesShowed = !this.AttendeesShowed;
        },
        showLikers() {
            this.LikersShowed = !this.LikersShowed;
        }
    },
    computed: {
        containersSelector() {
            return document.querySelectorAll(".show-replies");
        },
        answercontainersSelector() {
            return document.querySelectorAll(".show-answerinput");
        }
    },
})

</script>

<style scoped>
.showBlock {
    display: none;
}

.hover:hover {
    color: rgba(0, 125, 252, var(--tw-bg-opacity));
}

.form__group {
    position: relative;
    padding: 15px 0 0;
    margin-top: 10px;
    width: 100%;
}

.form__field {
    font-family: inherit;
    width: 100%;
    border: 0;
    border-bottom: 2px solid #9b9b9b;
    outline: 0;
    font-size: 1rem;
    color: #fff;
    padding: 8px 0;
    background: transparent;
    transition: border-color 0.2s;
    margin-bottom: 10px;
}

.form__field::placeholder {
    color: transparent;
}

.form__field:placeholder-shown~.form__label {
    font-size: 1rem;
    cursor: text;
    top: 20px;
}

.form__label {
    position: absolute;
    top: 0;
    display: block;
    transition: 0.2s;
    font-size: 1rem;
    color: #9b9b9b;
}

.form__field:focus {
    padding-bottom: 6px;
    font-weight: 200;
    border-width: 3px;
    border-image: linear-gradient(to right, #11998e, #38ef7d);
    border-image-slice: 1;
}

.form__field:focus~.form__label {
    position: absolute;
    top: 0;
    display: block;
    transition: 0.2s;
    font-size: 1rem;
    color: #11998e;
    font-weight: 700;
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


.buttonspace {
    justify-content: space-evenly;
    padding: 0 2vw;
}

.item-center {
    display: grid;
    gap: 4px;
    align-items: center;
    justify-items: center;
}

*,
*::before,
*::after {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html {
    font-size: 16px;
    min-height: 100%;
}

body {
    min-height: 100vh;
}

.container {
    width: min(90%, 1140px);
    margin: 3rem auto;
}

.comment__container {
    display: none;
    position: relative;
}

.comment__container.opened {
    display: block;
}

.comment__container::before {
    content: "";
    background-color: rgb(170, 170, 170);
    position: absolute;
    min-height: 100%;
    width: 1px;
    left: -10px;
}

.comment__container:not(:first-child) {
    margin-left: 3rem;
    margin-top: 1rem;
}

.comment__card {
    padding: 20px;
    border: 1px solid rgb(170, 170, 170);
    border-radius: .5rem;
    min-width: 100%;
    box-shadow: 0 0 50px rgb(102, 36, 181);
}

.comment__card h3,
.comment__card p {
    margin-bottom: 1rem;
}

.comment__card-footer {
    display: flex;
    font-size: .85rem;
    opacity: .6;
    gap: 30px;
    justify-content: flex-end;
    align-items: center;
}

.show-replies {
    cursor: pointer;
}
</style>
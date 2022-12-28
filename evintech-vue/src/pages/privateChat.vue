<template>
	<MainLayout>
		<main v-if="DataIsLoaded" class="py-14">
			<div class="messaging">
				<div class="inbox_msg">
					<div class="inbox_people">
						<div class="headind_srch">
							<div class="recent_heading">
								<h4>Recent</h4>
							</div>

						</div>
						<div class="inbox_chat">
							<div v-for="(user,index) in UsersTab" :key="user" class="chat_list " style="cursor: pointer">
								<div @click="getConversation(user.userId, index)" class="chat_people">
									<div class="chat_img">
										<img v-if="user.profilePicture" style="border-radius: 10px"
											src="https://marketplace.canva.com/EAE6OH6DF2w/1/0/1600w/canva-moon-astronaut-character-twitch-profile-picture-0kkgyJSodt4.jpg"
											alt="market" />
										<img v-else style="border-radius: 10px"
											src="https://therichpost.com/wp-content/uploads/2021/03/avatar2.png"
											alt="sunil" />
									</div>
									<div class="chat_ib">
										<h5>
											<strong>{{ user.userFirstName }} {{ user.userName }}</strong>
											<h5 v-if="this.LastMsgByPerson[index] != ''" style="word-wrap: break-word; font-size: small;">{{ this.LastMsgByPerson[index].content }}</h5>
											<span class="chat_date"><strong>{{ this.LastMsgByPerson[index].date }}</strong></span>
											<p v-if="this.LastMsgByPerson[index] != ''" style="font-size: 13px; float: left">
												Envoyé par : {{ this.LastMsgByPerson[index].sender.userFirstName }} {{ this.LastMsgByPerson[index].sender.userName }}
											</p>
										</h5>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="mesgs ">
						<div class="msg_history scroll">
							<div v-for="conv in ConvUser" :key="conv">
								<div v-if="conv.recipient.userId == this.UserId" class="incoming_msg">
									<div class="incoming_msg_img">
										<img src="https://ptetutorials.com/images/user-profile.png" alt="sunil" />
									</div>
									<div class="received_msg">
										<div class="received_withd_msg">
											<p style="word-wrap: break-word;">{{ conv.content }}</p>
											<span class="time_date">{{ conv.date }}</span>
										</div>
									</div>
								</div>
								<div v-else class="outgoing_msg">
									<div class="sent_msg">
										<p style="word-wrap: break-word;">{{ conv.content }}</p>
										<span class="time_date">{{ conv.date }}</span>
									</div>
								</div>
							</div>
						</div>
						<p class="alert">{{ MsgErr }}</p>
						<div class="type_msg">
							<div class="input_msg_write">
								<input v-on:keyup.enter="sendMessage(this.RecipientId)" v-model="MsgInput" type="text" class="write_msg"
									placeholder="Ecrivez un message" />
									<button @click="sendMessage(this.RecipientId)" class="msg_send_btn" type="submit">
										<i class="fa fa-paper-plane-o" aria-hidden="true"></i>
									</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</main>
	</MainLayout>
</template>
<script lang="ts">
import {
	getAllUsersByDate,
	getConv,
	sendMsg,
	getLoggedInUserInfo,
	getTRUC,
	isOpenedMsg,
	getLastMessage,
} from "../store/authentication";

import { ref } from "vue";
export default {
	data() {
		return {
			DataIsLoaded: false,
			UsersTab: [],
			ConvUser: [],
			MsgInput: "",
			RecipientId: "",
			UserId: "",
			LastMsg: "",
			MsgOpened: [],
			lastMessage:"",
			isOpen: true,
			LastMsgByPerson:[],
			MsgErr: "",
        }
    },
    async mounted() {
        const userInfo = getLoggedInUserInfo();
        this.UserId = userInfo.userId;
        this.UsersTab = await getAllUsersByDate();
		console.log(this.UsersTab);
		for (let i = 0; i < this.UsersTab.length; i++) {
  			this.LastMsgByPerson[i] = await getLastMessage(this.UsersTab[i].userId);
		}
        this.DataIsLoaded = true;
        const MsgContent = ref();
        this.MsgInput = MsgContent.value;
    },

    methods: {
		async miseAJourLastMessage(userid: string) {
			let index = 0;
			let exist = false;
			for (let i = 0; i < this.LastMsgByPerson.length; i++) 
			{
				if(this.LastMsgByPerson[i] != '')
				{
					if(this.LastMsgByPerson[i].recipient.userId == userid) 
					{
						this.LastMsgByPerson[i] = await getLastMessage(this.LastMsgByPerson[i].recipient.userId);
						index = i;
						exist = true;
					}
					else if(this.LastMsgByPerson[i].sender.userId == userid)
					{
						this.LastMsgByPerson[i] = await getLastMessage(this.LastMsgByPerson[i].sender.userId);
						index = i;
						exist = true;
					}
				}
			}

			if(!exist)
			{
				this.LastMsgByPerson.push(await getLastMessage(userid));
				index = this.LastMsgByPerson.length - 1;
			}

			if(this.LastMsgByPerson.length == 2)
			{
				if(index == 1)
				{
					const tmp = this.LastMsgByPerson[0];
					this.LastMsgByPerson[0] = this.LastMsgByPerson[1];
					this.LastMsgByPerson[1] = tmp;
				}
				else
				{

				}
			}

			else if(this.LastMsgByPerson.length > 2 && index != 0)
			{
				let tmp = this.LastMsgByPerson[0];
				let tmpTwo = -1;
				this.LastMsgByPerson[0] = this.LastMsgByPerson[index];

				for (let i = 1; i < this.LastMsgByPerson.length; i++) {
					if(tmpTwo != this.LastMsgByPerson[0])
					{
						if(i == 1)
						{
							tmpTwo = this.LastMsgByPerson[i];
							this.LastMsgByPerson[i] = tmp;
						}
						else
						{
							tmp = tmpTwo;
							tmpTwo = this.LastMsgByPerson[i];
							this.LastMsgByPerson[i] = tmp;
						}
					}
				}
			}	
		},

        async getConversation(userId: string, index: number) {
            this.ConvUser = await getConv(userId);
			this.UsersTab = await getAllUsersByDate();
            this.RecipientId = userId;
			let container = document.querySelector(".scroll");
			if(container != null)
			{
				let scrollHeight = container.scrollHeight;
				container.scrollTop = scrollHeight;
			}
			let UserContainer = document.querySelectorAll(".chat_list");
			UserContainer.item(index).classList.toggle("active_chat");
		},

		async getLastMessageConv(userId: string) {
            this.lastMessage = await getLastMessage(userId);
        },

        async sendMessage(userId:string) {
			const TrimMsg = String(this.MsgInput).trim();
			if(TrimMsg != "" && this.MsgInput != null){
				await sendMsg(userId, this.MsgInput);
				this.MsgOpened[userId] = await isOpenedMsg(userId);
				this.ConvUser = await getConv(userId);			
				this.UsersTab = await getAllUsersByDate();
				this.MsgInput = "";
				this.miseAJourLastMessage(userId);
				let container = document.querySelector(".scroll");
				if(container != null)
				{
					let scrollHeight = container.scrollHeight;
					container.scrollTop = scrollHeight;
				}
			} 
			else 
			{
				this.MsgErr = "Veuillez entrer un message !";
			}
		},

		async isOpenedMessage(userId: string) {
			this.MsgOpened[userId] = await isOpenedMsg(userId);
		},

		async getTRUCTRUC(userId: string) {
			await getTRUC(userId);
		},
    }
}

</script>
<style scoped>
.container {
	max-width: 1170px;
	margin: auto;
}

img {
	max-width: 100%;
}

.inbox_people {
	background: #f8f8f8 none repeat scroll 0 0;
	float: left;
	overflow: hidden;
	width: 40%;
	border-right: 1px solid #c4c4c4;
}

.inbox_msg {
	border: 1px solid #c4c4c4;
	clear: both;
	overflow: hidden;
}

.top_spac {
	margin: 20px 0 0;
}

.recent_heading {
	float: left;
	width: 40%;
}

.srch_bar {
	display: inline-block;
	text-align: right;
	width: 60%;
}

.headind_srch {
	padding: 10px 29px 10px 20px;
	overflow: hidden;
	border-bottom: 1px solid #c4c4c4;
}

.recent_heading h4 {
	color: #05728f;
	font-size: 21px;
	margin: auto;
}

.srch_bar input {
	border: 1px solid #cdcdcd;
	border-width: 0 0 1px 0;
	width: 80%;
	padding: 2px 0 4px 6px;
	background: none;
}

.srch_bar .input-group-addon button {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0;
	border: medium none;
	padding: 0;
	color: #707070;
	font-size: 18px;
}

.srch_bar .input-group-addon {
	margin: 0 0 0 -27px;
}

.chat_ib h5 {
	font-size: 15px;
	color: #464646;
	margin: 0 0 8px 0;
}

.chat_ib h5 span {
	font-size: 13px;
	float: right;
}

.chat_ib p {
	font-size: 14px;
	color: #989898;
	margin: auto;
}

.chat_img {
	float: left;
	width: 11%;
}

.chat_ib {
	float: left;
	padding: 0 0 0 15px;
	width: 88%;
}

.chat_people {
	overflow: hidden;
	clear: both;
}

.chat_list {
	border-bottom: 1px solid #c4c4c4;
	margin: 0;
	padding: 18px 16px 10px;
}

.inbox_chat {
	height: 550px;
	overflow-y: scroll;
}

.active_chat {
	background: #ebebeb;
}

.incoming_msg_img {
	display: inline-block;
	width: 6%;
}

.received_msg {
	display: inline-block;
	padding: 0 0 0 10px;
	vertical-align: top;
	width: 92%;
}

.received_withd_msg p {
	background: #ebebeb none repeat scroll 0 0;
	border-radius: 3px;
	color: #646464;
	font-size: 14px;
	margin: 0;
	padding: 5px 10px 5px 12px;
	width: 100%;
}

.time_date {
	color: #747474;
	display: block;
	font-size: 12px;
	margin: 8px 0 0;
}

.received_withd_msg {
	width: 57%;
}

.mesgs {
	float: left;
	padding: 30px 15px 0 25px;
	width: 60%;
}

.sent_msg p {
	background: #05728f none repeat scroll 0 0;
	border-radius: 3px;
	font-size: 14px;
	margin: 0;
	color: #fff;
	padding: 5px 10px 5px 12px;
	width: 100%;
}

.outgoing_msg {
	overflow: hidden;
	margin: 26px 0 26px;
}

.sent_msg {
	float: right;
	width: 46%;
}

.write_msg {
	padding-left: 10px;
}

.input_msg_write input {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0;
	border: medium none;
	color: white;
	font-size: 15px;
	min-height: 48px;
	width: 100%;
}


.type_msg {
	border-top: 1px solid #c4c4c4;
	position: relative;
}

.msg_send_btn {
	background: #05728f none repeat scroll 0 0;
	border: medium none;
	border-radius: 50%;
	color: #fff;
	cursor: pointer;
	font-size: 15px;
	height: 33px;
	position: absolute;
	right: 10px;
	top: 6px;
	width: 33px;
}

.messaging {
	padding: 0 0 50px 0;
}

.msg_history {
	height: 516px;
	overflow-y: auto;
}
</style>
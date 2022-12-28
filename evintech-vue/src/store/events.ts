import { reactive } from "vue";
import { qs } from "qs";
import axios from "axios";
import authHeader from './../services/auth-header';
import PrivateChatVue from "@/views/PrivateChat.vue";

const instanceBackend = axios.create({
  baseURL: "http://localhost:8080/api/events"
})

const instanceImageBackend = axios.create({
  baseURL: "http://localhost:8080/api/image/event"
})

////////////////////////////////////////////////
interface ICreateNewEvent {
  eventName: string,
  category: string,
  description: string,
  imageFile: any,
  price: number,
  dateEvent: string,
  dateCloseEvent: string
}

export const adminCreateNewEvent = async (newEvent: ICreateNewEvent) => {
  let errorMsg = "";
  let ok = false;
  await instanceBackend.post("", newEvent, { headers: authHeader("") })
    .then(response => {
      ok = true;
    }).catch(err => {
      switch (err.response.status) {
        case 403:
          errorMsg = "Vous n'avez pas droit de créer un évenement"
          break;
        default:
          errorMsg = "Erreur"
          break;
      }
    })

  // upload image
  if (ok) {
    const fd = new FormData();
    fd.append('file', newEvent.imageFile, newEvent.imageFile.name);
    await instanceImageBackend.post("/" + newEvent.eventName, fd, { headers: authHeader('multipart/form-data') })
      .then(response => {
        ok = true;
        errorMsg = "";
      }).catch(err => {
        switch (err.response.status) {
          case 403:
            errorMsg = "Vous n'avez pas droit de créer un évenement"
            break;
          default:
            errorMsg = "Erreur"
            break;
        }
      })
  }

  return errorMsg;
}

////////////////////////////////////////////////
export interface IEvent {
  eventName: string,
  category: string,
  urlImage: string,
  description: string,
  imageSrc: string,
  price: number,
  dateEvent: string,
  dateCloseEvent: string,
  dateCreation: string,
  id: number
}

const nullEvent = reactive<IEvent>({
  eventName: "",
  category: "",
  urlImage: "",
  description: "",
  imageSrc: "",
  price: 0,
  dateEvent: "",
  dateCloseEvent: "",
  dateCreation: "",
  id: 0
})

const transformImage = async (event: IEvent) => {
  if (event.urlImage === null || event.urlImage === "") {
    return "src/assets/images/events/escapegame.jpeg"
  }
  return "data:image/png;base64," + event.urlImage;
}

export const getAllEvents = async () => {
  let allEvents: IEvent[] = []
  await instanceBackend.get("", { headers: authHeader("") })
    .then(reponse => {
      if (reponse.status == 200) {
        allEvents = reponse.data;
      }
    })
    .catch(err => {
    })
  // transform images
  for (const event of allEvents) {
    event.imageSrc = await transformImage(event);
  }
  return allEvents;
}

export const getAllCategories = async () => {
  let allCategories: string[] = []
  await instanceBackend.get("/category", { headers: authHeader("") })
    .then(reponse => {
      if (reponse.status == 200) {
        allCategories = reponse.data;
      }
    })
    .catch(err => {
    })
  return allCategories;
}

export const getEventByKey = async (query: string) => {
  let body = {
    "content": query
  }
  let allEvents: IEvent[] = []
  await instanceBackend.post("/research", body)
    .then(reponse => {
      if (reponse.status == 200) {
        allEvents = reponse.data;
      }
    })
    .catch(err => {
    })
  // transform images
  for (const event of allEvents) {
    event.imageSrc = await transformImage(event);
  }
  return allEvents;
}

export const getTopEvents = async (numberEvents: number) => {
  let allEvents = await getAllEvents();
  return allEvents.slice(0, numberEvents);
}

export const getProchainEvents = async (numberEvents: number) => {
  let allEvents: IEvent[] = []
  await instanceBackend.get("/dateSort", { headers: authHeader("") })
    .then(reponse => {
      if (reponse.status == 200) {
        allEvents = reponse.data;
      }
    })
    .catch(err => {
    })
  // transform images
  for (const event of allEvents) {
    event.imageSrc = await transformImage(event);
  }
  return allEvents.slice(0, numberEvents);
}

export const getEventsByCategories = async (categories: string[]) => {
  let listEvents: IEvent[] = []
  // await instanceBackend.get(`/category/manyCategory?${["Jeu", "Photo"].map((n, index) => `categories[${index}]=${n}`).join('&')}`)
  await instanceBackend.post("/category/manyCategory/",
    {
      "categories": categories
    }
  )
    .then(reponse => {
      if (reponse.status == 200) {
        listEvents = reponse.data;
      }
    })
    .catch(err => {
    })
  // transform images
  for (const event of listEvents) {
    event.imageSrc = await transformImage(event);
  }
  return listEvents;
}

export const getEventByName = async (eventName: string) => {
  let event = nullEvent;
  await instanceBackend.get("/" + eventName, { headers: authHeader("") })
    .then(reponse => {
      if (reponse.status == 200) {
        console.log(reponse);
        event = reponse.data;
      }
    })
    .catch(err => {
    })
  event.imageSrc = await transformImage(event);
  return event;
}

interface IPay {
  description: string,
  price: number
}

export const payEvent = async (Pay: IPay) => {
  return await instanceBackend
    .post("/pay", Pay)
    .then(response => {
      return response.data.substring(9);
    })
}

// Modifier son evenement
export const modifyEvent = async (event: IEvent) => {
  await instanceBackend
    .put("/" + event.eventName,
      {
        "eventName": event.eventName,
        "category": event.category,
        "description": event.description,
        "urlImage": event.urlImage,
        "imageSrc": event.imageSrc,
        "price": event.price,
      }, { headers: authHeader("") })
    .then(response => {
      console.log(response);
    })
}

//Participer a un event
export const participateEvent = async (eventName: string) => {
  await instanceBackend
    .get("/participate/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

//Ne plus participer a un event

export const unparticipateEvent = async (eventName: string) => {
  await instanceBackend
    .delete("/participate/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

// Voir tous les participants d'un event en tant qu'organisateur ou admin
export const showAttendeesByEvent = async (eventName: string) => {
  return await instanceBackend
    .get("/attendees/" + eventName, { headers: authHeader("") })
    .then(data => {
      return data.data;
    }).catch(err => {
      console.error(err);
    })
}

//Obtenir tous les likes d'un event

export const showLikesByEvent = async (eventName: string) => {
  return await instanceBackend
    .get("/likes/" + eventName, { headers: authHeader("") })
    .then(data => {
      return data.data
    }).catch(err => {
      console.error(err);
    })
}

export const isLiked = async (eventName: string) => {
  return await instanceBackend
    .post("/isLike", { "content": eventName }, { headers: authHeader("") })
    .then(data => {
      return data.data;
    }).catch(err => {
      console.error(err);
    })
}

export const isParticipated = async (eventName: string) => {
  return await instanceBackend
    .post("/isParticipate", { "content": eventName }, { headers: authHeader("") })
    .then(data => {
      return data.data;
    }).catch(err => {
      console.error(err);
    })
}

// Liker un evenement
export const likeEvent = async (eventName: string) => {
  await instanceBackend
    .get("/like/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

//  Supprimer un like
export const deleteLikeEvent = async (eventName: string) => {
  await instanceBackend
    .delete("/like/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

// Mettre un commentaire a l'event
export const commentEvent = async (eventName: string, content: string) => {
  await instanceBackend
    .post("/comment/" + eventName, { content: content }, { headers: authHeader("") })
    .then(data => {
      console.log(data.data);
    }).catch(err => {
      console.error(err);
    })
}

//Obtenir tous les comments d'un evenement
export const GetCommentsByEvent = async (eventName: string) => {
  return await instanceBackend.get("/comment/" + eventName, { headers: authHeader("") })
    .then(data => {
      return data.data;
    }).catch(err => {
      console.error(err);
    })
}

//Repondre a un commentaire
export const AnswerComment = async (eventName: string, idParent: number, content: string) => {
  await instanceBackend.post("/response/" + eventName + "/" + idParent, { "content": content }, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

// Supprimer un commentaire ou une reponse
export const DeleteComment = async (eventName: string, id: number) => {
  await instanceBackend.delete("/comment/" + eventName + "/" + id, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

// Supprimer son evenement
export const DeleteEvent = async (eventName: string) => {
  await instanceBackend.delete("/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

// Supprimer un evenement en tant qu'admin 
export const DeleteEventByAdmin = async (eventName: string) => {
  await instanceBackend.delete("/admin/" + eventName, { headers: authHeader("") })
    .then(data => {
      console.log(data);
    }).catch(err => {
      console.error(err);
    })
}

/*
Obtenir tous les evenements d'une categorie

instanceBackend.get("/category/Jeux",config)
.then(data => {
console.log(data.data);
}).catch(err => {
console.error(err);
})

Obtenir un evenement

instanceBackend.get("/nomEvenement")
.then(data => {
console.log(data.data);
}).catch(err => {
console.error(err);
})

*/
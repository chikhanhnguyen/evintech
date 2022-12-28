import axios from "axios";
import authHeader from "./../services/auth-header";

//Creer l'instance axios

const instanceBackend = axios.create({
  baseURL: "http://localhost:8080/api/users",
});

const instanceImageBackend = axios.create({
  baseURL: "http://localhost:8080/api/image/user"
})

//Interface---------------------------------------------------------------------------------------------------

// Log in info interface
interface ILoginInfo {
  username: string;
  password: string;
}

//Register info interface
interface IRegisterInfo {
  userId: string;
  userName: string;
  userFirstName: string;
  email: string;
  password: string;
  biography: string
}

//Event Interface
interface Event {
  id: number;
  eventName: string;
  category: string;
  urlImage: string;
  description: string;
  price: number;
  creator: User;
  dateCreation: string;
  dateCloseEvent: string;
  dateEvent: string;
  like: [User];
  comments: [Comment];
  participants: [User];
}


//User Interface
interface User {
  userId: string;
  userName: string;
  userFirstName: string;
  email: string;
  password: string;
  role: string;
  biography: string;
  profilePicture: string;
  profilePictureSrc: string;
  eventFavorites: [Event];
}

//Message Interface
interface Message {
  id: number;
  sender: User;
  recipient: User;
  content: string;
  date: string;
  isSeen: boolean;
}

//Comment Interface
interface Comment{
  id: number;
  user: User;
  content: string;
  date: string;
  responses: [Comment];
  response: boolean;
  idParent: number;
}

//------------------------------------------------------------------------------------------------------------

//Check si l'utilisateur est connecte
const transformImage = (user: User) => {
  if (user.profilePicture === null || user.profilePicture === "") {
    return "src/assets/images/defautuser.png"
  }
  return "data:image/png;base64," + user.profilePicture;
}

export const isLoggedInUser = () => {
  let userStorage = localStorage.getItem("user");
  if (userStorage != null) {
    return true;
  }
  return false;
};

// Recupere les informations de l'utilisateur connecte

export const getLoggedInUserInfo = () => {
  let userStorage = localStorage.getItem("user");
  if (userStorage != null) {
    return JSON.parse(userStorage);
  }
  let userJwtStorage = localStorage.getItem("userJwt");
  if (userJwtStorage != null) {
    let config = {
      headers: {
        Authorization: "Bearer " + userJwtStorage,
      },
    };
    return instanceBackend
      .get<User>("/information", config)
      .then((response) => {
        if (response.status == 200) {
          let user: User = response.data;
          user.profilePictureSrc = transformImage(user);
          localStorage.setItem("user", JSON.stringify(user));
          return response.data;
        } else {
          localStorage.removeItem("user");
          localStorage.removeItem("userJwt");
        }
      })
      .catch((err) => {
        console.error(err);
        localStorage.removeItem("user");
        localStorage.removeItem("userJwt");
      });
  }
  return null;
};

//Se connecter pour avoir le token jwt

export const logInUser = async (userLoginInfo: ILoginInfo) => {
  let jwt = "";
  await instanceBackend
    .post("/signin", userLoginInfo)
    .then(function (response: any) {
      if (response.status == 200) {
        jwt = response.data;
        localStorage.setItem("userJwt", response.data);
        console.log(response);
      } else {
        localStorage.removeItem("userJwt");
        console.log(response);
      }
    })
    .catch(function (error: any) {
      console.log(error);
      localStorage.removeItem("userJwt");
    });

  if (jwt != "") {
    let config = {
      headers: {
        Authorization: "Bearer " + jwt,
      },
    };
    await instanceBackend
      .get<User>("/information", config)
      .then((response) => {
        if (response.status == 200) {
          let user: User = response.data;
          user.profilePictureSrc = transformImage(user);
          console.log(user);
          localStorage.setItem("user", JSON.stringify(user));
        } else {
          localStorage.removeItem("user");
        }
      })
      .catch((err) => {
        console.error(err);
        localStorage.removeItem("user");
      });
  }
};

//Se connecter avec Google

export const LogWithGoogle = async (user) => {
  let jwt = "";
  let token = await user.getIdToken().then(function (response: any) {
    return response;
  });
  await instanceBackend
    .get("/" + user.uid)
    .then(async (data) => {
      if (data.data == "") {
        await instanceBackend
          .post("/signInGoogle", {
            content: token,
          })
          .then(async function (response: any) {
            await logInUser({ username: user.email, password: "" });
          });
      } else {
        await logInUser({ username: user.email, password: "" });
      }
    })
    .catch((err) => {
      console.error(err);
    });
};

//Se connecter pour avoir le token jwt

export const logOutUser = async () => {
  localStorage.removeItem("userJwt");
  localStorage.removeItem("user");
};

//Creer un compte

export const register = async (userRegisterInfo: IRegisterInfo) => {
  return await instanceBackend
    .post("/visitor", userRegisterInfo)
    .then(function (response: any) { })
    .catch(function (error: any) {
      console.log(error);
    });
};

//Modifier son profil

export const modifAccount = async (userRegisterInfo: IRegisterInfo, userProfileFile: any) => {
  let ok = false;
  await instanceBackend
    .put("/" + userRegisterInfo.userId, userRegisterInfo, {
      headers: authHeader(""),
    })
    .then(async (response) => {
      ok = true;
      // await logInUser({
      //   username: userRegisterInfo.email,
      //   password: userRegisterInfo.password,
      // });
    })
    .catch((err) => {
      console.error(err);
    });
  //
  if (userProfileFile != null) {
    const fd = new FormData();
    fd.append('file', userProfileFile, userProfileFile.name);
    await instanceImageBackend
      .post("/", fd, {
        headers: authHeader(""),
      })
      .then(async (response) => {
      })
      .catch((err) => {
        ok = false;
        console.error(err);
      });
  }
  if (ok) {
    await logInUser({
      username: userRegisterInfo.email,
      password: userRegisterInfo.password,
    });
  }
};

//Supprimer son profil

export const deleteAccount = async (userRegisterInfo: IRegisterInfo) => {
  await instanceBackend
    .delete("/" + userRegisterInfo.userId, { headers: authHeader("") })
    .then((response) => { })
    .catch((err) => {
      console.error(err);
    });
};

//Mettre role student en tant qu'admin

export const setRoleStudent = async (userId: string) => {
  await instanceBackend
    .get("/giveRole/student/" + userId, { headers: authHeader("") })
    .then((data) => { })
    .catch((err) => {
      console.error(err);
    });
};

//Mettre role organizer en tant qu'admin

export const setRoleOrganizer = async (userId: string) => {
  await instanceBackend
    .get("/giveRole/organizer/" + userId, { headers: authHeader("") })
    .then((data) => { })
    .catch((err) => {
      console.error(err);
    });
};

//Mettre role admin en tant qu'admin

export const setRoleAdmin = async (userId: string) => {
  await instanceBackend
    .get("/giveRole/admin/" + userId, { headers: authHeader("") })
    .then((data) => { })
    .catch((err) => {
      console.error(err);
    });
};

//Get All Users Infos

export const getAllUsers = async () => {
  let allUsers: User[] = []
  await instanceBackend
    .get("", { headers: authHeader("") })
    .then((data) => {
      allUsers= data.data;
    })
    .catch((err) => {
      console.error(err);
    });
  
  // transform images
  for (const user of allUsers) {
    user.profilePictureSrc = await transformImage(user);
  }
  return allUsers;
};

//Supprimer le profil en tant qu'admin d'une personne

export const deleteAccountByAdmin = async (userId: string) => {
  await instanceBackend
    .delete("/admin/" + userId, { headers: authHeader("") })
    .then((data) => { })
    .catch((err) => {
      console.error(err);
    });
};

//Ajouter au favori

export const addFavEvent = async (eventName: string) => {
  await instanceBackend
    .get("/favorites/" + eventName, { headers: authHeader("") })
    .then((data) => {
      console.log(data.data);
    })
    .catch((err) => {
      console.error(err);
    });
};

//Enlever des favoris

export const deleteFavEvent = async (eventName: string) => {
  await instanceBackend
    .delete("/favorites/" + eventName, { headers: authHeader("") })
    .then((data) => {
      console.log(data.data);
    })
    .catch((err) => {
      console.error(err);
    });
};

//Get AllFavoris By User

export const getAllFavByUser = async (uid: string) => {
  return await instanceBackend
    .get("/getFavorites/" + uid, { headers: authHeader("") })
    .then((data) => {
      return data.data;
    })
    .catch((err) => {
      console.log(err);
    });
};

//Check if Event is in Favorite (return boolean)

export const isFavorited = async (eventName: string) => {
  return await instanceBackend
    .post("/isFavorite", { content: eventName }, { headers: authHeader("") })
    .then((data) => {
      return data.data;
    })
    .catch((err) => {
      console.error(err);
    });
};

//Envoyer un message

export const sendMsg = async (userId: string, content: string) => {
  await instanceBackend
    .post("/sendMessage", { uid: userId, content: content }, { headers: authHeader("") }
    )
    .then((data) => {
      console.log(data);
    })
    .catch((err) => {
      
    });
};

//Récupérer les utilisateurs par date de message envoyé

export const getAllUsersByDate = async () => {
  return await instanceBackend
    .get<Message[]>("/getConvByDate", { headers: authHeader("") })
    .then((data) => {
      return data.data;
    })
    .catch((err) => {
      console.log(err);
    })
}

// Get Open Msg

export const isOpenedMsg = async (uid: string) => {
  return await instanceBackend
    .get<boolean>("/isOpenMessage/" + uid, { headers: authHeader("") })
    .then((data) => {
      return data.data;
    })
    .catch((err) => {
      console.log(err);
    })
}

// Get Truc

export const getTRUC = async (uid:string) => {
  await instanceBackend
    .get("/openMessage" + uid, { headers: authHeader("") })
    .then((data) => {
    })
    .catch((err) => {
      console.log(err);
    })
}

//Obtenir une conversation

export const getConv = async (userId: string) => {
  return await instanceBackend.get("/message/" + userId, { headers: authHeader("") })
    .then(data => {
      
      return data.data;
    }).catch(err => {
      console.error(err);
    })

};

//Obtenir le dernier message d'une conversation

export const getLastMessage = async (userId: string) => {
  return await instanceBackend.get<Message>("/lastMessage/" + userId, { headers: authHeader("") })
    .then(data => {
      
      return data.data;
    }).catch(err => {
      console.error(err);
    })

};

////////////////////////////////////////////////////////////
function delete_cookie(name: string) {
  document.cookie = name + "=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;";
}

function add_cookie(name: string, value: any, expiresDateTimeString: string) {
  document.cookie =
    name + "=" + value + ";" + "Expires=" + expiresDateTimeString + ";Path=/;";
}
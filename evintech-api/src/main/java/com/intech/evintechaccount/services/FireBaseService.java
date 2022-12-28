//Import-------------------------------------------------------------------------------------------------------------------------------------

package com.intech.evintechaccount.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.intech.evintechaccount.models.Categories;
import com.intech.evintechaccount.models.Comment;
import com.intech.evintechaccount.models.Event;
import com.intech.evintechaccount.models.Message;
import com.intech.evintechaccount.models.User;
import com.intech.evintechaccount.models.UserInfo;


//-------------------------------------------------------------------------------------------------------------------------------------------


@Service
public class FireBaseService {


	//Enregister un utilisateur --------------------------------------------------------------------------------------------------------------

	public void registerUser(User user) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		dbFireStore.collection("Users").document(user.getUserId()).set(user);

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Acces a un compte ----------------------------------------------------------------------------------------------------------------------

	public User getUser(String userId) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		DocumentReference documentReference = dbFireStore.collection("Users").document(userId);
		ApiFuture<DocumentSnapshot> future = documentReference.get();

		DocumentSnapshot document = null;

		try {
			document = future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		User user = null;

		if(document.exists())
			user = document.toObject(User.class);

		return user;
	}

	//---------------------------------------

	public void updateUser(User user) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		dbFireStore.collection("Users").document(user.getUserId()).set(user);

		FirebaseAuth fireAuth = FirebaseAuth.getInstance();
		UserRecord userFireBase = null;
		try {
			userFireBase = fireAuth.getUser(user.getUserId());
		} catch (FirebaseAuthException e1) {
			e1.printStackTrace();
		}
		try {
			fireAuth.updateUser(new UpdateRequest(userFireBase.getUid()).setEmailVerified(true));
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}

	}

	//---------------------------------------

	public void deleteUser(String userId) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		FirebaseAuth fireAuth = FirebaseAuth.getInstance();
		try {
			fireAuth.deleteUser(userId);
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}

		dbFireStore.collection("Users").document(userId.toString()).delete();

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Acces a tous les comptes ---------------------------------------------------------------------------------------------------------------

	public List<User> getAllUser() throws InterruptedException, ExecutionException{

		Firestore dbFireStore = FirestoreClient.getFirestore();

		CollectionReference collectionReference = dbFireStore.collection("Users");

		List<User> users = new ArrayList<User>();

		if(collectionReference != null) {

			for (DocumentReference documentReference : collectionReference.listDocuments()) {
				ApiFuture<DocumentSnapshot> future = documentReference.get();
				DocumentSnapshot document = future.get();
				users.add(document.toObject(User.class));
			}
		}

		return users;
	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Verification de compte -----------------------------------------------------------------------------------------------------------------

	public boolean isVerifiedEmail(String UIDUser) throws FirebaseAuthException {

		FirebaseAuth fireAuth = FirebaseAuth.getInstance();

		UserRecord user = fireAuth.getUser(UIDUser);

		return user.isEmailVerified();

	}

	//---------------------------------------

	//Optimiser : ok

	public User getUserByEmail(String email) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		DocumentSnapshot documentSnapshotUser = null;

		Iterable<DocumentReference> collectionDocumentReference = dbFireStore.collection("Users").listDocuments();

		DocumentSnapshot document = null;

		for(DocumentReference documentReference : collectionDocumentReference){

			try {
				document = documentReference.get().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			if(document.get("email").equals(email))
			{
				documentSnapshotUser = document;
				break;
			}

		}

		User user = null;

		if(documentSnapshotUser != null)
			user = documentSnapshotUser.toObject(User.class);

		return user;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Donner les permissions -----------------------------------------------------------------------------------------------------------------

	public User getUserByName(String name) throws InterruptedException, ExecutionException{

		Firestore dbFireStore = FirestoreClient.getFirestore();

		DocumentSnapshot documentSnapshotUser = null;

		Iterable<DocumentReference> collectionDocumentReference = dbFireStore.collection("Users").listDocuments();

		for(DocumentReference documentReference : collectionDocumentReference){
			DocumentSnapshot document = documentReference.get().get();

			if(document.get("userName").equals(name))
				documentSnapshotUser = document;

		}

		User user = null;

		if(documentSnapshotUser.exists())
			user = documentSnapshotUser.toObject(User.class);

		return user;

	}

	public User getUserByUsername(UserInfo infos) throws InterruptedException, ExecutionException {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		DocumentSnapshot documentSnapshotUser = null;

		Iterable<DocumentReference> collectionDocumentReference = dbFireStore.collection("Users").listDocuments();

		for(DocumentReference documentReference : collectionDocumentReference){
			DocumentSnapshot document = documentReference.get().get();

			if(document.getId().equals(infos.getUid()))
				documentSnapshotUser = document;

		}

		User user = null;

		if(documentSnapshotUser != null)
			user = documentSnapshotUser.toObject(User.class);

		return user;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Ajouter un evenement -------------------------------------------------------------------------------------------------------------------

	public String addEvent(Event event) throws InterruptedException, ExecutionException {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		int id = 0;

		Iterable<CollectionReference> collectionReferences = dbFireStore.collection("Events").document("Categories").listCollections();
		for(CollectionReference collectionReference : collectionReferences){
			for(DocumentReference documentReference : collectionReference.listDocuments()) {
//				System.out.println(documentReference.get().get().getId());
				long idDocument = (long) documentReference.get().get().get("id");
				if(id < idDocument)
					id = (int) idDocument;
			}
		}

		if(id != 0)
			id++;

		event.setId(id);

		dbFireStore.collection("Events").document("Categories").collection(event.getCategory()).document(event.getEventName()).set(event);

		return "L'event " + event.getEventName() + " a bien ete cree";

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Obtenir les evenements-------------------------------------------------------------------------------------------------------------

	public List<Event> getAllEvent() throws InterruptedException, ExecutionException{

		Firestore dbFireStore = FirestoreClient.getFirestore();

		DocumentReference categorie = dbFireStore.collection("Events").document("Categories");

		List<Event> events = new ArrayList<Event>();

		if(categorie != null) {
			for (CollectionReference categorieName : categorie.listCollections()) {
				for(DocumentReference event : categorieName.listDocuments()) {
					ApiFuture<DocumentSnapshot> future = event.get();
					DocumentSnapshot document = future.get();
					events.add(document.toObject(Event.class));
				}
			}
		}

		return events;
	}

	//---------------------------------------

	public Event getEvent(String eventName) throws InterruptedException, ExecutionException {

		List<Event> events = getAllEvent();

		Event res = null;

		for(Event eventData : events)
		{
			if(eventData.getEventName().equals(eventName))
			{
				res = eventData;
			}
		}

		return res;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Modifier les evenements-----------------------------------------------------------------------------------------------------------------

	public String deleteEvent(String eventName) throws InterruptedException, ExecutionException {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		dbFireStore.collection("Events").document("Categories").collection(getEvent(eventName).getCategory()).document(eventName).delete();

		return "L'event " + eventName + " a bien ete supprime";

	}

	//---------------------------------------

	public String updateEvent(Event event) {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		dbFireStore.collection("Events").document("Categories").collection(event.getCategory()).document(event.getEventName()).set(event);

		return "L'event " + event.getEventName() + " a bien ete modifie";

	}

	//---------------------------------------

	public String modifyEvent(Event event, String eventname) throws InterruptedException, ExecutionException {

		deleteEvent(eventname);
		addEvent(event);

		return "L'event " + event.getEventName() + " a bien ete modifie";

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Systeme de participants-----------------------------------------------------------------------------------------------------------------

	public List<User> allParticipant(Event event) throws InterruptedException, ExecutionException {
		return getEvent(event.getEventName()).getParticipants();
	}

	public boolean addParticipant(Event event, User user) throws InterruptedException, ExecutionException {

		List<User> participants = allParticipant(event);
		boolean participate = false;
		for(User useradd : participants) {
			if(useradd.getEmail().equals(user.getEmail())) {
				participate = true;
				break;
			}
		}

		if(!participate){
			participants.add(user);

			Event eventchange = event;
			eventchange.setParticipants(participants);
			updateEvent(eventchange);

			return true;
		}

		return false;

	}

	//---------------------------------------

	public boolean removeParticipant(Event event, User user) throws InterruptedException, ExecutionException {

		boolean exist = false;

		List<User> participants = allParticipant(event);
		for(User userdelete : participants){
			if(userdelete.getEmail().equals(user.getEmail())) {
				exist = true;
				participants.remove(userdelete);
				break;
			}
		}

		Event eventchange = event;
		eventchange.setParticipants(participants);
		updateEvent(eventchange);

		return exist;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Systeme de like-------------------------------------------------------------------------------------------------------------------------

	public List<User> allLike(Event event) throws InterruptedException, ExecutionException {
		return getEvent(event.getEventName()).getLike();
	}

	public boolean like(Event event, User user) throws InterruptedException, ExecutionException {

		List<User> likers = allLike(event);
		boolean participate = false;
		for(User useradd : likers){
			if(useradd.getEmail().equals(user.getEmail())) {
				participate = true;
				break;
			}
		}

		if(!participate) {
			likers.add(user);

			Event eventchange = event;
			eventchange.setLike(likers);
			updateEvent(eventchange);

			return true;
		}

		return false;


	}

	//---------------------------------------

	public boolean removeLike(Event event, User user) throws InterruptedException, ExecutionException {

		boolean exist = false;

		List<User> likers = allLike(event);
		for(User userdelete : likers){
			if(userdelete.getEmail().equals(user.getEmail())) {
				exist = true;
				likers.remove(userdelete);
				break;
			}
		}

		Event eventchange = event;
		eventchange.setLike(likers);
		updateEvent(eventchange);

		return exist;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------


	//Systeme d'evenement favoris ------------------------------------------------------------------------------------------------------------

	public boolean addFavorite(Event event, User user) throws InterruptedException, ExecutionException, FirebaseAuthException {

		List<Event> favorites = user.getEventFavorites();
		boolean favorite = false;
		for(Event eventfavorite : favorites) {
			if(eventfavorite.getEventName().equals(event.getEventName())) {
				favorite = true;
				break;
			}
		}

		if(!favorite) {
			favorites.add(getEvent(event.getEventName()));

			User userchange = user;
			userchange.setEventFavorites(favorites);
			updateUser(user);

			return true;
		}

		return false;


	}

	//---------------------------------------

	public boolean removeFavorite(Event event, User user) throws InterruptedException, ExecutionException, FirebaseAuthException {

		boolean remove = false;

		List<Event> favorites = user.getEventFavorites();
		for(Event eventfavorite : favorites) {
			if(eventfavorite.getEventName().equals(event.getEventName())) {
				remove = true;
				favorites.remove(eventfavorite);
				break;
			}
		}

		if(remove)
		{
			User userchange = user;
			userchange.setEventFavorites(favorites);
			updateUser(userchange);
		}

		return remove;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------

	public List<Event> getCategory(String category) throws InterruptedException, ExecutionException {

		List<Event> events = getAllEvent();
		List<Event> res = new ArrayList<Event>();

		for(Event event : events)
		{
			if(event.getCategory().equals(category))
				res.add(event);
		}

		return res;
	}

	public int hightId(Event event) {

		int res = 0;

		List<Comment> comments = event.getComments();
		for(Comment comment : comments)
		{
			if(comment.getResponses().isEmpty() && comment.getId() > res)
				res = comment.getId();
			else if(!comment.getResponses().isEmpty() && comment.getResponses().get(comment.getResponses().size()-1).getId() > res)
				res = comment.getResponses().get(comment.getResponses().size()-1).getId();
		}

		return res + 1;

	}

	public String changePassword(User user) {

		String res = "";

		FirebaseAuth fireAuth = FirebaseAuth.getInstance();
		try {
			res = fireAuth.generatePasswordResetLink(user.getEmail());
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}

		return res;

	}

	@SuppressWarnings("unused")
	public void sendMessage(Message message) throws NumberFormatException, InterruptedException, ExecutionException {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		String sender = message.getSender().getUserId();
		String recipient = message.getRecipient().getUserId();
		int id = 0;
		int idTwo = 0;
		int idMessage = -1;

		Iterable<DocumentReference> collectionDocumentReference = dbFireStore.collection("Messages").document(sender + " " + recipient).collection("Messages").listDocuments();
		for(DocumentReference documentReference : collectionDocumentReference){
			id = Integer.parseInt(documentReference.get().get().getId()) + 1;
		}

		Iterable<DocumentReference> collectionDocumentReferenceTwo = dbFireStore.collection("Messages").document(recipient + " " + sender).collection("Messages").listDocuments();
		for(DocumentReference documentReference : collectionDocumentReferenceTwo){
			idTwo = Integer.parseInt(documentReference.get().get().getId()) + 1;
		}
		
		Iterable<DocumentReference> collectionDocumentReferenceBase = dbFireStore.collection("Messages").listDocuments();
		for(DocumentReference documentReferenceBase : collectionDocumentReferenceBase)
		{
			Iterable<DocumentReference> collectionDocumentReferenceConv = documentReferenceBase.collection("Messages").listDocuments();
			for(DocumentReference documentReferenceConv : collectionDocumentReferenceConv) {
				long idData = (long) documentReferenceConv.get().get().get("id");
				if(idMessage <= idData)
				{
					idMessage = (int) idData;
				}
			}
		}
		


		if(idTwo != 0)
		{
			message.setId(idMessage + 1);
			dbFireStore.collection("Messages").document(recipient + " " + sender).collection("Messages").document(String.valueOf(message.getId())).set(message);
		}
		else
		{
			if(idMessage == -1)
				message.setId(0);
			else
				message.setId(idMessage + 1);
			dbFireStore.collection("Messages").document(sender + " " + recipient).collection("Messages").document(String.valueOf(message.getId())).set(message);
		}


	}

	public List<Message> getMessage(User infos, User userActual) throws InterruptedException, ExecutionException {

		Firestore dbFireStore = FirestoreClient.getFirestore();

		List<Message> conv = new ArrayList<Message>();
		
		if(dbFireStore.collection("Messages").document(infos.getUserId() + " " + userActual.getUserId()).collection("Messages").get().get().size() > 0)
		{
			Iterable<DocumentReference> collectionDocumentReference = dbFireStore.collection("Messages").document(infos.getUserId() + " " + userActual.getUserId()).collection("Messages").listDocuments();
			for(DocumentReference documentReference : collectionDocumentReference){
				conv.add(documentReference.get().get().toObject(Message.class));
			}
			
			Collections.sort(conv, Comparator.comparing(s -> s.getId()));
		}
		
		else if(dbFireStore.collection("Messages").document(userActual.getUserId() + " " + infos.getUserId()).collection("Messages").get().get().size() > 0)
		{

			Iterable<DocumentReference> collectionDocumentReferenceTwo = dbFireStore.collection("Messages").document(userActual.getUserId() + " " + infos.getUserId()).collection("Messages").listDocuments();
			for(DocumentReference documentReference : collectionDocumentReferenceTwo){
				conv.add(documentReference.get().get().toObject(Message.class));
			}
			
			Collections.sort(conv, Comparator.comparing(s -> s.getId()));
		}
		

		return conv;

	}
	
	public List<String> getAllCategory() throws InterruptedException, ExecutionException {
		
		Firestore dbFireStore = FirestoreClient.getFirestore();
		
		List<String> res = new ArrayList<String>();
		
		Iterable<CollectionReference> collectionReferences = dbFireStore.collection("Events").document("Categories").listCollections();
		for(CollectionReference collectionReference : collectionReferences){
			res.add(collectionReference.getId());
		}
		
		return res;
		
	}

	public List<Event> getEventByManyCategories(Categories categories) throws InterruptedException, ExecutionException {
		
		Firestore dbFireStore = FirestoreClient.getFirestore();
		
		List<Event> res = new ArrayList<Event>();
		
		for(String category : categories.getCategories())
		{
			Iterable<DocumentReference> collectionReference = dbFireStore.collection("Events").document("Categories").collection(category).listDocuments();
			for(DocumentReference documentReference : collectionReference)
			{
				res.add(documentReference.get().get().toObject(Event.class));
			}
			
		}
		
		return res;
	}
	
	public List<Message> getAllConvUser(User user) throws InterruptedException, ExecutionException {
		
		Firestore dbFireStore = FirestoreClient.getFirestore();

		List<Message> convs = new ArrayList<Message>();

		Iterable<DocumentReference> collectionDocumentReferenceBase = dbFireStore.collection("Messages").listDocuments();
		for(DocumentReference documentReferenceBase : collectionDocumentReferenceBase)
		{
			if(documentReferenceBase.getId().contains(user.getUserId()))
			{
				Iterable<DocumentReference> collectionDocumentReferenceConv = documentReferenceBase.collection("Messages").listDocuments();
				for(DocumentReference documentReferenceConv : collectionDocumentReferenceConv) {
					Message res = documentReferenceConv.get().get().toObject(Message.class);
					convs.add(res);
				}
			}

		}
		
		return convs;
		
	}
	
	public void openConv(User user, String otherUser) throws InterruptedException, ExecutionException {
		
		Firestore dbFireStore = FirestoreClient.getFirestore();
		
		List<Message> messages = getMessage(getUser(otherUser), user);
		for(Message message : messages)
		{
			message.setSeen(true);
			String sender = message.getSender().getUserId();
			String recipient = message.getRecipient().getUserId();
			int idTwo = 0;

			Iterable<DocumentReference> collectionDocumentReferenceTwo = dbFireStore.collection("Messages").document(recipient + " " + sender).collection("Messages").listDocuments();
			for(DocumentReference documentReference : collectionDocumentReferenceTwo){
				idTwo = Integer.parseInt(documentReference.get().get().getId()) + 1;
			}
			
			if(idTwo != 0)
			{
				dbFireStore.collection("Messages").document(recipient + " " + sender).collection("Messages").document(String.valueOf(message.getId())).set(message);
			}
			else
			{
				dbFireStore.collection("Messages").document(sender + " " + recipient).collection("Messages").document(String.valueOf(message.getId())).set(message);
			}
		}
		
	}

}

package com.intech.evintechaccount.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;
import com.intech.evintechaccount.config.JwtConfig;
import com.intech.evintechaccount.models.Categories;
import com.intech.evintechaccount.models.Comment;
import com.intech.evintechaccount.models.Event;
import com.intech.evintechaccount.models.NameEvent;
import com.intech.evintechaccount.models.Order;
import com.intech.evintechaccount.models.User;
import com.intech.evintechaccount.models.UserInfo;
import com.intech.evintechaccount.services.FireBaseService;
import com.intech.evintechaccount.services.PaypalService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;


@RestController
@RequestMapping("api/events")
public class EventController {

	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";

	@Autowired
	FireBaseService firebaseService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	SecretKey secretKey;

	@Autowired
	PaypalService service;

	public User getUserActual(HttpServletRequest request) throws InterruptedException, ExecutionException{

		String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

		if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
			return null;
		}

		String username = null;

		String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");

		try {

			@SuppressWarnings("deprecation")
			Jws<Claims> claimsJws = Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token);

			Claims body = claimsJws.getBody();

			username = body.getSubject();

		} catch (JwtException e) {
			throw new IllegalStateException("Token " + token + " can't be trust");
		}

		return firebaseService.getUserByEmail(username);

	}


	@GetMapping("/participate/{eventName}")
	@PreAuthorize("hasAuthority('participateEvent')")
	public ResponseEntity<?> participateEvent(@PathVariable String eventName , HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean noParticipate = firebaseService.addParticipant(event, user);

		if(!noParticipate)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@DeleteMapping("/participate/{eventName}")
	@PreAuthorize("hasAuthority('participateEvent')")
	public ResponseEntity<?> cancelParticipateEvent(@PathVariable String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean exist = firebaseService.removeParticipant(event, user);

		if(!exist)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@GetMapping("/attendees/{eventName}")
	public List<User> getAllParticipant(@PathVariable("eventName") String eventName) throws InterruptedException, ExecutionException {

		return firebaseService.allParticipant(firebaseService.getEvent(eventName));

	}

	@GetMapping("/likes/{eventName}")
	public List<User> getAllLikes(@PathVariable("eventName") String eventName) throws InterruptedException, ExecutionException {

		return firebaseService.allLike(firebaseService.getEvent(eventName));

	}

	@GetMapping("/like/{eventName}")
	@PreAuthorize("hasAuthority('likeEvent')")
	public ResponseEntity<?> likeEvent(@PathVariable String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean nolike = firebaseService.like(event, user);

		if(!nolike)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@DeleteMapping("/like/{eventName}")
	@PreAuthorize("hasAuthority('likeEvent')")
	public ResponseEntity<?> cancelLikeEvent(@PathVariable String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean exist = firebaseService.removeLike(event, user);

		if(!exist)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@PostMapping(path = "/comment/{eventName}")
	@PreAuthorize("hasAuthority('writeComment')")
	public ResponseEntity<?> addNewComment(@RequestBody Comment comment, @PathVariable("eventName") String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		if(comment.getContent() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		comment.setUser(user);

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");  
		String strDate = dateFormat.format(date);  

		comment.setDate(strDate);
		comment.setResponse(false);

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		if(!event.getComments().isEmpty())
		{
			int id = 0;

			for(Comment commentExist : event.getComments())
			{
				id = id + commentExist.getResponses().size() + 1;
			}

			//int id = event.getComments().get(event.getComments().size() - 1).getId() + 1;


			comment.setId(id);
			comment.setIdParent(id);
		}
		else
		{
			comment.setId(0);
			comment.setIdParent(0);
		}

		event.getComments().add(comment);

		firebaseService.updateEvent(event);

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@DeleteMapping(path = "/comment/{eventName}/{id}")
	@PreAuthorize("hasAuthority('deleteHisComment')")
	public ResponseEntity<?> deleteComment(@PathVariable("eventName") String eventName, @PathVariable("id") int id, HttpServletRequest request) throws InterruptedException, ExecutionException{

		boolean isResponse = false;
		int idParent = 0;

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		Comment comment = null;

		for(Comment commentDelete : event.getComments())
		{
			if(commentDelete.getId() == id)
				comment = commentDelete;
			for(Comment response : commentDelete.getResponses())
			{
				if(response.getId() == id)
				{
					comment = response;
					idParent = response.getIdParent();
					isResponse = true;
				}
			}
		}

		if(comment == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		if(!comment.getUser().getEmail().equals(user.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		if(!isResponse)
			event.getComments().remove(comment);
		else
		{
			event.getComments().get(idParent).getResponses().remove(comment);
		}

		firebaseService.updateEvent(event);

		return ResponseEntity.status(HttpStatus.OK).build();

	}



	@PostMapping
	@PreAuthorize("hasAuthority('createEvent')")
	public ResponseEntity<?> addNewEvent(@RequestBody Event event, HttpServletRequest request) throws InterruptedException, ExecutionException{

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");  
		String strDate = dateFormat.format(date);

		event.setDateCreation(strDate);

		if(firebaseService.getEvent(event.getEventName()) != null)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		event.setCreator(user);

		if(event.getCategory() == null || event.getEventName() == null || event.getDescription() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		firebaseService.addEvent(event);

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@DeleteMapping(path = "{eventName}")
	@PreAuthorize("hasAuthority('deleteHisEvent')")
	public ResponseEntity<?> deleteEvent(@PathVariable("eventName") String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		if(firebaseService.getEvent(eventName) == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if(!firebaseService.getEvent(eventName).getCreator().getEmail().equals(user.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		firebaseService.deleteEvent(eventName);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@PutMapping(path = "{eventName}")
	@PreAuthorize("hasAuthority('modifyHisEvent')")
	public ResponseEntity<?> updateEvent(@PathVariable("eventName") String eventName, @RequestBody Event event , HttpServletRequest request) throws InterruptedException, ExecutionException {

		if(event.getCategory() == null || event.getEventName() == null || event.getDescription() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		if(firebaseService.getEvent(eventName) == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if(!firebaseService.getEvent(eventName).getCreator().getEmail().equals(user.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		event.setCreator(user);
		event.setComments(firebaseService.getEvent(eventName).getComments());
		event.setLike(firebaseService.getEvent(eventName).getLike());
		event.setParticipants(firebaseService.getEvent(eventName).getParticipants());
		event.setDateCreation(firebaseService.getEvent(eventName).getDateCreation());

		firebaseService.modifyEvent(event, eventName);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@DeleteMapping(path = "/admin/{eventName}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> deleteEventAdmin(@PathVariable("eventName") String eventName) throws InterruptedException, ExecutionException {

		if(firebaseService.getEvent(eventName) == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		firebaseService.deleteEvent(eventName);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@GetMapping("/category/{categoryName}")
	public List<Event> getCategory(@PathVariable("categoryName") String categoryName) throws InterruptedException, ExecutionException {

		return firebaseService.getCategory(categoryName);

	}

	@GetMapping("/{eventName}")
	public Event getEvent(@PathVariable("eventName") String eventName) throws InterruptedException, ExecutionException {
		Event event = firebaseService.getEvent(eventName);
		User creator = event.getCreator();
		creator.setEmail("");
		creator.setEventFavorites(new ArrayList<Event>());
		creator.setPassword("");
		creator.setRole("");
		creator.setBiography("");
		event.setCreator(creator);
		return event;

	}

	@PostMapping("/pay")
	public String payment(@RequestBody Order order) {

		try {
			Payment payment = service.createPayment(order.getPrice(), order.getDescription(), "http://localhost:8080/api/events/" + CANCEL_URL,
					"http://localhost:8080/api/events/" + SUCCESS_URL);

			for (Links link : payment.getLinks()) {

				System.out.println(link);

				if (link.getRel().equals("approval_url")) {
					return "redirect:" + link.getHref();
				}
			}

		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	@GetMapping(value = SUCCESS_URL)
	public RedirectView successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) throws InterruptedException, ExecutionException {

		RedirectView redirectView = new RedirectView();

		try {
			Payment payment = service.executePayment(paymentId, payerId);
			if (payment.getState().equals("approved")) {
				String[] infos = payment.getTransactions().get(0).getDescription().split("/");
				User user = firebaseService.getUserByUsername(new UserInfo(infos[1]));
				Event event = firebaseService.getEvent(infos[0]);
				firebaseService.addParticipant(event, user);
				redirectView.setUrl("http://localhost:3000/successPay");
				return redirectView; 
			}

		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}

		redirectView.setUrl("http://localhost:3000/errorPay");

		return redirectView;
	}

	@GetMapping()
	public List<Event> getAllEvent() throws InterruptedException, ExecutionException {

		return firebaseService.getAllEvent();

	}

	@PostMapping("/isParticipate")
	public boolean isParticipate(@RequestBody NameEvent eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		boolean res = false;

		User userActual = getUserActual(request);

		Event event = firebaseService.getEvent(eventName.getContent());

		List<User> users = event.getParticipants();

		for(User user : users)
		{
			if(userActual.getEmail().equals(user.getEmail()))
			{
				res = true;
			}
		}

		return res;	
	}

	@PostMapping("/isLike")
	public boolean isLike(@RequestBody NameEvent eventName, HttpServletRequest request) throws InterruptedException, ExecutionException {

		boolean res = false;

		User userActual = getUserActual(request);

		Event event = firebaseService.getEvent(eventName.getContent());

		List<User> users = event.getLike();

		for(User user : users)
		{
			if(userActual.getEmail().equals(user.getEmail()))
			{
				res = true;
			}
		}

		return res;	
	}

	@PostMapping("/research")
	public List<Event> research(@RequestBody NameEvent eventName) throws InterruptedException, ExecutionException {

		List<Event> res = new ArrayList<Event>();

		List<Event> events = firebaseService.getAllEvent();

		for(Event event : events) {
			if(event.getEventName().toLowerCase().contains(eventName.getContent().toLowerCase()) 
					|| event.getCategory().toLowerCase().contains(eventName.getContent().toLowerCase()) 
					|| event.getDescription().toLowerCase().contains(eventName.getContent().toLowerCase()))
				res.add(event);	
		}

		return res;

	}

	@GetMapping(path = "/comment/{eventName}")
	public List<Comment> getComment(@PathVariable("eventName") String eventName) throws InterruptedException, ExecutionException {

		return firebaseService.getEvent(eventName).getComments();

	}

	@PostMapping(path = "/response/{eventName}/{id}")
	@PreAuthorize("hasAuthority('writeComment')")
	public ResponseEntity<?> responseComment(@RequestBody Comment comment, @PathVariable("eventName") String eventName, @PathVariable("id") int id, HttpServletRequest request) throws InterruptedException, ExecutionException {

		boolean found = false;

		if(comment.getContent() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		User user = getUserActual(request);

		if(user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		comment.setUser(user);

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");  
		String strDate = dateFormat.format(date);  

		comment.setDate(strDate);

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		Comment commentexist = null;

		for(Comment commentActual : event.getComments())
		{

			//Si l'id correspond a un commentaire isole

			if(commentActual.getId() == id)
			{
				commentexist = commentActual;
				commentexist.setResponse(true);
				break;
			}

			//Si l'id correspond a une reponse

			for(Comment response : commentActual.getResponses())
			{
				if(response.getId() == id)
				{
					commentexist = response;
					commentexist.setResponse(true);
					commentexist.setIdParent(response.getIdParent());

					found = true;
					break;
				}
			}

			if(found)
				break;
		}

		if(commentexist == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		//Si le commentaire est deja une reponse

		if(commentexist.isResponse())
		{
			for(Comment commentActual : event.getComments())
			{
				if(commentActual.getId() == commentexist.getIdParent())
				{
					comment.setIdParent(commentexist.getIdParent());
					if(commentActual.getResponses().size() == 0)
						comment.setId(id+1);
					else
						comment.setId(firebaseService.hightId(event));

					List<Comment> comments = commentActual.getResponses();
					comments.add(comment);
					commentActual.setResponses(comments);
					break;
				}

			}
		}

		//Sinon

		else
		{
			comment.setIdParent(id);
			comment.setId(id + 1);
			commentexist.getResponses().add(comment);
		}


		firebaseService.updateEvent(event);

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@GetMapping("/dateSort")
	public List<Event> eventSort() throws InterruptedException, ExecutionException {

		List<Event> events = firebaseService.getAllEvent();

		Collections.sort(events, Comparator.comparing(s -> s.getId()));

		return events;
	}
	
	@GetMapping("/category")
	public List<String> getAllCategory() throws InterruptedException, ExecutionException {
		return firebaseService.getAllCategory();
	}
	
	@PostMapping("/category/manyCategory")
	public List<Event> getEventsByManyCategories(@RequestBody Categories categories) throws InterruptedException, ExecutionException {
		
		return firebaseService.getEventByManyCategories(categories);
		
	}

}

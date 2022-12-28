//Imports --------------------------------------------------------------------------------------------------------------------------------------

package com.intech.evintechaccount.controllers;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuthException;
import com.intech.evintechaccount.config.JwtConfig;
import com.intech.evintechaccount.models.Event;
import com.intech.evintechaccount.models.JwtToken;
import com.intech.evintechaccount.models.LoginRequest;
import com.intech.evintechaccount.models.Message;
import com.intech.evintechaccount.models.NameEvent;
import com.intech.evintechaccount.models.User;
import com.intech.evintechaccount.models.UserInfo;
import com.intech.evintechaccount.services.ApplicationUserService;
import com.intech.evintechaccount.services.FireBaseService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

//----------------------------------------------------------------------------------------------------------------------------------------------


//Initialisation de la classe ------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping("api/users")

//-------------------------------------------------------------------

public class UserController {

	@Autowired
	FireBaseService firebaseService;

	@Autowired
	ApplicationUserService userService;

	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	SecretKey secretKey;

	@Autowired
	AuthenticationManager authenticationManager;

	//----------------------------------------------------------------------------------------------------------------------------------------------

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

	@PostMapping("/signin")
	public String authenticateUser(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = Jwts.builder()
				.setSubject(authentication.getName())
				.claim("authorities", authentication.getAuthorities())
				.setIssuedAt(new Date())
				.setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
				.signWith(secretKey)
				.compact();

		return token;

	}

	//Creer un compte ------------------------------------------------------------------------------------------------------------------------------

	@PostMapping(path = "/visitor")
	public ResponseEntity<?> registerNewVisitor(@RequestBody User user) throws InterruptedException, ExecutionException {

		if(user.getUserId() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		if(user.getEmail() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		if(user.getUserName() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		if(user.getUserFirstName() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		boolean existUser = userService.addVisitor(user);

		if(existUser)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------	


	//Verifier le compte ---------------------------------------------------------------------------------------------------------------------------

	@GetMapping(path = "/verify/{userId}")
	public boolean isVerified(@PathVariable("userId") String userId) throws FirebaseAuthException {

		return firebaseService.isVerifiedEmail(userId);

	}

	//-------------------------------------------------------------------

	@GetMapping(path = "/student/{email}/**")
	public RedirectView studentVerified(@PathVariable("email") String email) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User user = firebaseService.getUserByEmail(email);

		RedirectView redirectView = new RedirectView();

		if(user == null)
		{
			redirectView.setUrl("http://localhost:3000/signin");
			return redirectView;
		}

		userService.setStudent(user);

		redirectView.setUrl("http://localhost:3000/signin");
		return redirectView;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------


	//Donner les roles -----------------------------------------------------------------------------------------------------------------------------

	@GetMapping(path = "/giveRole/student/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> setStudent(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException, FirebaseAuthException  {

		User user = firebaseService.getUser(userId);

		if(user == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		userService.setStudent(user);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//-------------------------------------------------------------------

	@GetMapping(path = "/giveRole/organizer/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> setOrganizer(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User user = firebaseService.getUser(userId);

		if(user == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		userService.setOrganizer(user);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//-------------------------------------------------------------------

	@GetMapping(path = "/giveRole/admin/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> setAdmin(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User user = firebaseService.getUser(userId);

		if(user == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		userService.setAdmin(user);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------


	//Recuperer informations -----------------------------------------------------------------------------------------------------------------------

	@GetMapping
	public List<User> getAllUser() throws InterruptedException, ExecutionException {

		return firebaseService.getAllUser();

	}

	//-------------------------------------------------------------------

	@GetMapping(path = "{userId}")
	public User getStudentDetails(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException {

		return firebaseService.getUser(userId);

	}

	@GetMapping(path = "/information")
	public User getActualUser(HttpServletRequest request) throws InterruptedException, ExecutionException {

		return getUserActual(request);

	}


	//----------------------------------------------------------------------------------------------------------------------------------------------


	//Modifier son compte --------------------------------------------------------------------------------------------------------------------------

	@DeleteMapping(path = "{userId}")
	public ResponseEntity<?> deleteAccount(@PathVariable("userId") String userId, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {

		System.out.println("delete account");

		User useractual = getUserActual(request);

		User user = firebaseService.getUser(userId);

		if(user == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if(!useractual.getEmail().equals(user.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		userService.deleteUser(user);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//-------------------------------------------------------------------

	@PutMapping(path = "{userId}")
	public ResponseEntity<?> updateHisAccount(@PathVariable("userId") String userId, @RequestBody User user, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User useractual = getUserActual(request);

		User userdata = firebaseService.getUser(userId);

		if(userdata == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if(!useractual.getEmail().equals(userdata.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		user.setUserId(userId);
		user.setEmail(userdata.getEmail());
		user.setRole(userdata.getRole());

		if(user.getEventFavorites() == null)
			user.setEventFavorites(userdata.getEventFavorites());

		if(user.getPassword() == null)
			user.setPassword(userdata.getPassword());

		if(user.getUserName() == null)
			user.setUserName(userdata.getUserName());

		if(user.getUserFirstName() == null)
			user.setUserFirstName(userdata.getUserFirstName());

		if(user.getBiography() == null)
			user.setBiography("");

		if(user.getProfilePicture() == null)
			user.setUrlProfilePicture("");

		userService.updateUser(user, userdata);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------


	//Partie admin ---------------------------------------------------------------------------------------------------------------------------------

	@DeleteMapping(path = "/admin/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> deleteStudent(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User user = firebaseService.getUser(userId);

		if(user == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		userService.deleteUser(user);

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------

	@GetMapping("/favorites/{eventName}")
	@PreAuthorize("hasAuthority('likeEvent')")
	public ResponseEntity<?> addFavorite(@PathVariable("eventName") String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User useractual = getUserActual(request);

		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		User user = firebaseService.getUser(useractual.getUserId());

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean noFavori = firebaseService.addFavorite(event, user);

		if(!noFavori)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@DeleteMapping("/favorites/{eventName}")
	@PreAuthorize("hasAuthority('likeEvent')")
	public ResponseEntity<?> removeFavorite(@PathVariable("eventName") String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {

		User useractual = getUserActual(request);

		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		User user = firebaseService.getUser(useractual.getUserId());

		Event event = firebaseService.getEvent(eventName);

		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		boolean exist = firebaseService.removeFavorite(event, user);

		if(!exist)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@PostMapping("/signInGoogle")
	public ResponseEntity<?> signInWithGoogle(@RequestBody JwtToken jwtToken) throws UnsupportedEncodingException, InterruptedException, ExecutionException {

		String[] pieces = jwtToken.getContent().split("\\.", -1);

		String b64payload = pieces[1];

		String jsonString = new String(Base64.getDecoder().decode(b64payload), "UTF-8");

		String[] element = jsonString.split(",");

		String[] output = {element[0].substring(9,element[0].length()-1),element[1].substring(11,element[1].length()-1),element[5].substring(11,element[5].length()-1),element[9].substring(9,element[9].length()-1)};
		String[] names = output[0].split(" ");

		User user = new User();

		user.setUserFirstName(names[0]);
		user.setUserName(names[1]);
		user.setUserId(output[2]);
		user.setEmail(output[3]);
		user.setBiography("");
		user.setPassword("");
		user.setEventFavorites(new ArrayList<Event>());

		boolean existUser = userService.addVisitor(user);

		if(existUser)
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		userService.setStudent(user);

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestBody LoginRequest identifiants) {

		return firebaseService.changePassword(firebaseService.getUserByEmail(identifiants.getUsername()));

	}

	@PostMapping("/sendMessage")
	public ResponseEntity<?> sendMessage(HttpServletRequest request, @RequestBody UserInfo infos) throws InterruptedException, ExecutionException {


		if(infos.getUid() == null || infos.getContent() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		User userActual = getActualUser(request);

		if(userActual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		User recipient = firebaseService.getUserByUsername(infos);

		if(recipient == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		if(userActual.getEmail().equals(recipient.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).build();

		Message message = new Message();

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm | dd-MM-yyyy");  
		String strDate = dateFormat.format(date);

		message.setSender(userActual);
		message.setRecipient(recipient);
		message.setDate(strDate);
		message.setContent(infos.getContent());
		message.setSeen(false);

		firebaseService.sendMessage(message);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/message/{user}")
	public List<Message> getConv(HttpServletRequest request, @PathVariable("user") String infos) throws InterruptedException, ExecutionException {

		return firebaseService.getMessage(firebaseService.getUser(infos), getActualUser(request));

	}

	@PostMapping("/isFavorite")
	public boolean isFavorite(HttpServletRequest request, @RequestBody NameEvent eventName) throws InterruptedException, ExecutionException {

		boolean res = false;

		User user = getActualUser(request);

		List<Event> favorites = user.getEventFavorites();

		for(Event event : favorites)
		{
			if(event.getEventName().equals(eventName.getContent()))
				res = true;
		}

		return res;

	}

	@GetMapping("/getFavorites/{id}")
	public List<Event> getFav(@PathVariable("id") String id) {

		return firebaseService.getUser(id).getEventFavorites();
	}

	@GetMapping("/getConvByDate")
	public List<User> getConvByDate(HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getActualUser(request);

		List<Message> convs = firebaseService.getAllConvUser(user);

		Collections.sort(convs, Comparator.comparing(s -> s.getId()));

		List<User> res = new ArrayList<User>();
		List<String> validate = new ArrayList<String>();

		for(int i = convs.size() - 1; i >= 0; i --)
		{
			if(convs.get(i).getSender().getUserId().equals(user.getUserId()))
			{
				if(!validate.contains(convs.get(i).getRecipient().getUserId()))
				{
					
					validate.add(convs.get(i).getRecipient().getUserId());
					res.add(convs.get(i).getRecipient());
				}
			}
			else
			{
				if(!validate.contains(convs.get(i).getSender().getUserId()))
				{
					
					validate.add(convs.get(i).getSender().getUserId());
					res.add(convs.get(i).getSender());
				}
			}
		}

		List<User> allUser = firebaseService.getAllUser();

		for(int i = 0; i < allUser.size(); i++)
		{
			if(!validate.contains(allUser.get(i).getUserId()) && !allUser.get(i).getUserId().equals(user.getUserId()))
			{
				validate.add(validate.size(), allUser.get(i).getUserId());
				res.add(res.size(), allUser.get(i));
			}
		}

		return res;

	}

	@GetMapping("/openMessage/{uid}")
	public void openMessage(@PathVariable("uid") String id, HttpServletRequest request) throws InterruptedException, ExecutionException {

		User user = getActualUser(request);

		List<Message> messages = getConv(request, id);

		for(Message message : messages)
		{
			message.setSeen(true);
		}

		firebaseService.openConv(user, id);

	}

	@GetMapping("/isOpenMessage/{uid}")
	public boolean isOpenMessage(@PathVariable("uid") String id, HttpServletRequest request) throws InterruptedException, ExecutionException {

		boolean res = true;

		List<Message> messages = getConv(request, id);

		for(Message message : messages)
		{
			if(!message.isSeen())
			{
				res = false;
				break;
			}

		}

		return res;

	}
	
	@GetMapping("/lastMessage/{uid}")
	public Message lastMessage(@PathVariable("uid") String id, HttpServletRequest request) throws InterruptedException, ExecutionException {

		List<Message> messages = getConv(request, id);
		
		if(!messages.isEmpty())
			return messages.get(messages.size() - 1);
		
		return null;

	}

}


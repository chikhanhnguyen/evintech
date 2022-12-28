package com.intech.evintechaccount.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuthException;
import com.intech.evintechaccount.config.JwtConfig;
import com.intech.evintechaccount.models.Event;
import com.intech.evintechaccount.models.User;
import com.intech.evintechaccount.services.FireBaseService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("api/image")
public class ImageController {
	
	@Autowired
	FireBaseService firebaseService;
	
	@Autowired
	JwtConfig jwtConfig;

	@Autowired
	SecretKey secretKey;
	
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
	
	@PostMapping("/user")
	public ResponseEntity<?> addImageUser(@RequestParam MultipartFile file,HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {
		
		User useractual = getUserActual(request);

		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		if(filename.contains(".."))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
		
		try {
			useractual.setUrlProfilePicture(Base64.getEncoder().encodeToString(file.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println(file.getBytes().length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		firebaseService.updateUser(useractual);
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	

	@PostMapping("/event/{eventName}")
	public ResponseEntity<?> addImageEvent(@PathVariable("eventName") String eventName, @RequestParam MultipartFile file, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {
		
		User useractual = getUserActual(request);
		
		if(useractual == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		Event event = firebaseService.getEvent(eventName);
		
		if(event == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
		User user = event.getCreator();
		
		if(!useractual.getEmail().equals(user.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		if(filename.contains(".."))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
		
		try {
			event.setUrlImage(Base64.getEncoder().encodeToString(file.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		firebaseService.updateEvent(event);
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	
	@GetMapping("/user/{userId}")
	public byte[] getImageUser(@PathVariable("userId") String userId) throws InterruptedException, ExecutionException, FirebaseAuthException {
		
		User user = firebaseService.getUser(userId);

		if(user == null)
			return null;
		
		String pictureEncode = user.getProfilePicture();
		
		if(pictureEncode == "")
			return new byte[0];
		
		byte[] image = null;
		
		image = Base64.getDecoder().decode(pictureEncode);
		
		return image;
		
	}
	
	@GetMapping("/event/{eventName}")
	public byte[] getImageEvent(@PathVariable("eventName") String eventName, HttpServletRequest request) throws InterruptedException, ExecutionException, FirebaseAuthException {
		
		Event event = firebaseService.getEvent(eventName);
		
		if(event == null)
			return null;
		
		String pictureEncode = event.getUrlImage();
		
		if(pictureEncode == "")
			return new byte[0];
		
		byte[] image = null;
		
		image = Base64.getDecoder().decode(pictureEncode);
		
		return image;
		
	}

}

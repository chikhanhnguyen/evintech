//Import------------------------------------------------------------------------------------------------------------

package com.intech.evintechaccount.services;

import java.io.FileInputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

//------------------------------------------------------------------------------------------------------------------

@Service
public class FireBaseInitialize {
	
	
//Initialisation de FireBase----------------------------------------------------------------------------------------
	
	@PostConstruct
	public void initialize() {
		
		FileInputStream serviceAccount;
		
		try {
			
			serviceAccount = new FileInputStream("./serviceAccount.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://evintechv2-default-rtdb.europe-west1.firebasedatabase.app")
					  .build();

			FirebaseApp.initializeApp(options);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//------------------------------------------------------------------------------------------------------------------

}

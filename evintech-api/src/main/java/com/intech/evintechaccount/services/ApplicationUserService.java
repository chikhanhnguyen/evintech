package com.intech.evintechaccount.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intech.evintechaccount.models.ApplicationUser;
import com.intech.evintechaccount.models.Comment;
import com.intech.evintechaccount.models.Event;
import com.intech.evintechaccount.models.User;
import com.intech.evintechaccount.security.ApplicationUserRole;

@Service
public class ApplicationUserService implements UserDetailsService {

	@Autowired
	FireBaseService firebaseService;

	private PasswordEncoder passwordEncoder;

	public ApplicationUserService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public UserDetails loadUserByUsername(String username){

		User user = firebaseService.getUserByEmail(username);

		return new ApplicationUser(user.getEmail(), 
				passwordEncoder.encode(user.getPassword()), 
				ApplicationUserRole.valueOf(user.getRole()).getGrantedAuthorities(), 
				true, 
				true, 
				true, 
				true);
	}

	public boolean addVisitor(User visitor) {

		boolean userExist = false;

		if(firebaseService.getUserByEmail(visitor.getEmail()) != null || firebaseService.getUser(visitor.getUserId()) != null)
			userExist = true;

		if(!userExist) {
			visitor.setRole("VISITOR");
			visitor.setBiography("");
			visitor.setEventFavorites(new ArrayList<Event>());
			visitor.setUrlProfilePicture("");
			firebaseService.registerUser(visitor);
		}

		return userExist;

	}

	public void setStudent(User student) {

		student.setRole("STUDENT");
		firebaseService.updateUser(student);

	}

	public void setOrganizer(User organizer) {

		organizer.setRole("ORGANIZER");
		firebaseService.updateUser(organizer);

	}

	public void setAdmin(User admin) {

		admin.setRole("ADMIN");
		firebaseService.updateUser(admin);

	}

	public void deleteUser(User user) throws InterruptedException, ExecutionException {

		List<Event> events = firebaseService.getAllEvent();

		boolean modif = false;

		for(Event event : events)
		{

			if(event.getCreator().getEmail().equals(user.getEmail()))
			{
				firebaseService.deleteEvent(event.getEventName());
				continue;
			}

			for(User participate : event.getParticipants())
			{
				if(participate.getEmail().equals(user.getEmail()))
				{
					firebaseService.removeParticipant(event, user);
					modif = true;
					break;
				}
			}

			for(User like : event.getLike())
			{
				if(like.getEmail().equals(user.getEmail()))
				{
					firebaseService.removeLike(event, user);
					modif = true;
					break;
				}
			}

			List<Comment> comments = event.getComments();
			List<Comment> responses;
			boolean commentDelete = false;

			if(!comments.isEmpty())
			{
				int i = 0;
				while(i < comments.size())
				{
					if(comments.get(i).getUser().getEmail().equals(user.getEmail()))
					{
						event.getComments().remove(comments.get(i));
						modif = true;
						if(event.getComments().isEmpty())
						{
							break;
						}
						commentDelete = true;
					}

					if(!commentDelete)
					{
						responses = comments.get(i).getResponses();

						if(!responses.isEmpty())
						{
							int j = 0;
							while(j < responses.size())
							{
								if(responses.get(j).getUser().getEmail().equals(user.getEmail()))
								{
									comments.get(i).getResponses().remove(responses.get(j));
									modif = true;
									if(comments.get(i).getResponses().isEmpty())
									{
										break;
									}
								}

								else
									j++;
							}
						}
						i++;
					}

				}

			}

			if(modif)
			{
				firebaseService.updateEvent(event);
				modif = false;
			}
		}

		firebaseService.deleteUser(user.getUserId());

	}

	public void updateUser(User user, User oldUser) throws InterruptedException, ExecutionException {

		List<Event> events = firebaseService.getAllEvent();

		boolean modif = false;

		for(Event event : events)
		{

			if(event.getCreator().getEmail().equals(oldUser.getEmail()))
			{
				event.setCreator(user);
				modif = true;
			}
			
			List<User> participants = event.getParticipants();
			
			for(int i = 0; i < participants.size(); i ++)
			{
				if(participants.get(i).getEmail().equals(oldUser.getEmail()))
				{
					participants.remove(i);
					participants.add(i, user);
					modif = true;
					break;
				}
			}
			
			List<User> likes = event.getLike();
			
			for(int i = 0; i < likes.size(); i++)
			{
				if(likes.get(i).getEmail().equals(user.getEmail()))
				{
					likes.remove(i);
					likes.add(i,user);
					modif = true;
					break;
				}
			}

			List<Comment> comments = event.getComments();
			List<Comment> responses;

			if(!comments.isEmpty())
			{
				int i = 0;
				while(i < comments.size())
				{
					if(comments.get(i).getUser().getEmail().equals(user.getEmail()))
					{
						comments.get(i).setUser(user);
						modif = true;
					}

					responses = comments.get(i).getResponses();

					if(!responses.isEmpty())
					{
						int j = 0;
						
						while(j < responses.size())
						{
							if(responses.get(j).getUser().getEmail().equals(user.getEmail()))
							{
								comments.get(i).getResponses().get(j).setUser(user);
								modif = true;

							}
							j++;
						}
					}
					
					i++;

				}

			}

			if(modif)
			{
				firebaseService.updateEvent(event);
				modif = false;
			}
		}

		firebaseService.updateUser(user);

	}

}

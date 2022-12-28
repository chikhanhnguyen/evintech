package com.intech.evintechaccount.models;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class ApplicationUser implements UserDetails {
	
	private Set<? extends GrantedAuthority> grantedAuthorithies;
	
	private final String username;
	private String password;
	
	private final boolean isAccountNonExpired;
	private final boolean isAccountNonLocked;
	private final boolean isCredentialsNonExpired;
	private final boolean isEnabled;
	
	public ApplicationUser(	String username, 
							String password,
							Set<? extends GrantedAuthority> grantedAuthorithies,
							boolean isAccountNonExpired, 
							boolean isAccountNonLocked, 
							boolean isCredentialsNonExpired,
							boolean isEnabled) {

		this.username = username;
		this.password = password;
		this.grantedAuthorithies = grantedAuthorithies;
		this.isAccountNonExpired = isAccountNonExpired;
		this.isAccountNonLocked = isAccountNonLocked;
		this.isCredentialsNonExpired = isCredentialsNonExpired;
		this.isEnabled = isEnabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorithies;
	}

	public void setGrantedAuthorithies(Set<? extends GrantedAuthority> grantedAuthorithies) {
		this.grantedAuthorithies = grantedAuthorithies;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

}

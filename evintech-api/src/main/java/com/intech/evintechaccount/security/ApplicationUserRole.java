package com.intech.evintechaccount.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;

import static com.intech.evintechaccount.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
	VISITOR(Sets.newHashSet()),
	STUDENT(Sets.newHashSet(WRITE_COMMENT, MODIFY_HIS_COMMENT, DELETE_HIS_COMMENT, WRITE_MESSAGE, PARTICIPATE, LIKE, PAY)),
	ORGANIZER(Sets.newHashSet(WRITE_COMMENT, MODIFY_HIS_COMMENT, DELETE_HIS_COMMENT, WRITE_MESSAGE, PARTICIPATE, LIKE, PAY, SEE_ATTENDEES, CREATE_EVENT, CREATE_CATEGORY, DELETE_HIS_EVENT, MODIFY_HIS_EVENT)),
	ADMIN(Sets.newHashSet(WRITE_COMMENT, MODIFY_HIS_COMMENT, DELETE_HIS_COMMENT, WRITE_MESSAGE, PARTICIPATE, LIKE, PAY, SEE_ATTENDEES, CREATE_EVENT, CREATE_CATEGORY, DELETE_HIS_EVENT, MODIFY_HIS_EVENT, DELETE_ACCOUNT, DELETE_COMMENT));
	
	private Set<ApplicationUserPermission> permissions;

	private ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<ApplicationUserPermission> getPermissions() {
		return permissions;
	}
	
	public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
		
		Set<SimpleGrantedAuthority> permissions =  getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
				.collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return permissions;
	}
	
}

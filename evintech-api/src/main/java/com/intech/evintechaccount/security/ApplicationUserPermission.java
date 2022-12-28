package com.intech.evintechaccount.security;

public enum ApplicationUserPermission {
	
	WRITE_COMMENT("writeComment"),
	MODIFY_HIS_COMMENT("modifyHisComment"),
	DELETE_HIS_COMMENT("deleteHisComment"),
	PARTICIPATE("participateEvent"),
	LIKE("likeEvent"),
	WRITE_MESSAGE("writeMessage"),
	PAY("pay"),
	
	DELETE_HIS_ACCOUNT("deleteHisAccount"),
	MODIFY_HIS_ACCOUNT("modifyHisAccount"),
	
	SEE_ATTENDEES("seeAttendees"),
	CREATE_EVENT("createEvent"),
	CREATE_CATEGORY("createCategory"),
	DELETE_HIS_EVENT("deleteHisEvent"),
	MODIFY_HIS_EVENT("modifyHisEvent"),

	DELETE_ACCOUNT("deleteAccount"),
	DELETE_COMMENT("deleteComment");
	
	
	private final String permission;
	
	private ApplicationUserPermission(String permission) {
		this.permission = permission;	
	}
	
	public String getPermission() {
		return permission;
	}

}

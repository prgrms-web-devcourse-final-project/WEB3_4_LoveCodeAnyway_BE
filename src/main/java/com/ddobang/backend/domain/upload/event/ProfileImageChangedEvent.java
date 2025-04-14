package com.ddobang.backend.domain.upload.event;

import com.ddobang.backend.global.event.DomainEvent;

/**
 * ProfileImageChangedEvent
 * <p></p>
 * @author 100minha
 */
public record ProfileImageChangedEvent(String oldUrl) implements DomainEvent {

	@Override
	public String getEventType() {
		return "Profile_Image_Changed_Event";
	}
}

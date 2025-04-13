package com.ddobang.backend.domain.upload.event;

import java.util.List;

import com.ddobang.backend.global.event.DomainEvent;

/**
 * PostAttachmentsUpdatedEvent
 * <p></p>
 * @author 100minha
 */
public record PostAttachmentsUpdatedEvent(List<String> oldUrls) implements DomainEvent {

	@Override
	public String getEventType() {
		return "Post_Attachments_Updated_Event";
	}
}

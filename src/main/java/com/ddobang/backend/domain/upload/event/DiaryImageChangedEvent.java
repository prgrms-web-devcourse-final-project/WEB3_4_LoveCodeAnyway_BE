package com.ddobang.backend.domain.upload.event;

import com.ddobang.backend.global.event.DomainEvent;

/**
 * DiaryImageChangedEvent
 * <p></p>
 * @author 100minha
 */
public record DiaryImageChangedEvent(String oldUrl) implements DomainEvent {

	@Override
	public String getEventType() {
		return "Diary_Image_Changed_Event";
	}
}

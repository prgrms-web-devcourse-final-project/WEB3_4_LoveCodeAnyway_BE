package com.ddobang.backend.domain.upload.event;

import java.util.List;

/**
 * PostAttachmentsUpdatedEvent
 * <p></p>
 * @author 100minha
 */
public record PostAttachmentsUpdatedEvent(List<String> oldUrls) {
}

package com.ddobang.backend.global.validation.validator;

import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.global.validation.annotation.ValidParticipants;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ParticipantsValidator implements ConstraintValidator<ValidParticipants, PartyRequest> {

	@Override
	public boolean isValid(PartyRequest request, ConstraintValidatorContext context) {
		if (request.participantsNeeded() == null || request.totalParticipants() == null) {
			return true;
		}
		return request.participantsNeeded() <= request.totalParticipants();
	}
}

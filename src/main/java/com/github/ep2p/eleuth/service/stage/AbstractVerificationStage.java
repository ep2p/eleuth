package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.service.MessageSignatureService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public abstract class AbstractVerificationStage {
    private final Validator validator;
    private final MessageSignatureService messageSignatureService;

    protected AbstractVerificationStage(Validator validator, MessageSignatureService messageSignatureService) {
        this.validator = validator;
        this.messageSignatureService = messageSignatureService;
    }

    protected <E> boolean hasValidFields(E e, List<String> messages){
        Set<ConstraintViolation<E>> constraintViolations = validator.validate(e);
        if(constraintViolations.size() > 0){
            constraintViolations.forEach(constraintViolation -> {
                messages.add(constraintViolation.getMessage());
            });
            return false;
        }

        return true;
    }

    protected <E extends Serializable> boolean isValidSignature(SignedData<E> signedData){
        try {
            messageSignatureService.validate(signedData);
            return true;
        } catch (InvalidSignatureException e) {
            return false;
        }
    }
}

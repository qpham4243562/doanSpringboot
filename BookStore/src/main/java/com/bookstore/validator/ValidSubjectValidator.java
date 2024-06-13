package com.bookstore.validator;

import com.bookstore.entity.SubjectEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSubjectValidator implements ConstraintValidator<ValidSubject, SubjectEntity> {
    @Override
    public boolean isValid(SubjectEntity subjectEntity, ConstraintValidatorContext context) {
        return subjectEntity != null && subjectEntity.getId() != null;
    }
}

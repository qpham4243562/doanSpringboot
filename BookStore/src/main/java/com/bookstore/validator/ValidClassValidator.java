package com.bookstore.validator;

import com.bookstore.entity.ClassEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidClassValidator implements ConstraintValidator<ValidClass, ClassEntity> {
    @Override
    public boolean isValid(ClassEntity classEntity, ConstraintValidatorContext context) {
        return classEntity != null && classEntity.getId() != null;
    }
}

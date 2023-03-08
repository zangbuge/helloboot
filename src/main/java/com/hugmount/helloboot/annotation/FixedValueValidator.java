package com.hugmount.helloboot.annotation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

/**
 * 校验值是否在预设的取值范围
 *
 * @author: lhm
 * @date: 2023/3/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// 关联注解和校验器
@Constraint(validatedBy = {FixedValueValidator.FixedValueValid.class})
public @interface FixedValueValidator {

    String message() default "not in the field value range, please check @FixedValueValidator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values() default {};

    class FixedValueValid implements ConstraintValidator<FixedValueValidator, Object> {
        Set<String> values = new HashSet<>();

        @Override
        public void initialize(FixedValueValidator constraintAnnotation) {
            for (String val : constraintAnnotation.values()) {
                values.add(val);
            }
        }

        @Override
        public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
            for (String val : values) {
                return val.equals(String.valueOf(obj));
            }
            return false;
        }
    }
}

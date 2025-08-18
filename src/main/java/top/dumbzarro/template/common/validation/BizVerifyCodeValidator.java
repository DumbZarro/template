package top.dumbzarro.template.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class BizVerifyCodeValidator implements ConstraintValidator<BizVerifyCode, String> {

    private static final String REGEX = "^[0-9]{6}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.hasText(value) && value.matches(REGEX);
    }
}
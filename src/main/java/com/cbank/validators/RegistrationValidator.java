package com.cbank.validators;

import com.cbank.domain.RegistrationForm;
import com.cbank.exceptions.ValidationException;
import com.cbank.services.UserService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bank.validators.Validator.validateAccount;
import static com.bank.validators.Validator.validateEmail;
import static org.springframework.util.StringUtils.hasLength;


@Component
@AllArgsConstructor
public class RegistrationValidator implements Validator<RegistrationForm> {

    /**
     * Pattern and regex for username
     */
    private static final String USERNAME_REGEX = "^(?=.*[a-z])[a-z0-9]{3,}";
    private static final Pattern pattern = Pattern.compile(USERNAME_REGEX);

    private static final String NULL = "You have not entered username or email";
    private static final String SMALL_PASSWORD = "Your password has small length";
    private static final String INCORRECT_USERNAME = "Incorrect username";
    private static final String USERNAME_ALREADY_EXIST = "Username already had been registered";
    private static final String SMALL_ADDRESS_OR_NAME = "Address or Name has small length";

    private final UserService userService;


    public void validate(RegistrationForm form) {
        val error = check(form);
        if(error != null) throw new ValidationException(error);
    }

    private String check(RegistrationForm form){
        val username = form.getUsername();
        val email = form.getEmail();
        ValidationUtils.email(email);
        if (username == null) return NULL;
        if (!isAlreadyExist(username)) return USERNAME_ALREADY_EXIST;
        if (!pattern.matcher(username).matches()) return INCORRECT_USERNAME;
        if (!validatePassword(form.getPassword()))  return SMALL_PASSWORD;
        if (hasLength(form.getAddress()) && form.getAddress().length() > 5)  return SMALL_ADDRESS_OR_NAME;
        if (hasLength(form.getName()) && form.getName().length() > 5)  return SMALL_ADDRESS_OR_NAME;
        return null;
    }

    private boolean validatePassword(String password) {
        return hasLength(password) && password.length() >= 5;
    }

    private boolean isAlreadyExist(String username) {
        return userService.byUsername(username).isPresent();
    }


}
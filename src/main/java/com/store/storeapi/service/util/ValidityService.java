package com.store.storeapi.service.util;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.microsoft.sqlserver.jdbc.StringUtils;

@Service
public class ValidityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidityService.class);

	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^0+[0-9]{9}$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$");
	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^([a-zA-Z0-9]+([._\\-\\+][a-zA-Z0-9]+)*[_\\-]*)@(([\\w\\d\\-\\+]+[\\w\\d]+\\.)+[a-zA-Z]{2,63})$");

	public boolean validateUserPassword(String password) {
		return validate(password, PASSWORD_PATTERN);
	}

	public boolean validateEmailAddress(String emailAddress) {
		return validate(emailAddress, EMAIL_PATTERN);
	}

	public boolean validatePhoneNumber(String phoneNumber) {
		return validate(phoneNumber, PHONE_NUMBER_PATTERN);
	}

	private static boolean validate(String stringToMatch, Pattern pattern) {
		return !StringUtils.isEmpty(stringToMatch) && pattern.matcher(stringToMatch).matches();
	}

}

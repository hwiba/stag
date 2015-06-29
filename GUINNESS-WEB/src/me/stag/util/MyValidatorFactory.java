package me.stag.util;

import javax.validation.Validation;
import javax.validation.Validator;

public class MyValidatorFactory {
	private MyValidatorFactory() {
	}

	public static Validator createValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}
}

package pl.bedkowski.code.jdd.validation;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class Constraints {

	public static List<Constraint> constraints(Constraint... constraints) {
		return Arrays.asList(constraints);
	}

	public static Constraint notNull() {
		return new Constraint() {

			@Override
			public boolean isValid(Object value) {
				return value != null;
			}

			@Override
			public String getErrorMessage() {
				return "Value cannot be null";
			}
		};
	}

	public static Constraint size(final int min, final int max) {
		return new Constraint() {

			@Override
			public boolean isValid(Object value) {
				if (value == null) {
					return true;
				}
				String sValue = value.toString();
				if (sValue.length() < min || sValue.length() > max) {
					return false;
				}
				return true;
			}

			@Override
			public String getErrorMessage() {
				return "Value must be between: " + min + ", " + max;
			}
		};
	}

	public static Constraint max(final long max) {
		return new Constraint() {

			@Override
			public boolean isValid(Object value) {
				if (value == null) {
					return true;
				}

				try {
					BigInteger num = new BigInteger(value.toString());
					if (num.longValue() > max) {
						return false;
					}
				} catch(Exception e) {
					// FIXME [mb] ignored exception
				}
				return true;
			}

			@Override
			public String getErrorMessage() {
				return "Value must be lower than: " + max;
			}
		};
	}

}

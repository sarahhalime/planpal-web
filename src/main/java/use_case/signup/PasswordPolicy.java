package use_case.signup;

/**
 * Defines the mandatory password rules used whenever a password is set.
 */
public final class PasswordPolicy {

    public static final int MINIMUM_LENGTH = 8;

    private static final String LENGTH_MESSAGE = "Password must be at least 8 characters long.";
    private static final String UPPERCASE_MESSAGE = "Password must contain at least one uppercase letter.";
    private static final String LOWERCASE_MESSAGE = "Password must contain at least one lowercase letter.";
    private static final String NUMBER_MESSAGE = "Password must contain at least one number.";
    private static final String SPECIAL_MESSAGE = "Password must contain at least one special character.";

    private PasswordPolicy() {
    }

    /**
     * Returns the first password-rule error, or {@code null} when the password is valid.
     *
     * @param password password to validate
     * @return validation message, or {@code null}
     */
    public static String getValidationError(char[] password) {
        final String validationError;

        if (!hasMinimumLength(password)) {
            validationError = LENGTH_MESSAGE;
        }
        else if (!hasUppercase(password)) {
            validationError = UPPERCASE_MESSAGE;
        }
        else if (!hasLowercase(password)) {
            validationError = LOWERCASE_MESSAGE;
        }
        else if (!hasNumber(password)) {
            validationError = NUMBER_MESSAGE;
        }
        else if (!hasSpecialCharacter(password)) {
            validationError = SPECIAL_MESSAGE;
        }
        else {
            validationError = null;
        }

        return validationError;
    }

    /**
     * Returns whether the password meets the minimum length requirement.
     *
     * @param password password to inspect
     * @return whether the requirement is met
     */
    public static boolean hasMinimumLength(char[] password) {
        return password != null && password.length >= MINIMUM_LENGTH;
    }

    /**
     * Returns whether the password contains an uppercase letter.
     *
     * @param password password to inspect
     * @return whether the requirement is met
     */
    public static boolean hasUppercase(char[] password) {
        return containsCharacterType(password, CharacterType.UPPERCASE);
    }

    /**
     * Returns whether the password contains a lowercase letter.
     *
     * @param password password to inspect
     * @return whether the requirement is met
     */
    public static boolean hasLowercase(char[] password) {
        return containsCharacterType(password, CharacterType.LOWERCASE);
    }

    /**
     * Returns whether the password contains a number.
     *
     * @param password password to inspect
     * @return whether the requirement is met
     */
    public static boolean hasNumber(char[] password) {
        return containsCharacterType(password, CharacterType.NUMBER);
    }

    /**
     * Returns whether the password contains a non-alphanumeric character.
     *
     * @param password password to inspect
     * @return whether the requirement is met
     */
    public static boolean hasSpecialCharacter(char[] password) {
        return containsCharacterType(password, CharacterType.SPECIAL);
    }

    private static boolean containsCharacterType(char[] password, CharacterType characterType) {
        boolean found = false;

        if (password != null) {
            for (final char character : password) {
                if (characterType.matches(character)) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Identifies the character class required by a password rule.
     */
    private enum CharacterType {
        UPPERCASE {
            @Override
            boolean matches(char character) {
                return Character.isUpperCase(character);
            }
        },
        LOWERCASE {
            @Override
            boolean matches(char character) {
                return Character.isLowerCase(character);
            }
        },
        NUMBER {
            @Override
            boolean matches(char character) {
                return Character.isDigit(character);
            }
        },
        SPECIAL {
            @Override
            boolean matches(char character) {
                return !Character.isLetterOrDigit(character) && !Character.isWhitespace(character);
            }
        };

        abstract boolean matches(char character);
    }
}

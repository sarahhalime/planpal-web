package data_access;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordHasher {

    private static final Argon2 ARGON2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    private static final int ITERATIONS = 2;
    private static final int MEMORY_KB = 65536;
    // 65,536 KB = 64 MB
    private static final int PARALLELISM = 1;

    /**
     * Hashes the given password. The salt is generated and embedded in the
     * returned hash string automatically by the library, so no salt storage is needed
     * <p/>
     * The caller's char[] is wiped in place before this method returns.
     *
     * @param password the plaintext password
     * @return the encoded hash string for storage
     */
    public static String hash(char[] password) {
        try {
            return ARGON2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, password);
        }
        finally {
            ARGON2.wipeArray(password);
        }
    }

    /**
     * Checks whether the given plaintext password matches a specific hash.
     * <p/>
     * The caller's char[] is wiped in place before this method returns.
     *
     * @param password the plaintext password entered by the user
     * @param storedHash the hash previously returned the hash method in PasswordHasher
     * @return true if they match, false otherwise
     */
    public static boolean matches(char[] password, String storedHash) {
        try {
            return ARGON2.verify(storedHash, password);
        }
        finally {
            ARGON2.wipeArray(password);
        }
    }
}

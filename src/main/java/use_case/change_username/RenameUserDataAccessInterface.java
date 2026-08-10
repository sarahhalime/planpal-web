package use_case.change_username;

/**
 * A store that files data under a username and must follow a rename.
 *
 * <p>Usernames are used as keys across several stores, so changing one has to be
 * carried through all of them or the old rows are left stranded under a name
 * nobody answers to any more.</p>
 */
public interface RenameUserDataAccessInterface {

    /**
     * Moves everything stored under the old username to the new one.
     *
     * @param oldUsername the username being replaced
     * @param newUsername the username to file the data under
     */
    void renameUser(String oldUsername, String newUsername);
}

package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.model.User;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Domain service interface for managing users.
 *
 * This port abstracts user-related operations from the underlying data access implementation.
 */
public interface UserService {

    /**
     * Retrieve all users.
     *
     * @return a non-null list (possibly empty) of all users
     */
    @NonNull
    List<User> getAllUsers();

    /**
     * Retrieve a user by its ID.
     *
     * @param id the ID of the user
     * @return the user with the given ID
     * @throws RuntimeException (or a more specific exception) if the user does not exist
     */
    @NonNull
    User getUserById(@NonNull Long id);

    /**
     * Retrieve users by their login name via a filter-style operation.
     * Even if loginName is intended to be unique, this returns a list
     * to stay flexible and consistent with "filter" endpoints.
     *
     * @param loginName the login name to filter by
     * @return a non-null list (possibly empty) of matching users
     */
    @NonNull
    List<User> findUsersByLoginName(@NonNull String loginName);

    /**
     * Create a new user.
     *
     * @param user the user to create (without ID / timestamps)
     * @return the created user including ID and timestamps
     */
    @NonNull
    User createUser(@NonNull User user);

    /**
     * Update an existing user identified by ID.
     *
     * @param id   the ID of the user to update
     * @param user the new user data (e.g., loginName, emailAddress, names)
     * @return the updated user
     * @throws RuntimeException (or a more specific exception) if the user does not exist
     */
    @NonNull
    User updateUser(@NonNull Long id, @NonNull User user);

    /**
     * Delete an existing user identified by ID.
     *
     * @param id the ID of the user to delete
     * @throws RuntimeException (or a more specific exception) if the user does not exist
     */
    void deleteUserById(@NonNull Long id);
}

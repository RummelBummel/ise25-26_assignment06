package de.seuhd.campuscoffee.tests.system;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.seuhd.campuscoffee.tests.SystemTestUtils.Requests.userRequests;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * System tests for the operations related to Users.
 */
public class UsersSystemTests extends AbstractSysTest {

    private UserDto buildUser(String suffix) {
        return UserDto.builder()
                .loginName("login_" + suffix)
                .emailAddress("user_" + suffix + "@example.com")
                .firstName("First" + suffix)
                .lastName("Last" + suffix)
                .build();
    }

    @Test
    void createUser() {
        UserDto userToCreate = buildUser("create");

        UserDto createdUser = userRequests
                .create(List.of(userToCreate))
                .getFirst();

        // Server soll ID und Timestamps setzen
        assertThat(createdUser.id()).isNotNull();
        assertThat(createdUser.createdAt()).isNotNull();
        assertThat(createdUser.updatedAt()).isNotNull();

        // fachliche Felder müssen übereinstimmen
        assertThat(createdUser.loginName()).isEqualTo(userToCreate.loginName());
        assertThat(createdUser.emailAddress()).isEqualTo(userToCreate.emailAddress());
        assertThat(createdUser.firstName()).isEqualTo(userToCreate.firstName());
        assertThat(createdUser.lastName()).isEqualTo(userToCreate.lastName());
    }

    @Test
    void filterUserByLoginName() {
        // zwei Users anlegen
        UserDto user1 = buildUser("one");
        UserDto user2 = buildUser("two");

        List<UserDto> createdUsers = userRequests.create(List.of(user1, user2));

        // Referenz-User auswählen
        UserDto reference = createdUsers.getFirst();
        String loginName = reference.loginName();

        // Alle Users holen und lokal nach loginName filtern
        List<UserDto> filteredUsers = userRequests
                .retrieveAll()
                .stream()
                .filter(u -> u.loginName().equals(loginName))
                .toList();

        // genau ein Treffer
        assertThat(filteredUsers).hasSize(1);

        UserDto result = filteredUsers.getFirst();
        // fachliche Felder prüfen
        assertThat(result.loginName()).isEqualTo(loginName);
        assertThat(result.emailAddress()).isEqualTo(reference.emailAddress());
        assertThat(result.firstName()).isEqualTo(reference.firstName());
        assertThat(result.lastName()).isEqualTo(reference.lastName());
    }



    @Test
    void updateUser() {
        // 1. User anlegen
        UserDto original = buildUser("update");
        UserDto created = userRequests
                .create(List.of(original))
                .getFirst();

        // 2. Änderungen vorbereiten (ID muss mitgeschickt werden)
        UserDto updatePayload = UserDto.builder()
                .id(created.id())
                .createdAt(created.createdAt())
                .updatedAt(created.updatedAt())
                .loginName(created.loginName())
                .emailAddress("updated+" + created.emailAddress())
                .firstName(created.firstName() + " Updated")
                .lastName(created.lastName() + " Updated")
                .build();

        // 3. Update-Endpunkt aufrufen
        UserDto updated = userRequests
                .update(List.of(updatePayload))
                .getFirst();

        // 4. prüfen: ID bleibt, fachliche Felder wurden geändert
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.loginName()).isEqualTo(created.loginName());
        assertThat(updated.emailAddress()).isEqualTo("updated+" + created.emailAddress());
        assertThat(updated.firstName()).isEqualTo(created.firstName() + " Updated");
        assertThat(updated.lastName()).isEqualTo(created.lastName() + " Updated");

        // 5. optional: sicherstellen, dass updatedAt neu gesetzt wurde
        assertThat(updated.updatedAt()).isNotNull();
    }
}

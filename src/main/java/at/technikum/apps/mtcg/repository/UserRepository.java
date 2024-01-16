package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.dto.UserInDto;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User create(User user) throws InternalServerException;

    void update(String username, UserInDto userDetails) throws InternalServerException;

    Optional<User> find(String username) throws InternalServerException;

    Optional<User> find(UUID userId) throws InternalServerException;
}

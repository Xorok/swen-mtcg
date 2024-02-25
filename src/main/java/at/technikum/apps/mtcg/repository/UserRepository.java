package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.StatNotFoundException;
import at.technikum.apps.mtcg.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User create(User user, Stat stat) throws InternalServerException;

    User update(User user) throws UserNotFoundException, InternalServerException;

    Optional<User> find(String username) throws InternalServerException;

    Optional<User> find(UUID userId) throws InternalServerException;

    Optional<Stat> getStat(UUID userId) throws InternalServerException;

    Stat setStat(UUID userId, Stat stat) throws StatNotFoundException, InternalServerException;

    List<StatOutDto> getScoreboard() throws InternalServerException;
}

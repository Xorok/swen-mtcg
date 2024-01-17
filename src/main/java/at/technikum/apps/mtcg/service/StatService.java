package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.StatToStatOutDtoConverter;
import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.StatNotFoundException;
import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.Optional;

public class StatService {

    private final UserRepository userRepository;
    private final StatToStatOutDtoConverter statConverter;

    public StatService(UserRepository userRepository, StatToStatOutDtoConverter statConverter) {
        this.userRepository = userRepository;
        this.statConverter = statConverter;
    }

    public StatOutDto getStat(User user) throws StatNotFoundException, InternalServerException {
        Optional<Stat> stat = userRepository.getStat(user.getUserId());
        if (stat.isEmpty()) {
            throw new StatNotFoundException("User statistics could not be found!");
        }
        return statConverter.convert(stat.get(), user);
    }
}

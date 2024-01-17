package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.List;

public class ScoreboardService {

    private final UserRepository userRepository;

    public ScoreboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<StatOutDto> getScoreboard() throws InternalServerException {
        return userRepository.getScoreboard();
    }
}

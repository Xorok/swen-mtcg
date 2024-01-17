package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;

public class StatToStatOutDtoConverter {

    public StatOutDto convert(Stat stat, User user) {
        return new StatOutDto(
                user.getUsername(),
                user.getName(),
                stat.getElo(),
                stat.getWins(),
                stat.getLosses()
        );
    }
}
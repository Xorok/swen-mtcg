package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.UserOutDto;
import at.technikum.apps.mtcg.entity.User;

public class UserToUserOutDtoConverter {

    public static UserOutDto convert(User user) {
        return new UserOutDto(
                user.getUserId().toString(),
                user.getName(),
                user.getUsername(),
                user.getCoins(),
                user.getElo(),
                user.getBio(),
                user.getImage()
        );
    }
}
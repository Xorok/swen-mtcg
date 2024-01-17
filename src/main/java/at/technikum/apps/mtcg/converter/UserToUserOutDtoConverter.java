package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.UserOutDto;
import at.technikum.apps.mtcg.entity.User;

public class UserToUserOutDtoConverter implements OutConverter<User, UserOutDto> {

    @Override
    public UserOutDto convert(User user) {
        return new UserOutDto(
                user.getName(),
                user.getUsername(),
                user.getCoins(),
                user.getBio(),
                user.getImage()
        );
    }
}
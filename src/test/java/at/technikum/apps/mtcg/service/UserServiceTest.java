package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.LoginInDto;
import at.technikum.apps.mtcg.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Test
    void shouldSetTaskId_whenSaveTask() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        LoginInDto user = new LoginInDto("", "Clean", "Kitchen", false);

        when(userRepository.save(any())).then(returnsFirstArg());

        // Act
        LoginInDto answer = userService.create(user);

        // Assert
        assertNotEquals("", answer.getId());
        assertEquals("Clean", answer.getName());
        assertEquals("Kitchen", answer.getDescription());
        assertFalse(answer.isDone());
    }

    @Test
    void shouldCallTaskRepository_whenSaveTask() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        LoginInDto user = new LoginInDto("", "Clean", "Kitchen", false);

        // Act
        userService.create(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }
}
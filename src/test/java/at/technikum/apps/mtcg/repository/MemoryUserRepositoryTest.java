package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.UserLoginDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryUserRepositoryTest {

    @Test
    void whenAddOneTask_ThenFindAllShouldReturnOneMore() {
        // Arrange
        MemoryUserRepository memoryUserRepository
                = new MemoryUserRepository();
        UserLoginDTO user = new UserLoginDTO(
                "1234-1234",
                "Clean",
                "Kitchen",
                false
        );

        // Act
        memoryUserRepository.save(user);
        List<UserLoginDTO> users = memoryUserRepository.findAll();

        // Assert
        assertEquals(1, users.size());
    }

    @Test
    void whenDeleteTask_ThenShouldBeRemovedFromFindAll() {
        // Arrange
        MemoryUserRepository memoryUserRepository
                = new MemoryUserRepository();
        UserLoginDTO user = new UserLoginDTO(
                "1234-1234",
                "Clean",
                "Kitchen",
                false
        );
        memoryUserRepository.save(user);

        // Act
        memoryUserRepository.delete(user);
        List<UserLoginDTO> users = memoryUserRepository.findAll();

        // Assert
        assertEquals(0, users.size());
    }
}
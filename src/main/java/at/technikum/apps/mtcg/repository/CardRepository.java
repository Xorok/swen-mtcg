package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidUserException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;

public interface CardRepository {
    void createAll(Card[] cards) throws InternalServerException;

    User buyPackage(User user) throws InvalidUserException, NotEnoughCoinsException, NoPackageAvailableException, InternalServerException;
}
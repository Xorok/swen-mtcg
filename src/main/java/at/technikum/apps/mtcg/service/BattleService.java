package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.DuplicateUserEntryException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoDeckDefinedException;
import at.technikum.apps.mtcg.exception.StatNotFoundException;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final RandomUtils randomUtils;
    private static final List<User> userQueue = new ArrayList<>();
    private static final Object lock = new Object();

    public BattleService(UserRepository userRepository, CardRepository cardRepository, RandomUtils randomUtils) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.randomUtils = randomUtils;
    }

    public String enterBattle(User user) throws NoDeckDefinedException, DuplicateUserEntryException, InternalServerException {
        synchronized (lock) {
            if (userQueue.contains(user)) { // TODO: Also stop user from entering new game before old one is finished!
                throw new DuplicateUserEntryException("This player is already waiting for a battle!");
            }

            if (userQueue.isEmpty()) {
                // Add new user
                userQueue.add(user);

                try {
                    lock.wait();
                    // TODO: Handle when user cancels request --> InterruptedException? --> Remove from list
                } catch (InterruptedException e) {
                    throw new InternalServerException("An error occurred while waiting for other players!");
                }
            } else {
                // Get opponent from queue
                User opponent = userQueue.get(0);
                userQueue.remove(0);

                // Do battle
                String battleLog = fightBattle(opponent, user);
                // TODO: Still notify waiting user thread if exception during battle occurs

                // Set results
                opponent.setLastBattleLog(battleLog);
                user.setLastBattleLog(battleLog);

                lock.notifyAll();
            }

            return user.getLastBattleLog();
        }
    }

    public String fightBattle(User player1, User player2) throws InternalServerException {
        List<Card> deck1 = cardRepository.getDeck(player1);
        List<Card> deck2 = cardRepository.getDeck(player2);

        StringBuilder battleLog = new StringBuilder("-- Battle Log: " + player1.getUsername() + " (P1) vs " + player2.getUsername() + " (P2) --\n");
        int wonRounds1 = 0;
        int wonRounds2 = 0;

        for (int i = 0; !deck1.isEmpty() && !deck2.isEmpty() && i < 100; i++) {
            Card card1 = deck1.get(randomUtils.getRandomNumber(0, deck1.size()));
            Card card2 = deck2.get(randomUtils.getRandomNumber(0, deck2.size()));
            Double damage1 = card1.getDamage();
            Double damage2 = card2.getDamage();
            String name1 = card1.getName();
            String name2 = card2.getName();
            StringBuilder roundLog = new StringBuilder(" #" + (i + 1) + " P1: " + name1 + " (" + damage1 + " Damage) vs P2: " + name2 + " (" + damage2 + " Damage) => ");
            boolean monsterFight = true;

            // Get damage for battles involving spells (based on element)
            if (card1.getType() == Card.Type.SPELL || card2.getType() == Card.Type.SPELL) {
                monsterFight = false;
                roundLog.append(damage1).append(" VS ").append(damage2).append(" -> ");
                damage1 *= Card.Element.getEffectiveness(card1.getElement(), card2.getElement());
                damage2 *= Card.Element.getEffectiveness(card2.getElement(), card1.getElement());
                roundLog.append(damage1).append(" VS ").append(damage2).append(" => ");
            }

            // Specialities
            if (name1.contains("Goblin") && name2.contains("Dragon")) {
                damage1 = 0d;
                roundLog.append("Goblins are too afraid of Dragons to attack. ");
            } else if (name2.contains("Goblin") && name1.contains("Dragon")) {
                damage2 = 0d;
                roundLog.append("Goblins are too afraid of Dragons to attack. ");
            } else if (name1.contains("Ork") && name2.contains("Wizard")) {
                damage1 = 0d;
                roundLog.append("Wizards can control Orks, so they are not able to damage them. ");
            } else if (name2.contains("Ork") && name1.contains("Wizard")) {
                damage2 = 0d;
                roundLog.append("Wizards can control Orks, so they are not able to damage them. ");
            } else if (name1.contains("Knight") && card2.getElement() == Card.Element.WATER && card2.getType() == Card.Type.SPELL) {
                damage1 = 0d;
                roundLog.append("The armor of Knights is so heavy that WaterSpells make them drown instantly. ");
            } else if (name2.contains("Knight") && card1.getElement() == Card.Element.WATER && card1.getType() == Card.Type.SPELL) {
                damage2 = 0d;
                roundLog.append("The armor of Knights is so heavy that WaterSpells make them drown instantly. ");
            } else if (card1.getType() == Card.Type.SPELL && name2.contains("Kraken")) {
                damage1 = 0d;
                roundLog.append("The Kraken is immune against spells. ");
            } else if (card2.getType() == Card.Type.SPELL && name1.contains("Kraken")) {
                damage2 = 0d;
                roundLog.append("The Kraken is immune against spells. ");
            } else if (name1.contains("Dragon") && name2.contains("Elf") && card2.getElement() == Card.Element.FIRE) {
                damage1 = 0d;
                roundLog.append("The FireElves know Dragons since they were little and can evade their attacks. ");
            } else if (name2.contains("Dragon") && name1.contains("Elf") && card1.getElement() == Card.Element.FIRE) {
                damage2 = 0d;
                roundLog.append("The FireElves know Dragons since they were little and can evade their attacks. ");
            }

            if (damage1 > damage2) {
                deck1.add(card2);
                deck2.remove(card2);
                wonRounds1++;
                if (monsterFight) roundLog.append(name1).append(" defeats ").append(name2).append("\n");
                else roundLog.append(name1).append(" wins\n");
            } else if (damage1 < damage2) {
                deck2.add(card1);
                deck1.remove(card1);
                wonRounds2++;
                if (monsterFight) roundLog.append(name2).append(" defeats ").append(name1).append("\n");
                else roundLog.append(name2).append(" wins\n");
            } else {
                // In case of a draw, no cards are moved
                roundLog.append(name2).append(" Draw (no action)\n");
            }

            battleLog.append(roundLog);
        }

        Optional<Stat> stat1Opt = userRepository.getStat(player1.getUserId());
        Optional<Stat> stat2Opt = userRepository.getStat(player2.getUserId());
        if (stat1Opt.isEmpty() || stat2Opt.isEmpty()) {
            throw new InternalServerException("Couldn't find stats for user '" + (stat1Opt.isEmpty() ? player1.getUsername() : player2.getUsername()) + "'!");
        }
        Stat stat1 = stat1Opt.get();
        Stat stat2 = stat2Opt.get();

        boolean statsChanged = false;
        if (deck1.isEmpty() || (!deck2.isEmpty() && wonRounds2 > wonRounds1)) {
            stat1.registerLoss();
            stat2.registerWin();
            statsChanged = true;
            battleLog.append("\n").append(" Player 2 ").append(player2.getUsername()).append(" won!\n");
        } else if (deck2.isEmpty() || wonRounds1 > wonRounds2) {
            stat2.registerLoss();
            stat1.registerWin();
            statsChanged = true;
            battleLog.append("\n").append(" Player 1 ").append(player1.getUsername()).append(" won!\n");
        } else {
            // In case of a draw, stats are not changed
            battleLog.append("\n").append(" The battle was a draw!\n");
        }

        if (statsChanged) {
            try {
                userRepository.setStat(player1.getUserId(), stat1);
                userRepository.setStat(player2.getUserId(), stat2);
            } catch (StatNotFoundException e) {
                throw new InternalServerException(e.getMessage());
            }
        }

        return battleLog.toString();
    }
}

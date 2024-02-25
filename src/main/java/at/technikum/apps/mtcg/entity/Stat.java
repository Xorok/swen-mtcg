package at.technikum.apps.mtcg.entity;

import java.util.Objects;
import java.util.UUID;

public class Stat {
    private UUID userId;
    private int elo;
    private int wins;
    private int losses;

    public Stat(UUID userId) {
        this.userId = userId;
        this.elo = 100; // set default starting elo
        this.wins = 0;
        this.losses = 0;
    }

    public Stat(UUID userId, int elo, int wins, int losses) {
        this.userId = userId;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void registerWin() {
        wins++;
        elo += 3;
    }

    public void registerLoss() {
        losses++;
        elo -= 5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stat user = (Stat) o;
        return Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}

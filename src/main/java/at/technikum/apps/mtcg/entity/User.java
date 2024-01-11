package at.technikum.apps.mtcg.entity;

import java.util.UUID;

public class User {

    private UUID uuid;

    private String username;

    private String passwordHash;

    private int coins;

    private int elo;

    public User(UserLogin userLogin) {
        // TODO: Check requirements
        username = userLogin.getUsername();
        // TODO: Add hashing
        passwordHash = userLogin.getPassword();
        // TODO: Check for collisions
        uuid = UUID.randomUUID();
        coins = 20;
        elo = 100;
    }

    public User(UUID uuid, String username, String passwordHash, int coins, int elo) {
        this.uuid = uuid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.coins = coins;
        this.elo = elo;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }
}

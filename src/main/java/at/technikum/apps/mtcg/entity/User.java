package at.technikum.apps.mtcg.entity;

import java.util.Objects;
import java.util.UUID;

public class User {
    private UUID userId;
    private String username;
    private String passwordHash;
    private byte[] passwordSalt;
    private int coins;
    private String name;
    private String bio;
    private String image;
    private String lastBattleLog;

    public User(UUID userId, String username, String passwordHash, byte[] passwordSalt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.coins = 20; // set default coins
    }

    public User(UUID userId, String username, String passwordHash, byte[] passwordSalt, int coins, String name, String bio, String image) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastBattleLog() {
        return lastBattleLog;
    }

    public void setLastBattleLog(String lastBattleLog) {
        this.lastBattleLog = lastBattleLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}

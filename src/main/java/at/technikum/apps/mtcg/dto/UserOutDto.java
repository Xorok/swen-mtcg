package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserOutDto {
    @JsonProperty(value = "Name")
    private String name;
    @JsonProperty(value = "Username")
    private String username;
    @JsonProperty(value = "Coins")
    private int coins;
    @JsonProperty(value = "Bio")
    private String bio;
    @JsonProperty(value = "Image")
    private String image;

    public UserOutDto(String name, String username, int coins, String bio, String image) {
        this.name = name;
        this.username = username;
        this.coins = coins;
        this.bio = bio;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
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
}

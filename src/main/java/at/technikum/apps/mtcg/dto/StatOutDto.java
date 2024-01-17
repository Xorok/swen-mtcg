package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatOutDto {
    @JsonProperty(value = "Username")
    private String username;
    @JsonProperty(value = "Name")
    private String name;
    @JsonProperty(value = "Elo")
    private int elo;
    @JsonProperty(value = "Wins")
    private int wins;
    @JsonProperty(value = "Losses")
    private int losses;

    public StatOutDto(String username, String name, int elo, int wins, int losses) {
        this.username = username;
        this.name = name;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

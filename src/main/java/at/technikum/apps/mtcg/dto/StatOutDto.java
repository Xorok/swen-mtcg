package at.technikum.apps.mtcg.dto;

public class StatOutDto {
    private String username;
    private String name;
    private int elo;
    private int wins;
    private int losses;

    public StatOutDto() {
    }

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

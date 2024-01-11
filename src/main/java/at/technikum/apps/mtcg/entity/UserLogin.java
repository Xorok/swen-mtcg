package at.technikum.apps.mtcg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLogin {

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    public UserLogin() {
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

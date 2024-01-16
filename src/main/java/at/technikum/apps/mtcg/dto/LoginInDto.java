package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInDto {

    @JsonProperty(value = "Username", required = true)
    private String username;

    @JsonProperty(value = "Password", required = true)
    private String password;

    public LoginInDto() {
    }

    public LoginInDto(String username, String password) {
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

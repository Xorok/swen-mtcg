package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInDto {
    @JsonProperty(value = "Name")
    private String name;
    @JsonProperty(value = "Bio")
    private String bio;
    @JsonProperty(value = "Image")
    private String image;

    UserInDto() {
    }

    public UserInDto(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
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
}

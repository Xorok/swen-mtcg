package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardDto {

    @JsonProperty(value = "Id", required = true)
    private String id;
    @JsonProperty(value = "Name", required = true)
    private String name;
    @JsonProperty(value = "Damage", required = true)
    private Double damage;

    public CardDto() {
    }

    public CardDto(String id, String name, Double damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDamage() {
        return damage;
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }
}

package at.technikum.apps.mtcg.entity;

import java.util.UUID;

public class Card {
    public enum Type {
        MONSTER("Monster"),
        SPELL("Spell");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type mapFrom(String value) {
            return switch (value) {
                case "Monster" -> MONSTER;
                case "Spell" -> SPELL;
                default -> throw new RuntimeException("No Type mapping for \"" + value + "\"!");
            };
        }
    }

    public enum Element {
        WATER("Water"),
        FIRE("Fire"),
        NORMAL("Normal");

        private final String value;

        Element(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Element mapFrom(String value) {
            return switch (value) {
                case "Water" -> WATER;
                case "Fire" -> FIRE;
                case "Normal" -> NORMAL;
                default -> throw new RuntimeException("No Element mapping for \"" + value + "\"!");
            };
        }
    }

    private UUID id;
    private String name;
    private Double damage;
    private Type type;
    private Element element;
    private UUID owner;

    public Card(UUID id, String name, Double damage, Type type, Element element, UUID owner) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.element = element;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}

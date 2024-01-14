package at.technikum.apps.mtcg.entity;

import at.technikum.apps.mtcg.dto.CardCreation;

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
    }

    private UUID id;
    private String name;
    private Double damage;
    private Type type;
    private Element element;
    private UUID owner;

    public Card(CardCreation newCard) {
        Card.Element element = Card.Element.NORMAL;
        if (newCard.getName().contains("Fire")) {
            element = Card.Element.FIRE;
        } else if (newCard.getName().contains("Water")) {
            element = Card.Element.WATER;
        }

        // TODO: Handle exceptions, check values
        this.id = UUID.fromString(newCard.getId());
        this.name = newCard.getName();
        this.damage = Double.parseDouble(newCard.getDamage());
        this.type = newCard.getName().contains("Spell") ? Card.Type.SPELL : Card.Type.MONSTER;
        this.element = element;
        this.owner = null;
    }

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
package at.technikum.apps.mtcg.entity;

import at.technikum.apps.mtcg.exception.InvalidElementException;
import at.technikum.apps.mtcg.exception.InvalidTypeException;

import java.util.UUID;

public class Card {
    public enum Type {
        MONSTER("MONSTER"),
        SPELL("SPELL");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type mapFrom(String value) throws InvalidTypeException {
            value = value != null ? value.toUpperCase() : "";
            return switch (value) {
                case "MONSTER" -> MONSTER;
                case "SPELL" -> SPELL;
                default -> throw new InvalidTypeException("No Type mapping for \"" + value + "\"!");
            };
        }
    }

    public enum Element {
        WATER("WATER"),
        FIRE("FIRE"),
        NORMAL("NORMAL");

        private final String value;

        Element(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Element mapFrom(String value) throws InvalidElementException {
            value = value != null ? value.toUpperCase() : "";
            return switch (value) {
                case "WATER" -> WATER;
                case "FIRE" -> FIRE;
                case "NORMAL" -> NORMAL;
                default -> throw new InvalidElementException("No Element mapping for \"" + value + "\"!");
            };
        }

        public static Double getEffectiveness(Element e1, Element e2) {
            if (e1 == WATER) {
                if (e2 == FIRE) return 2.0;
                if (e2 == NORMAL) return 0.5;
            } else if (e1 == FIRE) {
                if (e2 == NORMAL) return 2.0;
                if (e2 == WATER) return 0.5;
            } else if (e1 == NORMAL) {
                if (e2 == WATER) return 2.0;
                if (e2 == FIRE) return 0.5;
            }
            return 1.0;
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

    @Override
    public String toString() {
        return "-- " + name + " --" +
                "\n  Id: " + id +
                "\n  Damage: " + damage +
                "\n  Type: " + type +
                "\n  Element: " + element +
                "\n  Owner: " + owner + "\n";
    }
}

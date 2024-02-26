package at.technikum.apps.mtcg.entity;


import java.util.UUID;

public class Trade {
    private UUID offeredCardId;
    private Card.Type requestedType;
    private Card.Element requestedElement;
    private Double requestedMinDamage;

    public Trade(UUID offeredCardId, Card.Type requestedType, Card.Element requestedElement, Double requestedMinDamage) {
        this.offeredCardId = offeredCardId;
        this.requestedType = requestedType;
        this.requestedElement = requestedElement;
        this.requestedMinDamage = requestedMinDamage;
    }

    public UUID getOfferedCardId() {
        return offeredCardId;
    }

    public void setOfferedCardId(UUID offeredCardId) {
        this.offeredCardId = offeredCardId;
    }

    public Card.Type getRequestedType() {
        return requestedType;
    }

    public void setRequestedType(Card.Type requestedType) {
        this.requestedType = requestedType;
    }

    public Card.Element getRequestedElement() {
        return requestedElement;
    }

    public void setRequestedElement(Card.Element requestedElement) {
        this.requestedElement = requestedElement;
    }

    public Double getRequestedMinDamage() {
        return requestedMinDamage;
    }

    public void setRequestedMinDamage(Double requestedMinDamage) {
        this.requestedMinDamage = requestedMinDamage;
    }
}

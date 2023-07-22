package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public final class Seat {
    @JsonIgnore
    private final int row;
    private final int column;
    private int price;

    public Seat(@JsonProperty("row") int row, @JsonProperty("column") int column) {
        this.row = row;
        this.column = column;
    }

    public int getPrice() {
        return price;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Seat) obj;
        return this.row == that.row &&
                this.column == that.column &&
                this.price == that.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, price);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    public enum SeatType {
        AVAILABLE, SOLD
    }
}
package cinema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {

    private final String token;

    @JsonProperty("ticket")
    private Seat seat;

    public Ticket(@JsonProperty("token") String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return String.format("{\"token\": \"%s\",\"ticket\": %s}", this.token, this.seat);
    }
}

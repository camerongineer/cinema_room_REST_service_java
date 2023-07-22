package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static cinema.CinemaController.generateErrorString;
import static java.util.UUID.randomUUID;


@Component
public class Cinema {
    private final int totalRows = 9;
    private final int totalColumns = 9;
    private final CinemaStats stats;

    private final ConcurrentHashMap<Seat.SeatType, ArrayList<Seat>> theaterSeats = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Ticket> ticketsSold = new ConcurrentHashMap<>();

    public Cinema() {
        initTheaterSeats(this.theaterSeats);
        this.stats = new CinemaStats(this);
    }

    private void initTheaterSeats(ConcurrentHashMap<Seat.SeatType, ArrayList<Seat>> theaterSeats) {
        theaterSeats.put(Seat.SeatType.SOLD, new ArrayList<>());
        theaterSeats.put(Seat.SeatType.AVAILABLE, new ArrayList<>());
        for (int row = 1; row <= totalRows; row++) {
            for (int column = 1; column <= totalColumns; column++) {
                int price = row <= 4 ? 10 : 8;
                Seat seat = new Seat(row, column);
                seat.setPrice(price);
                theaterSeats.get(Seat.SeatType.AVAILABLE).add(seat);
            }
        }
    }

    public ResponseEntity<String> sellTicket(int row, int column) {
        try {
            for (Seat seat : this.theaterSeats.get(Seat.SeatType.AVAILABLE)) {
                if (row < 1 || totalRows < row || column < 1 || totalColumns < column) {
                    throw new IllegalArgumentException("The number of a row or a column is out of bounds!");
                }
                if (seat.getRow() == row && seat.getColumn() == column) {
                    Ticket ticket = new Ticket(randomUUID().toString());
                    ticket.setSeat(seat);
                    this.ticketsSold.put(ticket.getToken(), ticket);
                    this.theaterSeats.get(Seat.SeatType.SOLD).add(seat);
                    this.theaterSeats.get(Seat.SeatType.AVAILABLE).remove(seat);
                    return new ResponseEntity<>(ticket.toString(), HttpStatus.valueOf(200));
                }
            }
            throw new IllegalArgumentException("The ticket has been already purchased!");
        } catch (IllegalArgumentException ie) {
            return new ResponseEntity<>(generateErrorString(ie.getMessage()), HttpStatus.valueOf(400));
        }
    }

    public ResponseEntity<String> returnTicket(String token) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(token);
            String tokenValue = jsonNode.get("token").asText();
            if (this.ticketsSold.containsKey(tokenValue)) {
                Seat seat = this.ticketsSold.get(tokenValue).getSeat();
                this.theaterSeats.get(Seat.SeatType.AVAILABLE).add(seat);
                this.theaterSeats.get(Seat.SeatType.SOLD).remove(seat);
                this.ticketsSold.remove(tokenValue);
                return new ResponseEntity<>(String.format("{\"returned_ticket\": %s}", seat.toString()),
                                            HttpStatus.valueOf(200));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(generateErrorString("Wrong token!"), HttpStatus.valueOf(400));
    }

    @JsonIgnore
    public CinemaStats getStats() {
        return stats;
    }

    @JsonProperty("total_rows")
    public int getTotalRows() {
        return totalRows;
    }

    @JsonProperty("total_columns")
    public int getTotalColumns() {
        return totalColumns;
    }

    @JsonProperty("available_seats")
    public List<Seat> getAvailableTheaterSeats() {
        return theaterSeats.get(Seat.SeatType.AVAILABLE);
    }

    @JsonIgnore
    public ConcurrentHashMap<String, Ticket> getTicketsSold() {
        return ticketsSold;
    }

    public class CinemaStats {

        @JsonProperty("current_income")
        private int currentIncome;

        @JsonProperty("number_of_available_seats")
        private int numAvailableSeats;

        @JsonProperty("number_of_purchased_tickets")
        private int numPurchasedTickets;

        @JsonIgnore
        private final Cinema cinema;

        public CinemaStats(Cinema cinema) {
            this.cinema = cinema;
        }

        public int getCurrentIncome() {
            setCurrentIncome(cinema.getTicketsSold().values().stream()
                       .mapToInt(ticket -> ticket.getSeat().getPrice())
                       .sum());
            return currentIncome;
        }

        public int getNumAvailableSeats() {
            setNumAvailableSeats(cinema.theaterSeats.get(Seat.SeatType.AVAILABLE).size());
            return numAvailableSeats;
        }

        public int getNumPurchasedTickets() {
            setNumPurchasedTickets(cinema.getTicketsSold().size());
            return numPurchasedTickets;
        }

        public void setCurrentIncome(int currentIncome) {
            this.currentIncome = currentIncome;
        }

        public void setNumAvailableSeats(int numAvailableSeats) {
            this.numAvailableSeats = numAvailableSeats;
        }

        public void setNumPurchasedTickets(int numPurchasedTickets) {
            this.numPurchasedTickets = numPurchasedTickets;
        }
    }
}
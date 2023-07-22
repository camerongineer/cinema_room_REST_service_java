package cinema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
public class CinemaController {

    private final Cinema cinema;

    @Autowired
    public CinemaController(Cinema cinema) {
        this.cinema = cinema;
    }

    @GetMapping("/seats")
    public Cinema showCinema() {
        return cinema;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(@RequestBody Seat seat) {
        return cinema.sellTicket(seat.getRow(), seat.getColumn());
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnTicket(@RequestBody String token) {
        return cinema.returnTicket(token);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Optional<String> password) {
        if (password.isPresent()) {
            try {
                if (password.get().equals(Main.PASSWORD)) {
                    return new ResponseEntity<>(new ObjectMapper().writeValueAsString(cinema.getStats()),
                                                HttpStatus.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(generateErrorString("The password is wrong!"),
                                    HttpStatus.UNAUTHORIZED);
    }

    public static String generateErrorString(String errorMessage) {
        return String.format("{\"error\": \"%s\"}", errorMessage);
    }
}

package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AntiFraudController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<Object> validateTransaction(@RequestBody Map<String, Long> amount) {
        if (amount.get("amount") == null || amount.get("amount") <= 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Long transactionAmount = amount.get("amount");

        if (transactionAmount <= 200) {
            return new ResponseEntity<>(Map.of("result", "ALLOWED"),
                    HttpStatus.OK);
        }
        if (transactionAmount <= 1500) {
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("result", "PROHIBITED"),
                HttpStatus.OK);
    }
}

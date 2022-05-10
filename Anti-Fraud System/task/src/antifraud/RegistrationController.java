package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class RegistrationController {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/api/auth/user")
    public ResponseEntity<Object> register(@RequestBody Map<String, String> input) {
        if (input.get("name") == null || input.get("username") == null || input.get("password") == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        String name = input.get("name");
        String username = input.get("username");
        String password = input.get("password");

        if (userService.getUserByUsername(username) != null) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        User user = new User(name, username, password);
        if (userService.getAllUsers().isEmpty()) {
            user.setRole("ROLE_ADMINISTRATOR");
        } else {
            user.setRole("ROLE_MERCHANT");

        }
        userService.saveUser(user);

        return new ResponseEntity<>(Map.of("id", user.getId(),
                "name", name,
                "username", username,
                "role", user.getRole().split("_")[1]),
                HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<Object> getUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<Map<String, Object>> response = new LinkedList<>();
        for (User user : allUsers) {
            response.add(Map.of("id", user.getId(),
                    "name", user.getName(),
                    "username", user.getUsername(),
                    "role", user.getRole().split("_")[1]));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(user);
        return new ResponseEntity<>(Map.of("username", username, "status", "Deleted successfully!"),
                HttpStatus.OK);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<Object> assignRole(@RequestBody Map<String, String> userRole) {
        String username = userRole.get("username");
        String role = userRole.get("role");
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (!role.equals("SUPPORT") || !role.equals("MERCHANT")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        String currentRole = user.getRole().split("_")[1];
        if (role.equals(currentRole)) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        user.setRole("ROLE_" + role);
        return new ResponseEntity<>(Map.of("id", user.getId(),
                "name", user.getName(),
                "username", user.getUsername(),
                "role", user.getRole()), HttpStatus.OK);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<Object> updateLock(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String operation = request.get("operation");

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (user.getRole().equals("ROLE_ADMINISTRATOR")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}

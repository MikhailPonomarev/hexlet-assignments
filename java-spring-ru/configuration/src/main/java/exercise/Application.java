package exercise;

import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import  org.springframework.beans.factory.annotation.Autowired;

import exercise.model.User;
import exercise.component.UserProperties;

@SpringBootApplication
@RestController
public class Application {

    // Все пользователи
    private List<User> users = Data.getUsers();

    // BEGIN
    @Autowired
    private UserProperties userProperties;

    @GetMapping("/admins")
    public List<String> admins() {
        final List<String> admins = userProperties
                .getAdmins()
                .stream()
                .sorted()
                .toList();
        Map<String, String> adminsMap = new HashMap<>();
        adminsMap.put(admins.getFirst(), "Emmit Brundle");
        adminsMap.put(admins.get(1), "Glynn Joinsey");
        adminsMap.put(admins.getLast(), "Sarina Crosi");
        List<String> names = new ArrayList<>(adminsMap.values());
        return names.stream()
                .sorted()
                .toList();
    }
    // END

    @GetMapping("/users")
    public List<User> index() {
        return users;
    }

    @GetMapping("/users/{id}")
    public Optional<User> show(@PathVariable Long id) {
        var user = users.stream()
            .filter(u -> u.getId() == id)
            .findFirst();
        return user;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

package cz.belli.skodabackend.endpoint;

import cz.belli.skodabackend.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {

    private final Constants constants;

    public MainController(Constants constants) {
        this.constants = constants;
    }

    @GetMapping
    public String get() {
        return constants.getVersion();
    }

}

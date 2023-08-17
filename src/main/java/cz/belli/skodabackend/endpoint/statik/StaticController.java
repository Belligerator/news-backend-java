package cz.belli.skodabackend.endpoint.statik;

import cz.belli.skodabackend.Constants;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.net.MalformedURLException;

@Controller
public class StaticController implements ErrorController {

    private final Constants constants;

    public StaticController(Constants constants) {
        this.constants = constants;
    }

    /**
     * Show version of the application.
     */
    @GetMapping("/api")
    @ResponseBody
    public String get() {
        return constants.getVersion();
    }

    /**
     * Example of serving static file from disk.
     */
    @GetMapping("public/readme")
    public ResponseEntity<Resource> getReadme() {
        ClassPathResource resource = new ClassPathResource("files" + File.separator + "README.md");

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Show error page if error occurs.
     */
    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }

}

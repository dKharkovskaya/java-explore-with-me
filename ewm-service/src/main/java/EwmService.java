import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.client.StatsClient;


@SpringBootApplication(scanBasePackageClasses = {EwmService.class, StatsClient.class })
public class EwmService {

    public static void main(String[] args) {
        SpringApplication.run(EwmService.class, args);
    }
}
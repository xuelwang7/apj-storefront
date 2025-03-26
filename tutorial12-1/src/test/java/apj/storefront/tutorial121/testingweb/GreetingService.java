package apj.storefront.tutorial121.testingweb;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String greet() {
        return "Hello, World";
    }
}
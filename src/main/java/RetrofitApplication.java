import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import retrofit2.converter.jackson.JacksonConverterFactory;

@SpringBootApplication
@RetrofitScan("com.retrofit.demo")
@ComponentScan(value = "com.retrofit.demo")
public class RetrofitApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RetrofitApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RetrofitApplication.class);
    }

    @Bean
    JacksonConverterFactory jacksonConverterFactory() {
        return JacksonConverterFactory.create();
    }
}

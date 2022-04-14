package webrtc.openvidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class OpenviduApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenviduApplication.class, args);
	}

}

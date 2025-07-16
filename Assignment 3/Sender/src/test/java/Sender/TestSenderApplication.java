package Sender;

import org.springframework.boot.SpringApplication;

public class TestSenderApplication {

	public static void main(String[] args) {
		SpringApplication.from(SenderApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

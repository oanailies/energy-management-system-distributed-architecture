package Receiver;

import org.springframework.boot.SpringApplication;

public class TestReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReceiverApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

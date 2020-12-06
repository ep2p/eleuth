package com.github.ep2p.eleuth;

import com.github.ep2p.kademlia.Common;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EleuthApplication {

	public static void main(String[] args) {
		Common.IDENTIFIER_SIZE = 128;
		SpringApplication.run(EleuthApplication.class, args);
	}

}

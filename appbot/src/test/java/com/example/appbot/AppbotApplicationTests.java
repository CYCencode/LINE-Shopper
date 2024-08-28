package com.example.appbot;

import com.example.appbot.dto.CheckoutRequestDTO;
import com.example.appbot.service.CheckoutService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppbotApplicationTests {

	@Autowired
	private CheckoutService checkoutService;

	@Test
	void contextLoads() {
		checkoutService.verifyLogistic(
			CheckoutRequestDTO.builder()
				.prime("asdfasdf")
				.paymentMethod("tappay")
				.lineUserId("asdfasdf")
				.productId(32)
				.receiverName("pypy")
				.receiverPhone("0912345678")
				.receiverAddress("桃園市八德區介壽路二段149號")
				.receiverZipcode("33442")
				.receiverEmail("asdf@gmail.com")
				.build()
		);
	}

}

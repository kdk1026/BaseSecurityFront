package com.kdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
class PasswordEncoderTest {

	@Test
	void test() {
		String sRawPassword = "qwer1234";

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String sEncodedPassword = bCryptPasswordEncoder.encode(sRawPassword);

		System.out.println(sEncodedPassword);
		assertEquals(true, bCryptPasswordEncoder.matches(sRawPassword, sEncodedPassword));
	}

}

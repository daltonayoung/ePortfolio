package testContactService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import contactService.Contact;

class ContactTest {

	@Test
	@DisplayName("Test that we can create a new Contact")
	void testContact() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertEquals(contact.getId(), "123456");
		Assertions.assertEquals(contact.getFirstName(), "John");
		Assertions.assertEquals(contact.getLastName(), "Doe");
		Assertions.assertEquals(contact.getPhoneNumber(), "5738675309");
		Assertions.assertEquals(contact.getAddress(), "11 Wall St, New York, NY 10005");
	}

	@Test
	@DisplayName("Test that an exception is thrown when we pass an ID longer than 10 characters")
	void testContactIdTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("12345678910", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown whwn we pass a null ID")
	void testContactIdNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact(null, "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we pass a first name longer than 10 charcaters")
	void testContactFirstNameTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "Constantine", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a null first name is passed")
	void testContactFirstNameNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", null, "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a first name longer than 10 characters is passed")
	void testContactLastNameTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Constantine", "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a null last name is passed")
	void testContactLastNameNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", null, "5738675309", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a phone number not equal to 10 characters is passed")
	void testContactPhoneNumberNot10Characters() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Doe", "57386753091", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a null phone number is passed")
	void testContactPhoneNumberNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Doe", null, "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a phone number containing alphanumeric charcaters is passed")
	void testContactPhoneNumberAlphanumeric() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Doe", "abcd-57861", "11 Wall St, New York, NY 10005");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when an address longer than 30 charcaters is passed")
	void testContactAddressTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Doe", "5738675309", "1600 Pennsylvania Avenue NW, Washington, DC 20500");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a null address is passed")
	void testContactAddressNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Contact("123456789", "John", "Doe", "5738675309", null);
		});
	}

	@Test
	@DisplayName("Test that we can set the first name using setFirstName()")
	void testSetFirstName() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		contact.setFirstName("Dalton");
		Assertions.assertEquals("Dalton", contact.getFirstName());
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a null first name")
	void testSetFirstNameNull() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setFirstName(null);
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a first name longer than 10 characters")
	void tesSetFirstNameTooLong() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setFirstName("Dalton12304");
		});
	}

	@Test
	@DisplayName("Test that we can set the last name using setLastName()")
	void testSetLastName() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		contact.setLastName("Young");
		Assertions.assertEquals("Young", contact.getLastName());
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a null last name")
	void testSetLastNameNull() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setLastName(null);
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a last name longer than 10 characters")
	void testSetLastNameTooLong() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setLastName("Young123456");
		});
	}

	@Test
	@DisplayName("test that we can set a phone number using setPhoneNumber()")
	void testSetPhoneNumber() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		contact.setPhoneNumber("1234567891");
		Assertions.assertEquals("1234567891", contact.getPhoneNumber());
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a null phone number")
	void testSetPhoneNumberNull() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setPhoneNumber(null);
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a phone number not equal to 10 charcaters")
	void testSetPhoneNumberNot10Characters() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setPhoneNumber("123456789");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when a phone is set containing letters")
	void testSetPhoneNumberAlphanumeric() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setPhoneNumber("123456h789");
		});
	}

	@Test
	@DisplayName("Test that we can set an address using setAddress()")
	void testSetAddress() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		contact.setAddress("123 2nd, Mexico, MO 65265");
		Assertions.assertEquals("123 2nd, Mexico, MO 65265", contact.getAddress());
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set a null address")
	void testSetAddressNull() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setAddress(null);
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to set an address longer than 30 characters")
	void testSetAddressTooLong() {
		Contact contact = new Contact("123456", "John", "Doe", "5738675309", "11 Wall St, New York, NY 10005");
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			contact.setAddress("1 Broadway, St. Louis, MO 63101");
		});
	}
}

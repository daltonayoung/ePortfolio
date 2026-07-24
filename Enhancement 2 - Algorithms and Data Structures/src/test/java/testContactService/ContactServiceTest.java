package testContactService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import contactService.Contact;
import contactService.ContactService;

class ContactServiceTest {

	private ContactService service;

	@BeforeEach
	void createContactService() {
		this.service = new ContactService();
	}

	@Test
	@DisplayName("Test that we can add a Contact")
	void testAddContact() {
		Contact contact = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		service.addContact(contact);
		Assertions.assertEquals(service.getContact("0001"), contact);
	}

	@Test
	@DisplayName("Test that addContact generates sequential IDs when the caller does not supply one")
	void testAddContactGeneratesSequentialIds() {
		service.addContact("John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		service.addContact("Cheyenne", "Miller", "5731234567", "123 Lauren Ln, Naylor MO");

		Assertions.assertEquals("John", service.getContact("1").getFirstName());
		Assertions.assertEquals("Cheyenne", service.getContact("2").getFirstName());
	}

	@Test
	@DisplayName("Test that the generated-ID addContact still validates its fields")
	void testAddContactGeneratedIdValidatesFields() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.addContact("John", "Smith", "not-a-phone", "11 Broadway St, Springfield MO");
		});
	}

	@Test
	@DisplayName("Test that an exception is thrown when we try to add two Contacts with the same ID")
	void testAddContactIdNotUnique() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0001", "Cheyenne", "Miller", "5731234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		assertThrows(IllegalArgumentException.class, () -> {
			service.addContact(contact2);
		});
	}

	@Test
	@DisplayName("Test that we can delete a contact and that the correct one is deleted")
	void testDeleteContact() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Cheyenne", "Miller", "5731234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		service.addContact(contact2);

		service.deleteContact("0001");

		assertThrows(IllegalArgumentException.class, () -> {
			service.getContact("0001");
		});
		Assertions.assertEquals(service.getContact("0002"), contact2);
	}

	@Test
	@DisplayName("Test that an IllegalArgumentException is thrown when we try to delete a nonexistant contact")
	void testDeleteContactThatDoesNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.deleteContact("0005");
		});
	}

	@Test
	@DisplayName("Test that we can update a contact's first name")
	void testUpdateContactFirstName() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");

		service.addContact(contact1);
		service.updateFirstName("0001", "Brad");

		Assertions.assertEquals(service.getContact("0001").getFirstName(), "Brad");
	}

	@Test
	@DisplayName("Test that an IllegalArgumentException is thrown when we try to update a nonexistant contact's first name")
	void testUpdateFirstNameContactNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.updateFirstName("0005", "Ricky");
		});
	}

	@Test
	@DisplayName("Test that we can update a contact's last name")
	void testUpdateContactLastName() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");

		service.addContact(contact1);
		service.updateLastName("0001", "Jones");

		Assertions.assertEquals(service.getContact("0001").getLastName(), "Jones");
	}

	@Test
	@DisplayName("Test that an IllegalArgumentException is thrown when we try to update a nonexistant contact's last name")
	void testUpdateLastNameNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.updateLastName("0005", "Bobby");
		});
	}

	@Test
	@DisplayName("Test that we can update a contact's phone number")
	void testUpdateContactPhoneNumber() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");

		service.addContact(contact1);
		service.updatePhoneNumber("0001", "4171234567");

		Assertions.assertEquals(service.getContact("0001").getPhoneNumber(), "4171234567");
	}

	@Test
	@DisplayName("Test that an IllegalArgumentException is thrown when we try to update a nonexistant contact's phone numebr")
	void testUpdatePhoneNumberNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.updatePhoneNumber("0005", "4176861234");
		});
	}

	@Test
	@DisplayName("Test that we can update a contact's address")
	void testUpdateContactAddress() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");

		service.addContact(contact1);
		service.updateAddress("0001", "103 Main St, Poplar Bluff MO");

		Assertions.assertEquals(service.getContact("0001").getAddress(), "103 Main St, Poplar Bluff MO");
	}

	@Test
	@DisplayName("Test that an IllegalArgumentException is thrown when we try to update a nonexistant contact's address")
	void testUpdateAddressNotExist() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.updateAddress("0005", "100 1st Street, New York, New York 10002");
		});
	}

	@Test
	@DisplayName("Test that listAll returns contacts sorted by last name")
	void testListAllSortedByLastName() {
		Contact contact1 = new Contact("0001", "John", "Zimmerman", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Cheyenne", "Adams", "5731234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		service.addContact(contact2);

		List<Contact> all = service.listAll();

		Assertions.assertEquals("Adams", all.get(0).getLastName());
		Assertions.assertEquals("Zimmerman", all.get(1).getLastName());
	}

	@Test
	@DisplayName("Test that listAll stays correctly sorted after a contact in the middle is deleted")
	void testListAllStaysSortedAfterDelete() {
		Contact contact1 = new Contact("0001", "John", "Adams", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Cheyenne", "Miller", "5731234567", "123 Lauren Ln, Naylor MO");
		Contact contact3 = new Contact("0003", "Bob", "Zimmerman", "3141234567", "1 Main St, Anytown MO");

		service.addContact(contact1);
		service.addContact(contact2);
		service.addContact(contact3);

		service.deleteContact("0002");

		List<Contact> all = service.listAll();

		Assertions.assertEquals(2, all.size());
		Assertions.assertEquals("Adams", all.get(0).getLastName());
		Assertions.assertEquals("Zimmerman", all.get(1).getLastName());
	}

	@Test
	@DisplayName("Test that listAll reflects a contact's new position after its last name changes")
	void testListAllStaysSortedAfterRename() {
		Contact contact1 = new Contact("0001", "John", "Adams", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Cheyenne", "Miller", "5731234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		service.addContact(contact2);

		service.updateLastName("0001", "Zimmerman");

		List<Contact> all = service.listAll();

		Assertions.assertEquals("Miller", all.get(0).getLastName());
		Assertions.assertEquals("Zimmerman", all.get(1).getLastName());
	}

	@Test
	@DisplayName("Test that deleting one of two contacts sharing a full name removes the correct one")
	void testDeleteContactWithDuplicateNameRemovesCorrectOne() {
		Contact contact1 = new Contact("0001", "Dalton", "Young", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Dalton", "Young", "4171234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		service.addContact(contact2);

		service.deleteContact("0001");

		List<Contact> all = service.listAll();

		Assertions.assertEquals(1, all.size());
		Assertions.assertEquals(contact2, all.get(0));
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.getContact("0001");
		});
	}

	@Test
	@DisplayName("Test that deleting from a block of three contacts sharing a name still finds the exact one")
	void testDeleteContactWithThreeDuplicateNamesRemovesCorrectOne() {
		Contact contact1 = new Contact("0001", "Dalton", "Young", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Dalton", "Young", "4171234567", "123 Lauren Ln, Naylor MO");
		Contact contact3 = new Contact("0003", "Dalton", "Young", "3141234567", "1 Main St, Anytown MO");

		service.addContact(contact1);
		service.addContact(contact2);
		service.addContact(contact3);

		service.deleteContact("0001");

		List<Contact> all = service.listAll();

		Assertions.assertEquals(2, all.size());
		Assertions.assertTrue(all.contains(contact2));
		Assertions.assertTrue(all.contains(contact3));
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			service.getContact("0001");
		});
	}

	@Test
	@DisplayName("Test that searchByName finds a contact by last name, case-insensitively")
	void testSearchByNameFindsContact() {
		Contact contact = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		service.addContact(contact);

		ArrayList<Contact> matches = service.searchByName("smith");

		Assertions.assertEquals(1, matches.size());
		Assertions.assertEquals(contact, matches.get(0));
	}

	@Test
	@DisplayName("Test that searchByName excludes contacts whose last name doesn't match")
	void testSearchByNameExcludesNonMatches() {
		Contact match = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		Contact nonMatch = new Contact("0002", "Jane", "Doe", "4171234567", "123 Lauren Ln, Naylor MO");

		service.addContact(match);
		service.addContact(nonMatch);

		ArrayList<Contact> matches = service.searchByName("Smith");

		Assertions.assertEquals(1, matches.size());
		Assertions.assertTrue(matches.contains(match));
	}

	@Test
	@DisplayName("Test that searchByName matches a last-name prefix, including a longer name that starts with it")
	void testSearchByNamePrefixMatch() {
		Contact contact1 = new Contact("0001", "Dalton", "Young", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "John", "Young", "4171234567", "123 Lauren Ln, Naylor MO");
		Contact contact3 = new Contact("0003", "Bob", "Youngerest", "3141234567", "1 Main St, Anytown MO");

		service.addContact(contact1);
		service.addContact(contact2);
		service.addContact(contact3);

		ArrayList<Contact> matches = service.searchByName("Young");

		Assertions.assertEquals(3, matches.size());
		Assertions.assertTrue(matches.contains(contact1));
		Assertions.assertTrue(matches.contains(contact2));
		Assertions.assertTrue(matches.contains(contact3));
	}

	@Test
	@DisplayName("Test that searchByName returns every contact sharing the same last name")
	void testSearchByNameFindsMultipleMatches() {
		Contact contact1 = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		Contact contact2 = new Contact("0002", "Jane", "Smith", "4171234567", "123 Lauren Ln, Naylor MO");

		service.addContact(contact1);
		service.addContact(contact2);

		ArrayList<Contact> matches = service.searchByName("Smith");

		Assertions.assertEquals(2, matches.size());
		Assertions.assertTrue(matches.contains(contact1));
		Assertions.assertTrue(matches.contains(contact2));
	}

	@Test
	@DisplayName("Test that searchByName returns an empty list when no last name matches, among other contacts")
	void testSearchByNameNotFound() {
		Contact contact = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		service.addContact(contact);

		ArrayList<Contact> matches = service.searchByName("Nobody");

		Assertions.assertTrue(matches.isEmpty());
	}

	@Test
	@DisplayName("Test that a renamed contact is findable under its new last name and not its old one")
	void testSearchByNameFindsContactAfterRename() {
		Contact contact = new Contact("0001", "John", "Smith", "5731234567", "11 Broadway St, Springfield MO");
		service.addContact(contact);

		service.updateLastName("0001", "Johnson");

		ArrayList<Contact> matches = service.searchByName("Johnson");
		Assertions.assertEquals(1, matches.size());
		Assertions.assertEquals(contact, matches.get(0));

		ArrayList<Contact> oldNameMatches = service.searchByName("Smith");
		Assertions.assertTrue(oldNameMatches.isEmpty());
	}

	@Test
	@DisplayName("Test that searchByName returns an empty list when given a null prefix")
	void testSearchByNameNullPrefix() {
		ArrayList<Contact> matches = service.searchByName(null);

		Assertions.assertTrue(matches.isEmpty());
	}

	@Test
	@DisplayName("Test that searchByName returns an empty list when the prefix is longer than any valid last name")
	void testSearchByNamePrefixTooLong() {
		ArrayList<Contact> matches = service.searchByName("ThisIsWayTooLong");

		Assertions.assertTrue(matches.isEmpty());
	}

	@Test
	@DisplayName("Test that searchByName's forward expansion stops at a non-matching contact, not just the end of the list")
	void testSearchByNameStopsExpandingAtNonMatchWithMoreContactsAfter() {
		Contact before = new Contact("0001", "John", "Adams", "5731234567", "11 Broadway St, Springfield MO");
		Contact match = new Contact("0002", "Dalton", "Young", "4171234567", "123 Lauren Ln, Naylor MO");
		Contact after = new Contact("0003", "Bob", "Zabriskie", "3141234567", "1 Main St, Anytown MO");

		service.addContact(before);
		service.addContact(match);
		service.addContact(after);

		ArrayList<Contact> matches = service.searchByName("Young");

		Assertions.assertEquals(1, matches.size());
		Assertions.assertTrue(matches.contains(match));
	}

}

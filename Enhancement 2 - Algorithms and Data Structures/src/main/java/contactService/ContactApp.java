package contactService;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Command-line interface for the Contact Service. Provides a menu-driven way to add,
 * list, search, update, and delete contacts without needing to already know a contact's
 * ID, since ContactService generates IDs internally. Calls into the existing
 * ContactService rather than reimplementing any of its logic.
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class ContactApp {

	/**
	 * Entry point. Runs the menu loop until the user selects Exit.
	 *
	 * @param args Not used
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		ContactService contactService = new ContactService();
		boolean running = true;

		while (running) {
			displayMenu();
			String choice = scanner.nextLine().trim();
			System.out.println();

			switch (choice) {
				case "1":
					handleAdd(scanner, contactService);
					break;
				case "2":
					handleListAll(contactService);
					break;
				case "3":
					handleSearchByName(scanner, contactService);
					break;
				case "4":
					handleUpdate(scanner, contactService);
					break;
				case "5":
					handleDelete(scanner, contactService);
					break;
				case "6":
					running = false;
					break;
				default:
					System.out.println("Invalid choice.");
			}
		}

		scanner.close();
	}

	/**
	 * Prints the main menu
	 */
	private static void displayMenu() {
		System.out.println();
		System.out.println("1) Add contact");
		System.out.println("2) List all contacts");
		System.out.println("3) Search by name");
		System.out.println("4) Update contact");
		System.out.println("5) Delete contact");
		System.out.println("6) Exit");
		System.out.print("Choose an option: ");
	}

	/**
	 * Prints a single contact's ID and fields
	 *
	 * @param contact The contact to display
	 */
	private static void displayContact(Contact contact) {
		System.out.println("ID: " + contact.getId()
			+ " | " + contact.getFirstName() + " " + contact.getLastName()
			+ " | " + contact.getPhoneNumber()
			+ " | " + contact.getAddress());
	}

	/**
	 * Prompts for a new contact's fields one at a time, re-prompting immediately if a
	 * field is rejected rather than waiting until every field has been entered. Uses a
	 * throwaway Contact purely as a scratchpad, validating each field through its real
	 * setter so the validation rule itself is never duplicated here. Once every field is
	 * valid, adds the contact with ContactService generating the real ID internally.
	 *
	 * @param scanner Used to read user input
	 * @param contactService The service to add the contact to
	 */
	private static void handleAdd(Scanner scanner, ContactService contactService) {
		Contact draft = new Contact("0", "-", "-", "0000000000", "-");

		promptUntilValid(scanner, "First name (max " + Contact.MAX_NAME_LENGTH + " characters): ", draft::setFirstName);
		promptUntilValid(scanner, "Last name (max " + Contact.MAX_NAME_LENGTH + " characters): ", draft::setLastName);
		promptUntilValid(scanner, "Phone number (exactly " + Contact.PHONE_NUMBER_LENGTH + " digits): ", draft::setPhoneNumber);
		promptUntilValid(scanner, "Address (max " + Contact.MAX_ADDRESS_LENGTH + " characters): ", draft::setAddress);

		contactService.addContact(draft.getFirstName(), draft.getLastName(), draft.getPhoneNumber(), draft.getAddress());
		System.out.println("Contact added.");
	}

	/**
	 * Prompts for a value and applies it with the given setter, redisplaying the prompt
	 * and the rejection message if it's invalid, until a valid value is accepted.
	 *
	 * @param scanner Used to read user input
	 * @param prompt The text to display before reading input
	 * @param setter The Contact setter to validate and apply the entered value with
	 */
	private static void promptUntilValid(Scanner scanner, String prompt, Consumer<String> setter) {
		while (true) {
			System.out.print(prompt);
			String value = scanner.nextLine();

			try {
				setter.accept(value);
				return;
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Displays every contact, sorted by last name then first name.
	 *
	 * @param contactService The service to list contacts from
	 */
	private static void handleListAll(ContactService contactService) {
		ArrayList<Contact> contacts = contactService.listAll();

		if (contacts.isEmpty()) {
			System.out.println("No contacts to display.");
			return;
		}

		for (Contact contact : contacts) {
			displayContact(contact);
		}
	}

	/**
	 * Prompts for a full name and displays every contact that matches it. More than one
	 * contact can share a full name, so every match is shown rather than just the first.
	 *
	 * @param scanner Used to read user input
	 * @param contactService The service to search
	 */
	private static void handleSearchByName(Scanner scanner, ContactService contactService) {
		System.out.print("Name to search for: ");
		String name = scanner.nextLine();

		ArrayList<Contact> matches = contactService.searchByName(name);

		if (matches.isEmpty()) {
			System.out.println("No contacts found matching that name.");
			return;
		}

		for (Contact contact : matches) {
			displayContact(contact);
		}
	}

	/**
	 * Looks up a contact by ID, then repeatedly displays its fields and a submenu to
	 * edit one field at a time until the user cancels.
	 *
	 * @param scanner Used to read user input
	 * @param contactService The service to update the contact in
	 */
	private static void handleUpdate(Scanner scanner, ContactService contactService) {
		System.out.print("ID of the contact to update: ");
		String id = scanner.nextLine();

		Contact contact;
		try {
			contact = contactService.getContact(id);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return;
		}

		boolean editing = true;
		while (editing) {
			displayContact(contact);
			System.out.println("1) First name (max " + Contact.MAX_NAME_LENGTH + " characters)");
			System.out.println("2) Last name (max " + Contact.MAX_NAME_LENGTH + " characters)");
			System.out.println("3) Phone number (exactly " + Contact.PHONE_NUMBER_LENGTH + " digits)");
			System.out.println("4) Address (max " + Contact.MAX_ADDRESS_LENGTH + " characters)");
			System.out.println("5) Cancel");
			System.out.print("Field to update: ");
			String fieldChoice = scanner.nextLine().trim();

			if (fieldChoice.equals("5")) {
				editing = false;
				continue;
			}

			System.out.print("New value: ");
			String newValue = scanner.nextLine();

			try {
				switch (fieldChoice) {
					case "1":
						contactService.updateFirstName(id, newValue);
						break;
					case "2":
						contactService.updateLastName(id, newValue);
						break;
					case "3":
						contactService.updatePhoneNumber(id, newValue);
						break;
					case "4":
						contactService.updateAddress(id, newValue);
						break;
					default:
						System.out.println("Invalid choice.");
						continue;
				}
				System.out.println("Updated.");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Looks up a contact by ID, displays it, and asks for confirmation before deleting.
	 *
	 * @param scanner Used to read user input
	 * @param contactService The service to delete the contact from
	 */
	private static void handleDelete(Scanner scanner, ContactService contactService) {
		System.out.print("ID of the contact to delete: ");
		String id = scanner.nextLine();

		Contact contact;
		try {
			contact = contactService.getContact(id);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return;
		}

		displayContact(contact);
		System.out.print("Delete this contact? (y/n): ");
		String answer = scanner.nextLine().trim();

		if (answer.equalsIgnoreCase("n")) {
			System.out.println("Cancelled.");
			return;
		}

		contactService.deleteContact(id);
		System.out.println("Contact deleted.");
	}
}

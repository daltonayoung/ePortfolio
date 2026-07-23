package contactService;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * ContactService stores and manages Contacts. Each contact must have a unique ID
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class ContactService {
	/**
	 * ArrayList to store the Contacts
	 */
	private ArrayList<Contact> contacts;

	/**
	 * Counter used by generateNextId() to assign IDs to contacts added without one
	 */
	private int nextId;

	/**
	 * Initialize a new instance
	 */
	public ContactService() {
		this.contacts = new ArrayList<Contact>();
		this.nextId = 1;
	}

	/**
	 * Gets the index of the contact in the contacts ArrayList. It will return -1 if the Contact is not found
	 *
	 * @param id The unique ID of the contact to search for
	 * @return The index of the contact if it is found or -1 if it is not found
	 */
	private int getContactIndex(String id) {
		// Set index to -1
		int index = -1;

		// Loop through the list and compare the id of the contact to the id we are looking for
		// If the id is found, set index equal to i and break from the loop
		for (int i = 0; i < this.contacts.size(); i++) {
			if (this.contacts.get(i).getId().equals(id)) {
				index = i;
				break;
			}
		}

		return index;
	}

	/**
	 * Adds a contact to the list.
	 * @param newContact The Contact to add to the list. Its ID must be unique
	 */
	public void addContact(Contact newContact) {
		// Search for the Contact in the list. If -1 is not returned we know the ID is not unique
		int index = this.getContactIndex(newContact.getId());

		// Throw an IllegalArgumentException if the ID is not unique
		if(index != -1) {
			throw new IllegalArgumentException("ID is not unique");
		}

		this.contacts.add(newContact);
	}

	/**
	 * Generates the next contact ID from an internal counter
	 *
	 * @return The next available ID
	 */
	private String generateNextId() {
		return String.valueOf(this.nextId++);
	}

	/**
	 * Adds a new contact, generating its ID internally instead of requiring the caller to supply one.
	 * This corrects a design flaw in addContact(Contact), where the caller effectively had to choose
	 * its own primary key with no way to know what ID was actually safe to use. addContact(Contact)
	 * is left in place since it's still a legitimate lower-level API, and existing callers rely on it.
	 *
	 * @param firstName Contact's first name. Cannot be null or more than 10 characters
	 * @param lastName Contact's last name. Cannot be null or more than 10 characters
	 * @param phoneNumber Contact's phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 * @param address Contact's address. Cannot be null or more than 30 characters
	 */
	public void addContact(String firstName, String lastName, String phoneNumber, String address) {
		Contact newContact = new Contact(this.generateNextId(), firstName, lastName, phoneNumber, address);
		this.addContact(newContact);
	}

	/**
	 * Deletes a Contact from the list
	 *
	 * @param contactId The ID of the Contact to delete
	 */
	public void deleteContact(String contactId) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		this.contacts.remove(index);
	}

	/**
	 * Updates a Contact's first name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param firstName Contact's new first name. Cannot be null or more than 10 characters
	 */
	public void updateFirstName(String contactId, String firstName) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		this.contacts.get(index).setFirstName(firstName);
	}

	/**
	 * Updates a Contact's last name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param lastName Contact's new last name. Cannot be null or more than 10 characters
	 */
	public void updateLastName(String contactId, String lastName) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		this.contacts.get(index).setLastName(lastName);
	}

	/**
	 * Updates a Contact's phone number given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param phoneNumber Contact's new phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 */
	public void updatePhoneNumber(String contactId, String phoneNumber) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		this.contacts.get(index).setPhoneNumber(phoneNumber);
	}

	/**
	 * Updates a Contact's address given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param address Contact's new address. Cannot be null or more than 30 characters
	 */
	public void updateAddress(String contactId, String address) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		this.contacts.get(index).setAddress(address);
	}

	/**
	 * Returns a Contact given its unique ID
	 *
	 * @param contactId The unique ID of the contact
	 * @return The Contact
	 */
	public Contact getContact(String contactId) {
		// Get the index of the Contact
		int index = this.getContactIndex(contactId);

		// If the index is -1 then the Contact is not in the list, so throw an IllegalArgumentException
		if (index == -1) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		return this.contacts.get(index);
	}

	/**
	 * Returns all contacts sorted alphabetically by last name, then first name. Sorts a fresh
	 * copy of the list on every call rather than maintaining a sorted structure.
	 *
	 * @return All contacts, sorted by last name and then first name
	 */
	public ArrayList<Contact> listAll() {
		ArrayList<Contact> sorted = new ArrayList<Contact>(this.contacts);
		sorted.sort(Comparator.comparing(Contact::getLastName).thenComparing(Contact::getFirstName));

		return sorted;
	}

	/**
	 * Finds every contact whose full name (first name and last name combined) contains
	 * the given text. Matching is case-insensitive and by substring, so a partial name,
	 * just a first name or just a last name, still finds matches.
	 *
	 * @param name Text to search for within each contact's full name
	 * @return All contacts whose full name contains that text, or an empty list if none match
	 */
	public ArrayList<Contact> searchByName(String name) {
		ArrayList<Contact> matches = new ArrayList<Contact>();

		// Linear scan checking each contact's combined first and last name for the query, case-insensitively
		for (int i = 0; i < this.contacts.size(); i++) {
			Contact contact = this.contacts.get(i);
			String fullName = contact.getFirstName() + " " + contact.getLastName();

			if (fullName.toLowerCase().contains(name.toLowerCase())) {
				matches.add(contact);
			}
		}

		return matches;
	}
}

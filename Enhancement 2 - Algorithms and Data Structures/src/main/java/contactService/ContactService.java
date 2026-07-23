package contactService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

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
	 * HashMap keyed by contact ID, giving O(1) lookups for getContact, update, and delete
	 * instead of a linear scan. Holds references to the same Contact objects as contacts,
	 * not copies, so mutating a Contact through one structure is visible through the other.
	 */
	private HashMap<String, Contact> contactsById;

	/**
	 * Counter used by generateNextId() to assign IDs to contacts added without one
	 */
	private int nextId;

	/**
	 * Initialize a new instance
	 */
	public ContactService() {
		this.contacts = new ArrayList<Contact>();
		this.contactsById = new HashMap<String, Contact>();
		this.nextId = 1;
	}

	/**
	 * Adds a contact to the list.
	 * @param newContact The Contact to add to the list. Its ID must be unique
	 */
	public void addContact(Contact newContact) {
		// If the id is already a key, it's not unique
		if (this.contactsById.containsKey(newContact.getId())) {
			throw new IllegalArgumentException("ID is not unique");
		}

		this.contacts.add(newContact);
		this.contactsById.put(newContact.getId(), newContact);
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
		// getContact throws IllegalArgumentException if the contact does not exist
		Contact contact = this.getContact(contactId);

		this.contacts.remove(contact);
		this.contactsById.remove(contactId);
	}

	/**
	 * Updates a Contact's first name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param firstName Contact's new first name. Cannot be null or more than 10 characters
	 */
	public void updateFirstName(String contactId, String firstName) {
		this.getContact(contactId).setFirstName(firstName);
	}

	/**
	 * Updates a Contact's last name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param lastName Contact's new last name. Cannot be null or more than 10 characters
	 */
	public void updateLastName(String contactId, String lastName) {
		this.getContact(contactId).setLastName(lastName);
	}

	/**
	 * Updates a Contact's phone number given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param phoneNumber Contact's new phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 */
	public void updatePhoneNumber(String contactId, String phoneNumber) {
		this.getContact(contactId).setPhoneNumber(phoneNumber);
	}

	/**
	 * Updates a Contact's address given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param address Contact's new address. Cannot be null or more than 30 characters
	 */
	public void updateAddress(String contactId, String address) {
		this.getContact(contactId).setAddress(address);
	}

	/**
	 * Returns a Contact given its unique ID
	 *
	 * @param contactId The unique ID of the contact
	 * @return The Contact
	 */
	public Contact getContact(String contactId) {
		Contact contact = this.contactsById.get(contactId);

		// A null return from the map means the id isn't a key, so the contact does not exist
		if (contact == null) {
			throw new IllegalArgumentException("Contact does not exist");
		}

		return contact;
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

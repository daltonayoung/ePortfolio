package contactService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * ContactService stores and manages Contacts. Each contact must have a unique ID
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class ContactService {
	/**
	 * ArrayList to store the Contacts. Still needed by searchByName's linear scan for now
	 */
	private ArrayList<Contact> contacts;

	/**
	 * HashMap keyed by contact ID, giving O(1) lookups for getContact, update, and delete
	 * instead of a linear scan. Holds references to the same Contact objects as contacts,
	 * not copies, so mutating a Contact through one structure is visible through the other.
	 */
	private HashMap<String, Contact> contactsById;

	/**
	 * ArrayList kept sorted by last name, then first name, at all times. Same Contact
	 * references as contacts and contactsById, not copies. Maintained incrementally through
	 * insertIntoSorted/removeFromSorted so listAll doesn't need to sort on every call.
	 */
	private ArrayList<Contact> contactsByName;

	/**
	 * Comparator used to keep contactsByName sorted and to binary search it
	 */
	private static final Comparator<Contact> nameComparator =
		Comparator.comparing(Contact::getLastName).thenComparing(Contact::getFirstName);

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
		this.contactsByName = new ArrayList<Contact>();
		this.nextId = 1;
	}

	/**
	 * Finds the correct position for a contact in contactsByName using binary search and
	 * inserts it there, keeping the list sorted without a full re-sort
	 *
	 * @param contact The contact to insert
	 */
	private void insertIntoSorted(Contact contact) {
		int index = Collections.binarySearch(this.contactsByName, contact, nameComparator);

		// A negative result is the insertion point (encoded as -(insertion point) - 1)
		// binarySearch returns when no exact match is found
		if (index < 0) {
			index = -(index + 1);
		}

		this.contactsByName.add(index, contact);
	}

	/**
	 * Removes a contact from contactsByName using binary search to find it
	 *
	 * @param contact The contact to remove
	 */
	private void removeFromSorted(Contact contact) {
		int index = Collections.binarySearch(this.contactsByName, contact, nameComparator);

		// binarySearch only guarantees landing on *a* contact that compares equal (the same
		// last and first name), not necessarily this exact object, since two contacts can
		// share a name. Walk back to the start of that equal-comparing block, then scan
		// forward to find the exact reference being removed.
		while (index > 0 && nameComparator.compare(this.contactsByName.get(index - 1), contact) == 0) {
			index--;
		}
		while (this.contactsByName.get(index) != contact) {
			index++;
		}

		this.contactsByName.remove(index);
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
		this.insertIntoSorted(newContact);
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
		this.removeFromSorted(contact);
	}

	/**
	 * Updates a Contact's first name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param firstName Contact's new first name. Cannot be null or more than 10 characters
	 */
	public void updateFirstName(String contactId, String firstName) {
		Contact contact = this.getContact(contactId);

		// Removed using the old name before mutating, since removeFromSorted needs the old
		// sort key to find it. The finally block guarantees it's always reinserted, either
		// with the new name if setFirstName succeeds, or with the unchanged name if it
		// throws, since setFirstName validates before assigning
		this.removeFromSorted(contact);
		try {
			contact.setFirstName(firstName);
		} finally {
			this.insertIntoSorted(contact);
		}
	}

	/**
	 * Updates a Contact's last name given its unique ID
	 *
	 * @param contactId The unique ID of the Contact
	 * @param lastName Contact's new last name. Cannot be null or more than 10 characters
	 */
	public void updateLastName(String contactId, String lastName) {
		Contact contact = this.getContact(contactId);

		this.removeFromSorted(contact);
		try {
			contact.setLastName(lastName);
		} finally {
			this.insertIntoSorted(contact);
		}
	}

	/**
	 * Updates a Contact's phone number given its unique ID. Doesn't touch contactsByName,
	 * phone number isn't part of the sort key
	 *
	 * @param contactId The unique ID of the Contact
	 * @param phoneNumber Contact's new phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 */
	public void updatePhoneNumber(String contactId, String phoneNumber) {
		this.getContact(contactId).setPhoneNumber(phoneNumber);
	}

	/**
	 * Updates a Contact's address given its unique ID. Doesn't touch contactsByName,
	 * address isn't part of the sort key
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
	 * Returns all contacts sorted alphabetically by last name, then first name. contactsByName
	 * is already kept sorted at all times, so this just returns it directly instead of sorting
	 * a copy on every call. Returns an unmodifiable view rather than a defensive copy, keeping
	 * this O(1) while still not exposing the real internal list for a caller to mutate
	 *
	 * @return All contacts, sorted by last name and then first name
	 */
	public List<Contact> listAll() {
		return Collections.unmodifiableList(this.contactsByName);
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

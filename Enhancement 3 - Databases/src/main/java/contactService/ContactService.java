package contactService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * ContactService stores and manages Contacts, persisting them to a SQLite database. Each
 * contact must have a unique ID
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class ContactService {
	/**
	 * HashMap keyed by contact ID, giving O(1) lookups for getContact, update, and delete
	 * instead of a linear scan. Holds references to the same Contact objects as
	 * contactsByName, not copies, so mutating a Contact through one structure is visible
	 * through the other.
	 */
	private HashMap<String, Contact> contactsById;

	/**
	 * ArrayList kept sorted by last name, then first name, at all times. Same Contact
	 * references as contactsById, not copies. Maintained incrementally through
	 * insertIntoSorted/removeFromSorted so listAll doesn't need to sort on every call.
	 */
	private ArrayList<Contact> contactsByName;

	/**
	 * Comparator used to keep contactsByName sorted and to binary search it
	 */
	private static final Comparator<Contact> nameComparator =
		Comparator.comparing((Contact contact) -> contact.getLastName().toLowerCase())
			.thenComparing(contact -> contact.getFirstName().toLowerCase());

	/**
	 * Counter used by generateNextId() to assign IDs to contacts added without one
	 */
	private int nextId;

	/**
	 * JDBC connection to the SQLite database backing this service, opened once during
	 * construction and held for the lifetime of this instance
	 */
	private Connection connection;

	/**
	 * Initializes a new instance backed by a private, in-memory SQLite database that exists
	 * only for the lifetime of this instance and is never written to disk. This is the
	 * default so existing callers, including every test written before this enhancement,
	 * stay isolated from each other and from any real database file
	 */
	public ContactService() {
		this.initialize("jdbc:sqlite::memory:");
	}

	/**
	 * Initializes a new instance backed by a SQLite database file at the given path,
	 * creating the file and the contacts table if they don't already exist. Used for real
	 * persistence, such as ContactApp's actual usage, or for opening two separate instances
	 * against the same file to confirm data survives a restart
	 *
	 * @param databaseFilePath Path to the SQLite database file
	 */
	public ContactService(String databaseFilePath) {
		this.initialize("jdbc:sqlite:" + databaseFilePath);
	}

	/**
	 * Shared setup for both constructors: initializes the in-memory structures, opens the
	 * database connection, creates the contacts table if it doesn't already exist, and loads
	 * any existing rows into contactsById and contactsByName
	 *
	 * @param jdbcUrl The JDBC connection URL to open
	 * @throws ContactPersistenceException If the connection, table creation, or initial load fails
	 */
	private void initialize(String jdbcUrl) {
		this.contactsById = new HashMap<String, Contact>();
		this.contactsByName = new ArrayList<Contact>();
		this.nextId = 1;

		try {
			this.connection = DriverManager.getConnection(jdbcUrl);
			this.createContactsTableIfNotExists();
			this.loadContactsFromDatabase();
		} catch (SQLException e) {
			throw new ContactPersistenceException("Failed to initialize the contacts database", e);
		}
	}

	/**
	 * Creates the contacts table if it doesn't already exist, so both a fresh in-memory
	 * database and a pre-existing file-backed database work the same way
	 *
	 * @throws SQLException If the table creation fails
	 */
	private void createContactsTableIfNotExists() throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS contacts ("
			+ "id TEXT PRIMARY KEY, "
			+ "first_name TEXT, "
			+ "last_name TEXT, "
			+ "phone_number TEXT, "
			+ "address TEXT)";

		try (Statement statement = this.connection.createStatement()) {
			statement.execute(sql);
		}
	}

	/**
	 * Loads every existing row from the contacts table into contactsById and contactsByName,
	 * so a file-backed database's contacts are available immediately on startup. Not
	 * parameterized since it takes no input, the query is a fixed literal with nothing to
	 * inject
	 *
	 * @throws SQLException If reading from the database fails
	 */
	private void loadContactsFromDatabase() throws SQLException {
		String sql = "SELECT id, first_name, last_name, phone_number, address FROM contacts";

		try (Statement statement = this.connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				Contact contact = new Contact(
					resultSet.getString("id"),
					resultSet.getString("first_name"),
					resultSet.getString("last_name"),
					resultSet.getString("phone_number"),
					resultSet.getString("address"));

				this.contactsById.put(contact.getId(), contact);
				this.insertIntoSorted(contact);
			}
		}
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
	 * Finds every contact whose last name starts with the given prefix, case-insensitively,
	 * using binary search against contactsByName instead of a linear scan. A prefix match
	 * works because everything sharing a prefix sorts contiguously in a list sorted by last 
	 * name, so binary search can land inside that block, then a walk outward collects every 
	 * match. No last name can exceed Contact.MAX_NAME_LENGTH, so a longer prefix can never match
	 * anything and is rejected immediately without searching.
	 *
	 * @param lastNamePrefix The last name, or the start of it, to search for
	 * @return All contacts whose last name starts with that prefix, or an empty list if none match
	 */
	public ArrayList<Contact> searchByName(String lastNamePrefix) {
		ArrayList<Contact> matches = new ArrayList<Contact>();

		if (lastNamePrefix == null || lastNamePrefix.length() > Contact.MAX_NAME_LENGTH) {
			return matches;
		}

		String prefix = lastNamePrefix.toLowerCase();

		// A placeholder Contact used purely to carry the prefix into the comparator below,
		// never actually stored anywhere
		Contact key = new Contact("0", "-", lastNamePrefix, "0000000000", "-");

		// Treats a contact as "equal" to the key if its last name starts with the prefix,
		// case-insensitively, otherwise orders by the same case-insensitive rule as
		// nameComparator, so binarySearch stays consistent with how the list is actually sorted
		Comparator<Contact> prefixComparator = (contact, k) -> {
			String contactLastName = contact.getLastName().toLowerCase();

			if (contactLastName.startsWith(prefix)) {
				return 0;
			}

			return contactLastName.compareTo(k.getLastName().toLowerCase());
		};

		int anchor = Collections.binarySearch(this.contactsByName, key, prefixComparator);

		if (anchor < 0) {
			return matches;
		}

		// binarySearch only guarantees landing somewhere inside the matching block, so walk
		// back to its start, then collect forward until the prefix no longer matches
		int start = anchor;
		while (start > 0 && this.contactsByName.get(start - 1).getLastName().toLowerCase().startsWith(prefix)) {
			start--;
		}

		int end = anchor;
		while (end < this.contactsByName.size() - 1 && this.contactsByName.get(end + 1).getLastName().toLowerCase().startsWith(prefix)) {
			end++;
		}

		for (int i = start; i <= end; i++) {
			matches.add(this.contactsByName.get(i));
		}

		return matches;
	}
}

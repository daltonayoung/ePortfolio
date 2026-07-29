package contactService;

/**
 * A contact object that has a unique ID, first name, last name, phone number, and address.
 * <br>
 * For use by the ContactService class
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class Contact {
	/**
	 * Maximum allowed length for a contact's ID
	 */
	private static final int MAX_ID_LENGTH = 10;

	/**
	 * Maximum allowed length for a contact's first or last name. Public so callers (such
	 * as ContactApp) can display the limit without duplicating it as a separate literal.
	 */
	public static final int MAX_NAME_LENGTH = 10;

	/**
	 * Required length for a contact's phone number. Public so callers (such as
	 * ContactApp) can display the limit without duplicating it as a separate literal.
	 */
	public static final int PHONE_NUMBER_LENGTH = 10;

	/**
	 * Maximum allowed length for a contact's address. Public so callers (such as
	 * ContactApp) can display the limit without duplicating it as a separate literal.
	 */
	public static final int MAX_ADDRESS_LENGTH = 30;

	/**
	 * A unique ID for the contact
	 */
	private String id;

	/**
	 * The contact's first name
	 */
	private String firstName;

	/**
	 * The contact's last name
	 */
	private String lastName;

	/**
	 * The contact's phone number
	 */
	private String phoneNumber;

	/**
	 * The contact's address
	 */
	private String address;

	/**
	 * Constructor. Returns a Contact object
	 *
	 * @param id Unique ID. Cannot be null or more than 10 characters
	 * @param firstName Contact's first name. Cannot be null or more than 10 characters
	 * @param lastName Contact's last name. Cannot be null or more than 10 characters
	 * @param phoneNumber Contact's phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 * @param address Contact's address. Cannot be null or more than 30 characters
	 */
	public Contact(String id, String firstName, String lastName, String phoneNumber, String address) {
		// id has no setter, since it's immutable after construction, so it's validated here directly
		if(id == null || id.length() > MAX_ID_LENGTH) {
			throw new IllegalArgumentException("Invalid ID");
		}
		this.id = id;

		// Delegate to the setters instead of duplicating their validation here
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPhoneNumber(phoneNumber);
		this.setAddress(address);
	}

	/**
	 * Return the contact's ID
	 *
	 * @return Contact's unique ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the contact's first name
	 *
	 * @return The contact's first name
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Return the contact's last name
	 *
	 * @return The contact's last name
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Return the contact's phone number
	 *
	 * @return The contact's phone number
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Return the contact's address
	 *
	 * @return The contact's address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Set the contact's first name
	 *
	 * @param firstName Contact's first name. Cannot be null or more than 10 characters
	 */
	public void setFirstName(String firstName) {
		if(firstName == null || firstName.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid First Name");
		}

		this.firstName = firstName;
	}

	/**
	 * Set the contact's last name
	 *
	 * @param lastName Contact's last name. Cannot be null or more than 10 characters
	 */
	public void setLastName(String lastName) {
		if(lastName == null || lastName.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid Last Name");
		}

		this.lastName = lastName;
	}

	/**
	 * Set the contact's phone number
	 *
	 * @param phoneNumber Contact's phone number. Cannot be null, must be exactly 10 characters, and can only contain the digits 0-9
	 */
	public void setPhoneNumber(String phoneNumber) {
		if(phoneNumber == null || phoneNumber.length() != PHONE_NUMBER_LENGTH || !phoneNumber.matches("[0-9]+")) {
			throw new IllegalArgumentException("Invalid Phone Number");
		}

		this.phoneNumber = phoneNumber;
	}

	/**
	 * Set the contact's address
	 *
	 * @param address Contact's address. Cannot be null or more than 30 characters
	 */
	public void setAddress(String address) {
		if(address == null || address.length() > MAX_ADDRESS_LENGTH) {
			throw new IllegalArgumentException("Invalid Address");
		}

		this.address = address;
	}
}

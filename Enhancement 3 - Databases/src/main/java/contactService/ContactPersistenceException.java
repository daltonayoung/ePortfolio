package contactService;

/**
 * Thrown when a database operation backing ContactService fails. Wraps the underlying
 * SQLException as its cause rather than exposing java.sql.SQLException itself, since
 * SQLException is checked and every other ContactService method is unchecked. Keeping this
 * unchecked means adding persistence doesn't force try/catch or throws onto any existing
 * caller, including every test written before this enhancement.
 *
 * @author Dalton Young <dalton.young@snhu.edu>
 *
 */
public class ContactPersistenceException extends RuntimeException {
	/**
	 * Constructs a new instance
	 *
	 * @param message Description of what operation failed
	 * @param cause The underlying SQLException that caused the failure
	 */
	public ContactPersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
}

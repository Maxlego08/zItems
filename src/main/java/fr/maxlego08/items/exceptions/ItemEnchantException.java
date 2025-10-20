/**
 * 
 */
package fr.maxlego08.items.exceptions;

/**
 * @author Maxlego08
 *
 */
public class ItemEnchantException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemEnchantException() {
	}

	public ItemEnchantException(String message) {
		super(message);
	}

	public ItemEnchantException(Throwable cause) {
		super(cause);
	}

	public ItemEnchantException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemEnchantException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

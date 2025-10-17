package fr.maxlego08.items.exceptions;

public class InventoryAlreadyExistException extends Error {

	public InventoryAlreadyExistException() {
		super();
	}

	public InventoryAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InventoryAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public InventoryAlreadyExistException(String message) {
		super(message);
	}

	public InventoryAlreadyExistException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5611455794293458580L;

}

package fr.maxlego08.items.exceptions;

public class InventoryOpenException extends Exception {

	

	public InventoryOpenException() {
		super();
	}

	public InventoryOpenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InventoryOpenException(String message, Throwable cause) {
		super(message, cause);
	}

	public InventoryOpenException(String message) {
		super(message);
	}

	public InventoryOpenException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;

}

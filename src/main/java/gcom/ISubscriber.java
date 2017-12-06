package gcom;

public interface ISubscriber {
	/**
	 * This method will be called in order to deliver new messages to the
	 * subscriber.
	 * 
	 * @param message
	 *            the new message
	 */
	public <T> void deliverMessage(T message);
}

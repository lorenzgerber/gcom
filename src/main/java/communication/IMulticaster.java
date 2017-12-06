package communication;

import java.util.List;

import order.Message;

public interface IMulticaster {
	/**
	 * Send a message to all recipients.
	 * 
	 * @param message
	 *            the message to send
	 * @return a list of IDs of nodes that failed to receive the message
	 */
	public List<Integer> multicast(Message<?> message);
}

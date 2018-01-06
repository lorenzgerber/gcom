package communication;

import java.util.List;

import gcom.INode;
import order.Message;

public interface IMulticaster {
	/**
	 * Send a message to all recipients.
	 * 
	 * @param message
	 *            the message to send
	 * @return a list of nodes that failed to receive the message
	 */
	public List<INode> multicast(Message<?> message);

	/**
	 * Get the current performance count.
	 * 
	 * @return the number of messages required to send a message
	 */
	public int debugPerformance();
}

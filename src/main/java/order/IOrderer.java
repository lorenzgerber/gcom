package order;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import communication.IMulticaster;
import gcom.INode;
import gcom.ISubscriber;

public interface IOrderer {
	/**
	 * Add any needed clocks or signatures to the message and pass it on the the
	 * multicaster for sending.
	 * 
	 * @param message
	 *            the message to send
	 * @return a list of failed nodes
	 */
	public List<INode> send(Message<?> message);

	/**
	 * Receive a message and order it correctly before delivering it to subscribers.
	 * 
	 * @param message
	 *            the received message
	 * @return true if successful
	 */
	public boolean receive(Message<?> message);

	/**
	 * Add a subscriber that should get ordered messages.
	 * 
	 * @param subscriber
	 *            the new subscriber
	 */
	public void subscribe(ISubscriber subscriber);

	/**
	 * Remove the specified subscriber.
	 * 
	 * @param subscriber
	 *            the subscriber
	 */
	public void unSubscribe(ISubscriber subscriber);

	/**
	 * Set the multicaster that should be used for sending out messages.
	 * 
	 * @param multicaster
	 *            the multicaster
	 */
	public void setMulticaster(IMulticaster multicaster);

	/**
	 * Set the id of this orderer. Needed for orderers that use vector clocks.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(UUID id);

	/**
	 * Reset the orderer. This should be called when joining a new group.
	 */
	public void reset();

	/**
	 * Notify the orderer of a member that has left the group.
	 * 
	 * @param id
	 *            the id of the now gone member
	 */
	public void removeMember(UUID id);

	/**
	 * Get the number of messages sent from this orderer.
	 * 
	 * @return the number of sent messages or -1 if no counter is used
	 */
	public long debugGetMessagesSent();

	/**
	 * Get the vector clock from this orderer.
	 * 
	 * @return the vector clock or null if not used
	 */
	public HashMap<UUID, Long> debugGetVectorClock();

	/**
	 * Get a list of messages kept in buffer.
	 * 
	 * @return buffered messages, or null if not used
	 */
	public List<Message<?>> debugGetBuffer();

	/**
	 * Subscribe for debug notifications.
	 * 
	 * @param subscriber
	 *            the subscriber to be notified
	 */
	public void debugSubscribe(IDebugOrdererSubscriber subscriber);

	/**
	 * Get a performance count.
	 * 
	 * @return the number of messages required to send a message
	 */
	public int getPerformance();
}

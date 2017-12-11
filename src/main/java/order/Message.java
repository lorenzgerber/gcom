package order;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import gcom.INode;

public class Message<T> implements Serializable {
	public final T data;
	protected int sender;
	protected HashMap<Integer, Long> vectorClock;
	private Collection<INode> recipients = Collections.emptyList();
	private static final long serialVersionUID = 8148518950441165743L;

	public Message(T data) {
		this.data = data;
	}

	public Collection<INode> getRecipients() {
		return recipients;
	}

	public void setRecipients(Collection<INode> recipients) {
		this.recipients = recipients;
	}

	protected void setVectorClock(HashMap<Integer, Long> vectorClock) {
		this.vectorClock = vectorClock;
	}

	protected HashMap<Integer, Long> getVectorClock() {
		return vectorClock;
	}

}

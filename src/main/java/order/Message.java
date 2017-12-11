package order;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import gcom.INode;

public class Message<T> implements Serializable {
	public final T data;
	protected int sender;
	protected HashMap<Integer, Long> vectorClock;
	private List<INode> recipients = Collections.emptyList();
	private static final long serialVersionUID = 8148518950441165743L;

	public Message(T data) {
		this.data = data;
	}

	public List<INode> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<INode> recipients) {
		this.recipients = recipients;
	}

	protected void setVectorClock(HashMap<Integer, Long> vectorClock) {
		this.vectorClock = vectorClock;
	}

	protected HashMap<Integer, Long> getVectorClock() {
		return vectorClock;
	}

}

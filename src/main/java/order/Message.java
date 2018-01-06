package order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import gcom.INode;

public class Message<T extends Serializable> implements Serializable {
	public final T data;
	protected UUID sender;
	private HashMap<UUID, Long> vectorClock;
	private ArrayList<INode> recipients = new ArrayList<>();
	private static final long serialVersionUID = 8148518950441165743L;

	public Message(T data) {
		this.data = data;
	}

	public ArrayList<INode> getRecipients() {
		return recipients;
	}

	public void setRecipients(ArrayList<INode> recipients) {
		this.recipients = recipients;
	}

	protected void setVectorClock(HashMap<UUID, Long> vectorClock) {
		this.vectorClock = vectorClock;
	}

	protected HashMap<UUID, Long> getVectorClock() {
		return vectorClock;
	}

}

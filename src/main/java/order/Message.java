package order;

import java.util.Collections;
import java.util.List;

import gcom.INode;

public class Message<T> {
	public final int id;
	public final T data;
	private List<INode> recipients = Collections.emptyList();

	public Message(int id, T data) {
		this.id = id;
		this.data = data;
	}

	public List<INode> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<INode> recipients) {
		this.recipients = recipients;
	}

}

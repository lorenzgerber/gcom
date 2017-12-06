package gcom;

public interface ISubscriber {
	public <T> void deliverMessage(T message);
}

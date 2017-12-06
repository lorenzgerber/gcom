package gcom;

import communication.IMulticaster;
import order.IOrderer;

public interface GCom {
	public void join(String group);

	public <T> void Send(T data);

	public void leave();

	public void subscribe(ISubscriber subscriber);

	public void setConfig(IOrderer orderer, IMulticaster multicaster);
}

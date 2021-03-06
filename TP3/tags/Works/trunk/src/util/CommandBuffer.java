package util;

import java.util.LinkedList;

public class CommandBuffer<T> {

	private LinkedList<T> commands;

	public CommandBuffer() {
		this.commands = new LinkedList<T>();
	}

	public synchronized T getCommand() {
		while (commands.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		notifyAll();
		return commands.remove();
	}

	public synchronized void putCommand(T cmd) {
		commands.add(cmd);
		notifyAll();
	}

	public boolean isEmpty() {
		return (commands.size() == 0);
	}

}

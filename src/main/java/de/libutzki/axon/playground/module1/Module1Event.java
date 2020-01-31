package de.libutzki.axon.playground.module1;

import de.libutzki.axon.playground.axon.embedded.eventstore.LocalEvent;

@LocalEvent
public class Module1Event {
	private final String payload;

	public Module1Event( final String payload ) {
		this.payload = payload;
	}

	public String getPayload( ) {
		return payload;
	}

	@Override
	public String toString( ) {
		return "<" + this.getClass( ).getSimpleName( ) + "> " + payload;
	}
}
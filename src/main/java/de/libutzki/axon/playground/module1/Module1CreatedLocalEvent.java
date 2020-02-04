package de.libutzki.axon.playground.module1;

import de.libutzki.axon.playground.axon.embedded.outbox.LocalEvent;

@LocalEvent
public class Module1CreatedLocalEvent {
	private final String id;

	public Module1CreatedLocalEvent( final String id ) {
		this.id = id;
	}

	public String getId( ) {
		return id;
	}

	@Override
	public String toString( ) {
		return "<" + this.getClass( ).getSimpleName( ) + "> " + id;
	}
}
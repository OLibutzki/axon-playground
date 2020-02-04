package de.libutzki.axon.playground.module1;

public class Module1CreatedGlobalEvent {
	private final String id;

	public Module1CreatedGlobalEvent( final String id ) {
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
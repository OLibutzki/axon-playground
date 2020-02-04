package de.libutzki.axon.playground.module1;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class Module1Command {
	@TargetAggregateIdentifier
	private final String payload;

	public Module1Command( final String payload ) {
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
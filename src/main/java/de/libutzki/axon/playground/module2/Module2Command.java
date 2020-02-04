
package de.libutzki.axon.playground.module2;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class Module2Command {
	@TargetAggregateIdentifier
	private final String payload;

	public Module2Command( final String payload ) {
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
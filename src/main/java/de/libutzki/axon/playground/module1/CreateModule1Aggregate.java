
package de.libutzki.axon.playground.module1;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateModule1Aggregate {

	@TargetAggregateIdentifier
	private final String id;

	public CreateModule1Aggregate( final String id ) {
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
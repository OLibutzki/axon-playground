package de.libutzki.axon.playground.axon.embedded.eventstore;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.axonframework.common.stream.BlockingStream;

public class FilteringBlockingStream<M> implements BlockingStream<M> {

	private final BlockingStream<M> delegate;
	private final Predicate<M> filter;

	public FilteringBlockingStream( final BlockingStream<M> delegate, final Predicate<M> filter ) {
		this.delegate = delegate;
		this.filter = filter;
	}

	@Override
	public Optional<M> peek( ) {
		Optional<M> nextElementOptional = delegate.peek( );
		while ( nextElementOptional.isPresent( ) ) {
			final Optional<M> filteredElement = nextElementOptional.filter( filter );
			if ( filteredElement.isPresent( ) ) {
				return filteredElement;
			}
			try {
				delegate.nextAvailable( );
			} catch ( InterruptedException e ) {
				// Ignore
			}
			nextElementOptional = delegate.peek( );
		}
		return Optional.empty( );
	}

	@Override
	public boolean hasNextAvailable( final int timeout, final TimeUnit unit ) throws InterruptedException {
		if ( timeout > 0 ) {
			try {
				Awaitility.await( ).atMost( timeout, unit ).until( ( ) -> peek( ), Optional::isPresent );
				return true;
			} catch ( ConditionTimeoutException e ) {
				return false;
			}
		} else {
			return peek( ).isPresent( );
		}
	}

	@Override
	public M nextAvailable( ) throws InterruptedException {
		return delegate.nextAvailable( );
	}

	@Override
	public void close( ) {
		delegate.close( );
	}

}

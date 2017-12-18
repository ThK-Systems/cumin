/*
 * tksCommons
 *
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A wrapping atomic (and thread-safe) counter using an {@link AtomicLong}.
 */
public final class WrappingAtomicCounter extends Number {

    private static final long serialVersionUID = -4963435702981072671L;

    private final AtomicLong counter = new AtomicLong();

    private final long initialValue;

    private final long maxValue;

    /**
     * Constructs with 'max value' of {@link Long#MAX_VALUE} and 'initial value' of 0.
     */
    public WrappingAtomicCounter() {
        this(Long.MAX_VALUE);
    }

    /**
     * Constructs with given 'max value' and 'initial value' of 0.
     */
    public WrappingAtomicCounter(long maxValue) {
        this(maxValue, 0l);
    }

    /**
     * Constructs with given 'max value' and given 'initial value'.
     */
    public WrappingAtomicCounter(long maxValue, long initialValue) {
        if (initialValue > maxValue) {
            throw new IllegalArgumentException("Initial value must not be larger than max value.");
        }
        this.maxValue = maxValue;
        this.initialValue = initialValue;
        counter.set(initialValue);
    }

    /**
     * Function used for incrementing counter.
     */
    private LongUnaryOperator getIncrementFunction() {
        return value -> value >= maxValue ? initialValue : value + 1;
    }

    /**
     * Returns current counter value (starting with 'initial value') and (after that) increments the counter.
     * <p>
     * If the counter reaches 'max value' the counter wraps the 'inital value'
     */
    public long getAndIncrement() {
        return counter.getAndUpdate(getIncrementFunction());
    }

    /**
     * Just increments and wraps the counter, if necessary (without returning a value).
     *
     * @see #getAndIncrement()
     */
    public void increment() {
        getAndIncrement();
    }

    /**
     * Like {@link #getAndIncrement()}, but the incremented (or wrapped) value is returned.
     */
    public long incrementAndGet() {
        return counter.updateAndGet(getIncrementFunction());
    }

    /**
     * Function used for decrementing counter.
     */
    private LongUnaryOperator getDecrementFunction() {
        return value -> value <= initialValue ? maxValue : value - 1;
    }

    /**
     * Gets the current counter value and (after that) decrements the counter value.
     * <p>
     * If the counter value is smaller than 'inital value' it is wrapped to 'max value'.
     */
    public long getAndDecrement() {
        return counter.getAndUpdate(getDecrementFunction());
    }

    /**
     * Just decrements (and wraps) the counter (without returning a value).
     */
    public void decrement() {
        getAndDecrement();
    }

    /**
     * Like {@link #getAndDecrement()}, but the decremented (or wrapped) value is returned.
     */
    public long decrementAndGet() {
        return counter.updateAndGet(getDecrementFunction());
    }

    /**
     * Returns the current value of the counter,
     */
    public long get() {
        return counter.get();
    }

    /**
     * Sets the counter to the given value.
     */
    public void set(long newValue) {
        if (newValue > this.maxValue || newValue < initialValue) {
            throw new IllegalArgumentException(
                    "The value to set must not be larger than the max value of the counter and not be smaller than the initial value of the counter.");
        }
        counter.set(newValue);
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public int intValue() {
        return counter.intValue();
    }

    @Override
    public long longValue() {
        return counter.longValue();
    }

    @Override
    public float floatValue() {
        return counter.floatValue();
    }

    @Override
    public double doubleValue() {
        return counter.doubleValue();
    }

    @Override
    public byte byteValue() {
        return counter.byteValue();
    }

    @Override
    public short shortValue() {
        return counter.shortValue();
    }
}

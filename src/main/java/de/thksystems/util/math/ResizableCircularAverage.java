package de.thksystems.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import de.thksystems.util.lang.Deferred;

/**
 * A bounded, resizable highly-optimized queue of {@link BigDecimal}s to get its average (and sum) of all included values.
 * <p>
 * Not thread safe. :(
 */
public class ResizableCircularAverage implements Iterable<BigDecimal> {

    private final LinkedList<BigDecimal> list = new LinkedList<>();
    private int elementsCount;

    private BigDecimal sum = BigDecimal.ZERO;
    private final Deferred<BigDecimal> average = new Deferred<>(() -> sum.divide(BigDecimal.valueOf(list.size()), 8, RoundingMode.HALF_UP));

    /**
     * Creates the queue with its initial (binding) size.
     */
    public ResizableCircularAverage(int elementsCount) {
        assert elementsCount > 0 : "elementsCount must be positive (>0)";
        this.elementsCount = elementsCount;
    }

    /**
     * Resizes the queue. If the queue is smaller after resizing, excessive elements will be removed (from the HEAD on).
     */
    public void resize(int newElementsCount) {
        assert newElementsCount > 0 : "newElementsCount must be positive (>0)";
        if (newElementsCount < elementsCount && list.size() > newElementsCount) {
            for (int i = 0; i < elementsCount - newElementsCount; i++) {
                sum = sum.subtract(list.poll());
            }
        }
        this.elementsCount = newElementsCount;
        average.invalidate();
    }

    /**
     * Adds the given element to the queue, and remove the HEAD element of the queue (to keep the size of the queue).
     */
    public void add(BigDecimal value) {
        assert value != null : "value must be not null";
        list.add(value);
        sum = sum.add(value);
        if (list.size() > elementsCount) {
            sum = sum.subtract(list.poll());
        }
        average.invalidate();
    }

    /**
     * Removes the given element from the queue. (In the consequence, the queue will not be full anymore.)
     */
    public void remove(BigDecimal value) {
        assert value != null : "value must be not null";
        list.remove(value);
        sum = sum.subtract(value);
        average.invalidate();
    }

    /**
     * Returns the sum of all values of the queue.
     */
    public BigDecimal sum() {
        return this.sum;
    }

    /**
     * Returns the average of all values of the queue. (The average sum of the values will be cached.)
     */
    public BigDecimal average() {
        return this.average.get();
    }

    /**
     * Returns true, if the queue is empty.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Returns true, if the queue is full (related to its size).
     */
    public boolean isFull() {
        return list.size() == elementsCount;
    }

    /**
     * Returns the {@link Iterator} of the queue.
     *
     * @see Iterable#iterator()
     */
    @Override
    public Iterator<BigDecimal> iterator() {
        return list.iterator();
    }

    /**
     * For each element of the queue, process the given action.
     *
     * @see Iterable#forEach(Consumer)
     */
    @Override
    public void forEach(Consumer<? super BigDecimal> action) {
        list.forEach(action);
    }
}
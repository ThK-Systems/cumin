package de.thksystems.util.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

public class WrappingAtomicCounterTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testIncrementAndDecrement() {
		WrappingAtomicCounter wac = new WrappingAtomicCounter(3);
		assertEquals(0l, wac.getAndIncrement());
		assertEquals(1l, wac.getAndIncrement());
		assertEquals(2, wac.intValue());
		wac.increment();
		assertEquals(3l, wac.get());
		assertEquals(3l, wac.getAndIncrement());
		assertEquals(0l, wac.getAndIncrement());

		wac = new WrappingAtomicCounter(5, 5);
		assertEquals(5l, wac.getAndIncrement());
		assertEquals(5l, wac.getAndIncrement());
		assertEquals(5l, wac.getAndIncrement());

		wac = new WrappingAtomicCounter(5);
		assertEquals(0l, wac.get());
		wac.set(4l);
		assertEquals(4l, wac.getAndIncrement());
		assertEquals(0l, wac.incrementAndGet());

		wac = new WrappingAtomicCounter(3, 1);
		assertEquals(3l, wac.decrementAndGet());
		assertEquals(1l, wac.incrementAndGet());
		assertEquals(2l, wac.incrementAndGet());
		wac.decrement();
		assertEquals(1l, wac.getAndDecrement());
		assertEquals(3l, wac.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLargerThanMax() {
		WrappingAtomicCounter wac = new WrappingAtomicCounter(5l);
		wac.set(6l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSmallerThanInitial() {
		WrappingAtomicCounter wac = new WrappingAtomicCounter(5l, 1l);
		wac.set(0l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitialLargerThanMax() {
		new WrappingAtomicCounter(5l, 6l);
	}

}

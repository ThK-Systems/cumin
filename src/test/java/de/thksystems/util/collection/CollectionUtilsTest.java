package de.thksystems.util.collection;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.thksystems.util.collection.CollectionUtils;

public class CollectionUtilsTest {

	@Test
	public void testCreateArrayList() {
		List<String> list1 = CollectionUtils.createArrayList("1", "2", "3");
		assertEquals(3, list1.size());
	}
}

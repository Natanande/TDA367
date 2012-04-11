package com.github.joakimpersson.tda367.model.tiles.walkable;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.joakimpersson.tda367.model.constants.Attribute;
import com.github.joakimpersson.tda367.model.tiles.walkable.SpeedUpdateItem;

/**
 * 
 * @author joakimpersson
 * 
 */
public class SpeedUpdateItemTest {

	private SpeedUpdateItem item;

	@Before
	public void setUp() throws Exception {
		item = new SpeedUpdateItem();
	}

	@Test
	public void testGetAttr() {
		Attribute attr = item.getAttr();
		assertEquals(Attribute.Speed, attr);
	}

	@After
	public void tearDown() throws Exception {
		item = null;
	}
}
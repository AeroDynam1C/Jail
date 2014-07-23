package test.java.com.graywolf336.jail;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.graywolf336.jail.Util;

public class TestUtilClass {
	private static List<String> list;
	private static Vector bottomCorner;
	private static Vector topCorner;
	
	@BeforeClass
	public static void setUp() throws Exception {
		list = new ArrayList<String>();
		list.add(Material.SEEDS.toString());
		list.add("coal_ore");
		list.add("torch");
		bottomCorner = new Vector(-10.50, 50.25, 100.00);
		topCorner = new Vector(50, 100, 250);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		bottomCorner = null;
		topCorner = null;
		list = null;
	}
	
	@Test
	public void testIsInsideAB() {
		Vector inside = new Vector(35, 64, 110);
		assertTrue(Util.isInsideAB(inside, bottomCorner, topCorner));
	}
	
	@Test
	public void testIsOutsideAB() {
		Vector outside = new Vector(350, 15, 350);
		assertFalse(Util.isInsideAB(outside, bottomCorner, topCorner));
	}
	
	@Test
	public void testHalfInHalfOutsideAB() {
		Vector halfAndHalf = new Vector(25, 75, 99);
		assertFalse(Util.isInsideAB(halfAndHalf, bottomCorner, topCorner));
	}
	
	@Test
	public void testInList() {
		assertTrue(Util.isStringInsideList(list, "seeds"));
		assertTrue(Util.isStringInsideList(list, Material.COAL_ORE.toString()));
		assertTrue(Util.isStringInsideList(list, "tOrCh"));
	}
	
	@Test
	public void testNotInList() {
		assertFalse(Util.isStringInsideList(list, "dirt"));
		assertFalse(Util.isStringInsideList(list, "SAND"));
		assertFalse(Util.isStringInsideList(list, Material.BEDROCK.toString()));
	}
	
	@Test
	public void testNoFormat() throws Exception {
		assertEquals(60000L, Util.getTime("1"), 0);
		assertEquals(360000L, Util.getTime("6"), 0);
	}
	
	@Test
	public void testSeconds() throws Exception {
		assertEquals(2000L, Util.getTime("2s"), 0);
		assertEquals(2000L, Util.getTime("2second"), 0);
		assertEquals(2000L, Util.getTime("2seconds"), 0);
	}
	
	@Test
	public void testMinutes() throws Exception {
		assertEquals(60000L, Util.getTime("1m"), 0);
		assertEquals(60000L, Util.getTime("1minute"), 0);
		assertEquals(60000L, Util.getTime("1minutes"), 0);
	}
	
	@Test
	public void testHours() throws Exception {
		assertEquals(3600000L, Util.getTime("1h"), 0);
		assertEquals(3600000L, Util.getTime("1hours"), 0);
	}
	
	@Test
	public void testDays() throws Exception {
		assertEquals(86400000L, Util.getTime("1d"), 0);
		assertEquals(86400000L, Util.getTime("1days"), 0);
	}
}

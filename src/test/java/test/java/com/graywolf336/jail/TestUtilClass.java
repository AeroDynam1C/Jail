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
}

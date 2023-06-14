package client.map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.exceptions.InvalidMapException;

class CoordinatesTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void constructorTest_positivXY_True() {
		Coordinates testCoordinates = new Coordinates(1, 1);
		assertEquals(testCoordinates.getX(), 1);
		assertEquals(testCoordinates.getY(), 1);
		assertEquals(testCoordinates, testCoordinates.getCoordinates());
	}

	// negative 
	@Test
	void constructorTest_negativXY_True() throws Exception{
		assertThrows(IllegalArgumentException.class, () -> {
			Coordinates testCoordinates = new Coordinates(-1, 0);
	        throw new IllegalArgumentException();
	    });
	}


}

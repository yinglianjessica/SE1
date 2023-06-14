package client.map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.exceptions.InvalidMapException;

class HalfMapTest {

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

	// negativ test
	@Test
	public void halfMapConstructorTest_Null_ThrowsException() {
	    assertThrows(InvalidMapException.class, () -> {
			HalfMap testMap = new HalfMap(null);
	        throw new InvalidMapException();
	    });
	}
	
	

}

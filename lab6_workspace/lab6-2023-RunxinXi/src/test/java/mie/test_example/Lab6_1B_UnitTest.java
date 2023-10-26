package mie.test_example;

import org.junit.BeforeClass;					// using annotation BeforeClass for the filename of the linked BPMN diagram

public class Lab6_1B_UnitTest extends Lab6_1A_UnitTest {

	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/lab6_1B.bpmn";
	}
}
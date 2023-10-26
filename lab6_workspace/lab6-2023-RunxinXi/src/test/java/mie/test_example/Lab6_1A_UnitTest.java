package mie.test_example;

import static org.junit.Assert.assertNotNull;	// assertNotNull test function for JUnit
import static org.junit.Assert.assertTrue;		// assertTrue test function for JUnit

import java.sql.Connection;				// Database connection
import java.sql.ResultSet;				// Retrieving and Modifying Values from Result Sets
import java.sql.SQLException;			// Exception feature for SQL database
import java.sql.Statement;				// Using SQL statement

import java.util.List;					// using List
import java.util.ArrayList;				// using ArrayList
import java.util.HashMap;				// using HashMap

import org.flowable.engine.RuntimeService;		// for RuntimeService
import org.flowable.engine.TaskService;			// using service task
import org.flowable.task.api.Task;				// using user task

import org.junit.BeforeClass;					// using annotation BeforeClass for the filename of the linked BPMN diagram
import org.junit.Test;							// using annotation Test for JavaUnitTest
import org.springframework.core.annotation.Order;

import edu.toronto.dbservice.exceptions.SQLExceptionHandler;	// Handling SQL exceptions
import edu.toronto.dbservice.config.MIE354DBHelper;				// Handling dll file for database
	
import edu.toronto.dbservice.types.Person;		// java type Person

public class Lab6_1A_UnitTest extends LabBaseUnitTest {

	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/lab6_1A.bpmn";
	}

	public void submitFormData() {
		// One way to complete any task (even if it includes as form) is 
		// to call the method TaskService.complete(taskid).
		// Another way to complete a task that has a form is to submit the task's form data.

		// form fields are filled using a map from form field ids to values
		HashMap<String, String> formEntries = new HashMap<String, String>();
		formEntries.put("num_rows", "2");
		
		// checks that the task's form fields have been assigned
		// get the user task (Pause Task)
		TaskService taskService = flowableContext.getTaskService();
		Task proposalsTask = taskService.createTaskQuery().taskDefinitionKey("usertask1")
				.singleResult();
		
		// submit the form (will lead to completing the user task)
		flowableContext.getFormService().submitTaskFormData(proposalsTask.getId(), formEntries);
	}
	
	@Test 
	public void testConfirmSalaries() {
		
		startProcess();
		submitFormData();		
		assertPendingTaskSize(1);
		testPersonList();
		//get it from parameterized ;
	
		ArrayList<Person> outputTable = getOutputTable();
		assertTrue(outputTable.size() > 0);
		System.out.println("outputTable:"+outputTable);
		for (Person p : outputTable) {
			assertTrue (p.newsalary == p.salary*1.5 );
			System.out.println("name: "+p.name);
			System.out.println("pnewsalary:"+p.newsalary);
			System.out.println("psalary:"+p.salary);
		}
		
	}

	// num_row is the value that you input in the submitFormData()
	// To get the first num_row rows from the database
	private ArrayList<Person> getOutputTable() {
		Connection dbCon = MIE354DBHelper.getDBConnection();
		
		Statement statement;
		ResultSet resultSet = null;
		ArrayList<Person> personList = new ArrayList<Person>();
		try {
			statement = dbCon.createStatement();
			
			// Select the updated value from the database
			resultSet = statement.executeQuery("SELECT people.name, people.salary, outputtable.salary as newsalary "
					+ "FROM outputtable, people WHERE people.name = outputtable.name");
			
			Integer i = 1;
			while (resultSet.next()) {
				String pName = resultSet.getString("name");
				int pSalary = resultSet.getInt("salary");
				int newsalary = resultSet.getInt("newsalary");
				Person p = new Person(pName, pSalary);
				p.newsalary = newsalary; 
				personList.add(p);
				i++;
			}

			resultSet.close();
			statement.close();
			dbCon.close();
			
		} catch (SQLException se) {
			SQLExceptionHandler.handleException(se);
		}
		return personList;
	}


	private void startProcess() {
		RuntimeService runtimeService = flowableContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process1");
	}
	

	
	public void testPersonList() {
		//testCheckPausedAtFirstUserTask();
		ArrayList<Person> plist = (ArrayList<Person>) flowableContext.getRuntimeService().getVariable(processInstance.getId(), "personList");
		assertTrue(plist.size() > 0);
	}
	
	
	private ArrayList<String> getPendingTaskNames() {
		ArrayList<String> taskListNames = new ArrayList<String>();
		for (Task t : flowableContext.getTaskService().createTaskQuery().list()) {
			taskListNames.add(t.getName());
		}
		return taskListNames;
	}

	private ArrayList<String> getPendingTaskIds() {
		ArrayList<String> taskListIds = new ArrayList<String>();
		for (Task t : flowableContext.getTaskService().createTaskQuery().list()) {
			taskListIds.add(t.getTaskDefinitionKey());
		}
		return taskListIds;
	}

	private void assertPendingTaskSize(int num) {
		List<Task> list3 = flowableContext.getTaskService().createTaskQuery().list();
		assertTrue(list3.size() == num);
	}

	private void completeAllPendingTasks() {
		List<Task> list2 = flowableContext.getTaskService().createTaskQuery().list();
		for (Task t : list2) {
			System.out.println("completing task:" + t.getName());
			flowableContext.getTaskService().complete(t.getId());
		}
	}

	private void showPendingTasks() {
		System.out.println("Printing pending tasks...");
		List<Task> list = flowableContext.getTaskService().createTaskQuery().list();
		if (list.size() == 0) {
			System.out.println("Pending task list size is zero.");
		}
		for (Task t : list) {
			System.out.println("pending task:" + t.getId() + ":" + t.getName());
		}
	}

}
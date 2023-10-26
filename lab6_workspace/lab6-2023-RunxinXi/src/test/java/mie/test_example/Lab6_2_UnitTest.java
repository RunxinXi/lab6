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

import edu.toronto.dbservice.exceptions.SQLExceptionHandler;	// Handling SQL exceptions
import edu.toronto.dbservice.config.MIE354DBHelper;				// Handling dll file for database
import edu.toronto.dbservice.types.Document;
import edu.toronto.dbservice.types.Person;		// java type Person

public class Lab6_2_UnitTest extends LabBaseUnitTest {

	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/lab6_2.bpmn";
	}

	
	public void submitFormData() {
		// One way to complete any task (even if it includes as form) is 
		// to call the method TaskService.complete(taskid).
		// Another way to complete a task that has a form is to submit the task's form data.

		// form fields are filled using a map from form field ids to values
		HashMap<String, String> formEntries = new HashMap<String, String>();
		formEntries.put("num_rows", "3");
		formEntries.put("newSalary", "50000"); // Example value
	    formEntries.put("dependentNumber", "2");
		
		// checks that the task's form fields have been assigned
		// get the user task (Pause Task)
		TaskService taskService = flowableContext.getTaskService();
		Task proposalsTask = taskService.createTaskQuery().taskDefinitionKey("usertask1")
				.singleResult();
		
		// submit the form (will lead to completing the user task)
		flowableContext.getFormService().submitTaskFormData(proposalsTask.getId(), formEntries);
	}
	
	@Test
	public void testCoPaymentCalculation() {
	    startProcess();
	    submitFormData();
	    
	    // Fetch the calculated co-payment from the process instance
	    Double coPayAmount = (Double) flowableContext.getRuntimeService().getVariable(processInstance.getId(), "coPayAmount");
	    
	    // Verify the co-payment calculation
	    Double expectedCoPay = 50000 * 0.0003; // 0.03% of the new salary
	    assertTrue(coPayAmount.equals(expectedCoPay));
	}
	
	@Test
	public void testProcessStarted() {
		startProcess();
	}
	
	
	@Test
	public void testCheckPausedAtFirstUserTask() {
		startProcess();
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());
		assertNotNull(processInstance);
		assertPendingTaskSize(1);
		ArrayList<String> pendingTaskNames = getPendingTaskNames();
		assertTrue(getPendingTaskNames().contains("Enter the number of persons"));
		ArrayList<String> pendingTaskIds = getPendingTaskIds();
		assertTrue(getPendingTaskIds().contains("usertask1"));
	}
	
	
	@Test
	public void testConfirmSalaries() {
		
		startProcess();
		submitFormData();		
		assertPendingTaskSize(1);


		testPersonList();
	
		ArrayList<Person> outputTable = getOutputTable();
		assertTrue(outputTable.size() > 0);
		System.out.println("outputTable:"+outputTable);
		for (Person p : outputTable) {
			assertTrue (p.newsalary == p.salary );
			System.out.println("name: "+p.name);
			System.out.println("pnewsalary:"+p.newsalary);
			System.out.println("psalary:"+p.salary);
		}


		testDocumentList();
		
		ArrayList<Document> documentTable = getDocumentTable();
		assertTrue(documentTable.size() > 0);
		System.out.println("documentTable:"+documentTable);
		for (Document d : documentTable) {
			System.out.println("docname: "+d.docname);

		}
		
		
	}
	
	
	@Test
	public void testProcessEnded() {
		testConfirmSalaries();
		completeAllPendingTasks();
		showPendingTasks();
		assertPendingTaskSize(0);
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
	
	public void testDocumentList() {
		//testCheckPausedAtFirstUserTask();
		ArrayList<Document> dlist = (ArrayList<Document>) flowableContext.getRuntimeService().getVariable(processInstance.getId(), "documentList");
		assertTrue(dlist.size() > 0);
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
			resultSet = statement.executeQuery("SELECT people.name, people.salary, people.dependent, outputtable.salary as newsalary "
					+ "FROM outputtable, people WHERE people.name = outputtable.name");	
			
			Integer i = 1;
			while (resultSet.next()) {
				String pName = resultSet.getString("name");
				int pSalary = resultSet.getInt("salary");
				int newsalary = resultSet.getInt("newsalary");
				int dependent = resultSet.getInt("dependent");
				Person p = new Person(pName, pSalary);
				p.newsalary = newsalary;
				p.dependent = dependent;
				personList.add(p);
				i++;
			}
			
			//

			resultSet.close();
			statement.close();
			dbCon.close();
			
		} catch (SQLException se) {
			SQLExceptionHandler.handleException(se);
		}
		
	
		
		return personList;
	}

	
	// To get items on the document table database
	private ArrayList<Document> getDocumentTable() {
		Connection dbCon = MIE354DBHelper.getDBConnection();
		
		Statement statement;
		ResultSet resultSet = null;
		ArrayList<Document> documentList = new ArrayList<Document>();
		try {
			statement = dbCon.createStatement();
			
			// Select the updated value from the database
			resultSet = statement.executeQuery("SELECT * FROM document");
			
			Integer i = 1;
			while (resultSet.next()) {
				String docName = resultSet.getString("docname");
				int docNumber = resultSet.getInt("docnumber");
				String docDescription = resultSet.getString("description");
				Document d = new Document(docName, docNumber, docDescription);
				documentList.add(d);
				i++;
			}

			resultSet.close();
			statement.close();
			dbCon.close();
			
		} catch (SQLException se) {
			SQLExceptionHandler.handleException(se);
		}
		return documentList;
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
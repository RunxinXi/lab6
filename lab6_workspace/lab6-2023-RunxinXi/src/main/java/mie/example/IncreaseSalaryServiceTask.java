package mie.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.Person;

public class IncreaseSalaryServiceTask implements JavaDelegate {

	Connection dbCon = null;

	@Override
	public void execute(DelegateExecution execution) {
		dbCon = MIE354DBHelper.getDBConnection();
		
		Person currentPerson = (Person) execution.getVariable("currentInstance");

		int updatedValue = (int) (currentPerson.salary * 1.5);

		Statement statement = null;
		try {
			statement = dbCon.createStatement();
			statement.execute("INSERT INTO OUTPUTTABLE " + "VALUES ('" + currentPerson.name + "'," + updatedValue +")");
			statement.close();
			dbCon.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

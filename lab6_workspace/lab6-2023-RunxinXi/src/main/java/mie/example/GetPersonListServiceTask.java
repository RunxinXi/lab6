package mie.example;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.Person;

public class GetPersonListServiceTask implements JavaDelegate {

	Connection dbCon = null;

	public GetPersonListServiceTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}

	@Override
	public void execute(DelegateExecution execution) {
		Statement statement = null;
		ResultSet resultSet = null;
		ArrayList<Person> personList = new ArrayList<Person>();
		
		String num_row = (String) execution.getVariable("num_rows");
		int num = Integer.parseInt(num_row);
		
		int i = 1 ;
		try {
			statement = dbCon.createStatement();
		
		// search for the person's name based on what was entered on the form
			resultSet = statement.executeQuery("SELECT * FROM people ORDER BY name");
			while (resultSet.next() && i <= num ) {
				String pName = resultSet.getString("name");
				int pSalary = resultSet.getInt("salary");
				int dependent = resultSet.getInt("dependent");
				Person p = new Person(pName, pSalary);
				p.dependent = dependent;
				personList.add(p);
				i++; 
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		execution.setVariable("personList", personList);
	}
}

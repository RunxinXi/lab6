package mie.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.Document;
import edu.toronto.dbservice.types.Person;

public class NewDocumentServiceTask implements JavaDelegate {

	//Connection dbCon = null;

	@Override
	public void execute(DelegateExecution execution) {
		
		Document currentDoc = (Document) execution.getVariable("currentDocInstance");
		Person currentPerson = (Person) execution.getVariable("currentInstance");
				
		
		for (int i = 1; i <= currentPerson.getDependent(); i++) { 
			System.out.println("Document -> " + currentDoc.getdocname() + " - has been created for: " + currentPerson.getName() + "'s " + i + " dependent out of " + currentPerson.getDependent() + " dependents");
			}


	}

}

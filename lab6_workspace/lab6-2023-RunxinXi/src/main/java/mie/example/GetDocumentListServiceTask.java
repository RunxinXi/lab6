package mie.example;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.Document;
import edu.toronto.dbservice.types.Person;

public class GetDocumentListServiceTask implements JavaDelegate {

	Connection dbCon = null;

	public GetDocumentListServiceTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}

	@Override
	public void execute(DelegateExecution execution) {
		Statement statement = null;
		ResultSet resultSet = null;
		ArrayList<Document> documentList = new ArrayList<Document>();
		
		// Number of Rows in the table Document
		int num = 3;

		int i = 1 ;
		try {
			statement = dbCon.createStatement();
		
		// search for the document name
			resultSet = statement.executeQuery("SELECT * FROM document ORDER BY docname");
			while (resultSet.next() && i <= num ) {
				String docName = resultSet.getString("docname");
				int docNumber = resultSet.getInt("docnumber");
				String docDescription = resultSet.getString("description");
				Document d = new Document(docName, docNumber, docDescription);
				documentList.add(d);
				i++; 
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		execution.setVariable("documentList", documentList);
	}
}

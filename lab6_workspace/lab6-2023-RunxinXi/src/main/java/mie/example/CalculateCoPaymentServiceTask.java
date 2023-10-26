package mie.example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class CalculateCoPaymentServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
	    Double newSalary = (Double) execution.getVariable("newSalary");
	    Integer dependentNumber = (Integer) execution.getVariable("dependentNumber");
	    String personName = (String) execution.getVariable("personName");

	    Double coPayAmount = newSalary * 0.0003; // 0.03% of the new salary

	    // TODO: Insert the tuple (personName, dependentNumber, coPayAmount) into the CoPayment database table

	    System.out.println("CoPayment calculated for " + personName + " with dependent number " + dependentNumber + " is: " + coPayAmount);
	}


}

package test;

import java.util.Vector;

import org.alldifferent.CSP;
import org.alldifferent.ConstraintAllDifferent;
import org.alldifferent.Domain;

import org.alldifferent.Variable;

import junit.framework.TestCase;
import junit.framework.TestResult;

public class TestCsp extends TestCase {

	
	private CSP c = new CSP();
	
	protected void setUp() throws Exception {
		
		
		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
		Vector valuesX1 = new Vector();
		Vector valuesX2 = new Vector();
		Vector valuesX3 = new Vector();
		
		valuesX1.add(0);
		valuesX1.add(1);
		valuesX1.add(2);
		valuesX2.add(0);
		valuesX2.add(1);
		valuesX2.add(2);
		valuesX3.add(0);
		valuesX3.add(1);
		valuesX3.add(2);
		valuesX3.add(3);
		
		Domain dX1 = new Domain();
		Domain dX2 = new Domain();
		Domain dX3 = new Domain();
		
		dX1.setValues(valuesX1);
		dX2.setValues(valuesX2);
		dX3.setValues(valuesX3);
		
		
		Variable x1 = new Variable(1,dX1);
		Variable x2 = new Variable(2,dX2);
		Variable x3 = new Variable(3,dX3);
		
		constraint.addVariable(x1); 
		constraint.addVariable(x2);
		constraint.addVariable(x3);
		
		c.setConstraint(constraint);
		System.out.println(c);
		boolean success = false;
		c.backtracking(0, success);
		assertEquals(true, success);
		System.out.println(success);
		System.out.println(c);
		
	}

	@Override
	public TestResult run() {
		// TODO Auto-generated method stub
		return super.run();
	}

}

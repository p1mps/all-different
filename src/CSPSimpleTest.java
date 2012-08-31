

import static org.junit.Assert.*;

import java.util.Vector;

import org.alldifferent.CSP;
import org.alldifferent.ConstraintAllDifferent;
import org.alldifferent.Domain;
import org.alldifferent.Variable;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.xml.internal.security.algorithms.Algorithm;

public class CSPSimpleTest {

	
	private CSP c = new CSP();
	
	@Before
	public void setUp() throws Exception {
		//CSP c = new CSP();
		ConstraintAllDifferent constraint = new ConstraintAllDifferent();
		Vector<Integer> valuesX1 = new Vector<Integer>();
		Vector<Integer> valuesX2 = new Vector<Integer>();
		Vector<Integer> valuesX3 = new Vector<Integer>();
		
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
		
		
		Variable x1 = new Variable(0,dX1);
		Variable x2 = new Variable(1,dX2);
		Variable x3 = new Variable(2,dX3);
		
		constraint.addVariable(x1); 
		constraint.addVariable(x2);
		constraint.addVariable(x3);
		
		c.setConstraint(constraint);
		
	}

	@Test
	public void test() {
		System.out.println(c);
		boolean success = false;
//		algo a;
//		c.backtracking(0, success,c);
		success = c.consistent();
		assertTrue(success);
		System.out.println(success);
		System.out.println(c);
	}

}

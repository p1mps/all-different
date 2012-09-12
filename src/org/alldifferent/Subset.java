package org.alldifferent;


import java.util.Vector;

public class Subset {
	
	public static Vector<Vector<Variable>> getSubsets(
			Vector<Variable> set) {

		Vector<Vector<Variable>> subsetCollection = new Vector<Vector<Variable>>();

		if (set.size() == 0) {
			subsetCollection.add(new Vector<Variable>());
		} else {
			Vector<Variable> reducedSet = new Vector<Variable>();

			reducedSet.addAll(set);

			Variable first = reducedSet.remove(0);
			Vector<Vector<Variable>> subsets = getSubsets(reducedSet);
			subsetCollection.addAll(subsets);

			subsets = getSubsets(reducedSet);

			for (Vector<Variable> subset : subsets) {
				subset.add(0, first);
			}

			subsetCollection.addAll(subsets);
		}

		return subsetCollection;
	}

}
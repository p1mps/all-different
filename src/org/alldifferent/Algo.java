package org.alldifferent;


public enum Algo{
	arc,
	bounds,
	range,
	bipartite;
	
	public static Algo getEnum(String s){
        if(arc.name().equals(s)){
            return arc;
        }else if(bounds.name().equals(s)){
            return bounds;
        }else if(range.name().equals(s)){
            return range;
        }else if (bipartite.name().equals(s)){
            return bipartite;
        }
        throw new IllegalArgumentException("No Enum specified for this string");
    }
	
		
}


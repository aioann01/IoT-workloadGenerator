package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import java.util.Comparator;

public class NumberComparator<T extends Number & Comparable> implements Comparator<T> {

    //Compare Number objects
    public int compare( T a, T b ) throws ClassCastException {
        return a.compareTo(b);
    }
}
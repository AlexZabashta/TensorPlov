
package core;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class BoundVarShape {
    public static BoundVarShape concat(BoundVarShape first, BoundVarShape secnd) {
        return new BoundVarShape(ArrayUtils.addAll(first.numBoundVars, secnd.numBoundVars));
    }

    final int[] numBoundVars;

    public BoundVarShape(int... numBoundVars) {
        this.numBoundVars = numBoundVars;
    }

    public BoundVarShape concat(BoundVarShape shape) {
        return concat(this, shape);
    }

    public double[][] createBoundVar() {
        double[][] boundVars = new double[numBoundVars.length][];
        for (int i = 0; i < numBoundVars.length; i++) {
            boundVars[i] = new double[numBoundVars[i]];
        }
        return boundVars;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BoundVarShape shape = (BoundVarShape) obj;
        return Arrays.equals(numBoundVars, shape.numBoundVars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(numBoundVars);
    }

    final int length() {
        return numBoundVars.length;
    }

    public int[] numBoundVars() {
        return numBoundVars.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(numBoundVars);
    }

}

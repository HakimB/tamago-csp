package tamago.csp.generic;

import java.lang.reflect.InvocationTargetException;

import tamago.builder.TamagoBuilder;
import tamago.csp.constraint.QuantifierConstraint;
import tamago.csp.exception.TamagoCSPException;
import tamagocc.util.Pair;

public interface CSPvariableForQuantifier extends CSPvar {

	void addQuantifierConstraint(QuantifierConstraint quant);
	Iterable<Pair<FDvar,TamagoBuilder>> getElements();
	void putElem() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TamagoCSPException;

}
/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.TamagoCSP;
import tamago.csp.convert.TamagoCSPDNF;
import tamago.csp.convert.TamagoCSPFlatten;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.CSPvariableForQuantifier;
import tamago.csp.generic.FDvar;
import tamagocc.api.TOpeName;
import tamagocc.exception.TamagoCCException;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GOperator;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIQuantifierVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class ExistConstraintArray implements CSPConstraint,
		QuantifierConstraint {

	private CSPvariableForQuantifier coll;
	private GIQuantifierVariable variable;
	private TamagoCSP csp;
	private TamagoEnvironment env;
	private boolean done;
	
	/**
	 * 
	 * @throws TamagoCCException 
	 * 
	 */
	public ExistConstraintArray(CSPvariableForQuantifier var, GIQuantifierVariable variable, TamagoCSP csp, TamagoEnvironment env) throws TamagoCCException {
		this.coll = var;
		this.variable = variable;
		this.csp = csp;
		this.env = env;
		done = false;
		coll.addQuantifierConstraint(this);
		try {
			coll.putElem();
		} catch (Exception e) {
			throw new TamagoCCException("Exists constraints failed",e);
		} 
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#getVariables()
	 */
	public Iterable<CSPvar> getVariables() {
		ArrayList<CSPvar> res = new ArrayList<CSPvar>(1);
		res.add(coll);
		return res;
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#size()
	 */
	public int size() {
		return 1;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("exists "); 
		sb.append(variable.getQuantifier().getVariable().getName());
		return sb.toString();
	}
	
	private static <T> T selector(ArrayList<T> arraydnf) {
		Random random = new Random();
		if(arraydnf.size() == 0) {
			TamagoCCLogger.println(3, "No more DNF in forall constraint");
			throw new TamagoCSPRuntime("No more DNF in forall constraint");
		}
		int pos = random.nextInt(arraydnf.size());
		T expr = arraydnf.get(pos);
		arraydnf.remove(pos);
		return expr;
	}
	
	/**
	 * @see tamago.csp.constraint.QuantifierConstraint#addVariable(tamago.csp.generic.FDvar, tamago.builder.TamagoBuilder)
	 */
	public void addVariable(FDvar idx, TamagoBuilder tb)
			throws TamagoCSPException {

		if(done)
			return;
		idx.setMustInstantiate(true);
		TamagoCSPInferConstraint infer = new TamagoCSPInferConstraint(csp,env);
		TamagoBuilder tbold2 = env.get(variable.getSimpleVariable().getName()); // pas sur ...a supprimer?
		TamagoBuilder tbold = env.get(variable.getQuantifier().getVariable().getName()); // sur
		String oldname = null;
		try {
			TamagoCCLogger.println(3,"New variable in the array with a exists constraint");
			oldname = tb.getName();
			tb.setName(variable.getQuantifier().getVariable().getName());
			env.put(variable.getQuantifier().getVariable().getName(), tb);
			TamagoCCLogger.println(3,"	Enrich env with variable: "+variable.getQuantifier().getVariable().getName());
			TamagoCCLogger.println(3,"	Expression : "+TamagoCCMakeReadableGExpression.toString(variable.getQuantifier().getBody()));
			GExpression expr = variable.getQuantifier().getBody();
			TamagoCSPFlatten flat = new TamagoCSPFlatten(expr);
			expr = flat.flatten();
			TamagoCSPDNF dnf = new TamagoCSPDNF(expr);
			ArrayList<GExpression> dnfcoll = new ArrayList<GExpression>();
			dnfcoll.addAll(dnf.getCollection());
			boolean cont = false;
			do {
				GExpression selectdnf = selector(dnfcoll);
				try {
					if((selectdnf.getCategory() == GExprType.OPERATOR)
							&& (((GOperator)selectdnf).getOperator().equals(TOpeName.opAnd))) {
						Iterator<GExpression> item = ((GOperator)selectdnf).getOperands();
						while(item.hasNext()) {
							GExpression tmpexpr = item.next();
							TamagoCCMakeReadableGExpression tccmrg = new TamagoCCMakeReadableGExpression(tmpexpr);
							try {
								TamagoCCLogger.print(3," -- EXISTS: Conversion sub expr : ");
								TamagoCCLogger.println(3,tccmrg.getStrExpression());
							} catch (TamagoCCException e) {		}

							if(!infer.generate(tmpexpr)) {
								//TamagoCCMakeReadableGExpression tccmrg = new TamagoCCMakeReadableGExpression(tmpexpr);
								TamagoCCLogger.print(3,"  -- EXISTS: Problem to convert the sub expr ");
								try {
									TamagoCCLogger.print(3,": "+tccmrg.getStrExpression());
								} catch (TamagoCCException e) {		}
								TamagoCCLogger.println(3,"");
							}
						}
					}
					else {
						if(!infer.generate(selectdnf)) {
							TamagoCCMakeReadableGExpression tccmrg = new TamagoCCMakeReadableGExpression(selectdnf);
							TamagoCCLogger.print(3," -- Problem to convert the expr ");
							try {
								TamagoCCLogger.print(3,": "+tccmrg.getStrExpression());
							} catch (TamagoCCException e) {		}
							TamagoCCLogger.println(3,"");
						}
					}
					cont = true;
				}
				catch(Exception e) {
					cont = false;
				}

			} while(!cont);
			TamagoCCLogger.println(3,"Generation of constraints succeed");
			
		}
		catch(Exception e) {
			TamagoCCLogger.println(2, "Exists Error constraint");
			TamagoCCLogger.infoln(3, e);
			throw new TamagoCSPException(e);
		}
		finally {
			if(tbold != null)
				env.put(variable.getQuantifier().getVariable().getName(), tbold);
			if(tbold2 != null)
				env.put(variable.getSimpleVariable().getName(), tbold2);
			if(oldname != null)
				tb.setName(oldname);
			done = true;
		}


	}

}

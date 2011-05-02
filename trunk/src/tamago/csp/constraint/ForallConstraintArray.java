/**
 * 
 */
package tamago.csp.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.csp.TamagoCSP;
import tamago.csp.convert.TamagoCSPDNF;
import tamago.csp.convert.TamagoCSPFlatten;
import tamago.csp.convert.TamagoCSPGenericRename;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.exception.TamagoCSPRuntime;
import tamago.csp.generic.CSPConstraint;
import tamago.csp.generic.CSPvar;
import tamago.csp.generic.CSPvariableForQuantifier;
import tamago.csp.generic.FDvar;
import tamagocc.api.TOpeName;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GOperator;
import tamagocc.generic.api.GExpression.GExprType;
import tamagocc.generic.impl.GIQuantifierVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.Pair;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class ForallConstraintArray implements CSPConstraint, QuantifierConstraint {

	private CSPvariableForQuantifier coll;
	private GIQuantifierVariable variable;
	private TamagoCSP csp;
	private TamagoEnvironment env;

	/**
	 * 
	 */
	public ForallConstraintArray(CSPvariableForQuantifier var, GIQuantifierVariable variable, TamagoCSP csp, TamagoEnvironment env) {
		this.coll = var;
		this.variable = variable;
		this.csp = csp;
		this.env = env;
		var.addQuantifierConstraint(this);
		// on ajoute les contraintes pour les infos deja presentes
		Collection<Pair<FDvar, TamagoBuilder>> buffer = new ArrayList<Pair<FDvar,TamagoBuilder>>();
		for(Pair<FDvar, TamagoBuilder> pos : var.getElements()) {
			buffer.add(pos);
		}
		for(Pair<FDvar, TamagoBuilder> pos : buffer) {
			try {
				addVariable(pos.l(), pos.r());
			} catch (TamagoCSPException e) {
				TamagoCCLogger.println(2,"FORALL CONSTRAINT: A variable failed");
				TamagoCCLogger.infoln(3, e);
			}
		}
	}

	/**
	 * @see tamago.csp.generic.CSPConstraint#filter()
	 */
	public void filter() throws TamagoCSPException {
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
		sb.append("forall");
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

	public void addVariable(FDvar idx, TamagoBuilder tb) throws TamagoCSPException {
		// en fait l'index on s'en fiche car on veut juste ajuster des contraintes
		// sur le contenu
		TamagoCSPInferConstraint infer = new TamagoCSPInferConstraint(csp,env);
		TamagoBuilder tbold = null;
		String oldname = null;
		try {
			TamagoCCLogger.println(3,"FORALL CONSTRAINT: Detection of new variable in collection for index : "+idx.getName());
			oldname = tb.getCSPvar().getName();
			//tb.setName(variable.getQuantifier().getVariable().getName());
			Hashtable<String, String> envar = new Hashtable<String, String>();
			envar.put(variable.getQuantifier().getVariable().getName(), oldname);
			TamagoCCLogger.println(3,"FORALL CONSTRAINT: VARIABLE QUANT: "+variable.getQuantifier().getVariable().getName());
			TamagoCCLogger.println(3,"FORALL CONSTRAINT: OLD VARIABLE QUANT: "+oldname);
			TamagoCSPGenericRename tgr = new TamagoCSPGenericRename(true,null, envar);
			tbold = env.put(oldname, tb);
			GExpression expr = variable.getQuantifier().getBody();
			expr = (GExpression) expr.visitExpression(tgr);
			TamagoCCLogger.println(3,"	In env Rename variable: "+variable.getQuantifier().getVariable().getName()+" to "+oldname);
			TamagoCCLogger.println(3,"	Expression : "+TamagoCCMakeReadableGExpression.toString(variable.getQuantifier().getBody()));
			TamagoCSPFlatten flat = new TamagoCSPFlatten(expr);
			expr = flat.flatten();
			TamagoCSPDNF dnf = new TamagoCSPDNF(expr);
			ArrayList<GExpression> dnfcoll = new ArrayList<GExpression>();
			dnfcoll.addAll(dnf.getCollection());
			boolean cont = false;
			do {
				GExpression selectdnf = selector(dnfcoll);
				TamagoCCLogger.println(3,"	    Convertion of  "+TamagoCCMakeReadableGExpression.toString(selectdnf));
				try {
					if((selectdnf.getCategory() == GExprType.OPERATOR)
							&& (((GOperator)selectdnf).getOperator().equals(TOpeName.opAnd))) {
						Iterator<GExpression> item = ((GOperator)selectdnf).getOperands();
						while(item.hasNext()) {
							GExpression tmpexpr = item.next();
							TamagoCCLogger.print(3," -- FORALL: Conversion sub expr : ");
							TamagoCCLogger.println(3,TamagoCCMakeReadableGExpression.toString(tmpexpr));

							if(!infer.generate(tmpexpr)) {
								TamagoCCLogger.print(3,"  -- FORALL: Problem to convert the sub expr ");
								TamagoCCLogger.println(3,": "+TamagoCCMakeReadableGExpression.toString(tmpexpr));
							}
						}
					}
					else {
						if(!infer.generate(selectdnf)) {
							TamagoCCLogger.print(3," -- Problem to convert the expr ");
							TamagoCCLogger.println(3,": "+TamagoCCMakeReadableGExpression.toString(selectdnf));
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
			TamagoCCLogger.println(2, "Forall Error constraint");
			TamagoCCLogger.infoln(3, e);
			throw new TamagoCSPException(e);
		}
		finally {
			if(tbold != null)
				env.put(variable.getQuantifier().getVariable().getName(), tbold);
			//if(tbold2 != null)
			//	env.put(variable.getSimpleVariable().getName(), tbold2);
			//if(oldname != null)
			//	tb.setName(oldname);
		}
	}
}

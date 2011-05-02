/**
 * 
 */
package tamago.csp.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import tamago.csp.exception.TamagoCSPRuntime;
import tamagocc.api.TOpeName;
import tamagocc.exception.TamagoCCException;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GNot;
import tamagocc.generic.api.GOperator;
import tamagocc.generic.impl.GIInteger;
import tamagocc.generic.impl.GINot;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari and Matthieu Carlier
 *
 */
public class TamagoCSPDNF implements Iterable<GExpression> {

	private GExpression original;
	
	private GExpression simplexpr;
	private Collection<GExpression> nf;

	public TamagoCSPDNF(GExpression ori) {
		this.original = ori;
		nf = null;
		simplexpr = null;
	}

	public Iterator<GExpression> iterator() {
		if(simplexpr == null)
			simplexpr = simplify(original, false);
		if(nf == null)
			nf = decoupe(simplexpr);
		return nf.iterator();
	}

	public ArrayList<GExpression> getCollection() {
		iterator();
		return new ArrayList<GExpression>(nf);
	}

	public GExpression getOriginal() {
		return original;
	}
				
	public GExpression getSimplifyExpression() {
		if(simplexpr == null) {
			simplexpr = simplify(original, false);
		}
		return simplexpr;
	}


	private GExpression simplify(GExpression e,boolean mustrev) throws TamagoCSPRuntime {
		switch (e.getCategory()) {
		case ATPRE: throw new TamagoCSPRuntime("Error : atpre in a precondition");
		case INLABEL:
		case INT:
		case NIL:
		case NOCONTRACT:
		case REAL:
		case STRING:
			return e;
		case BOOL:
		case CALL:
		case RETURN:
		case READ:
		case VARIABLE:
			if(mustrev)
				return new GINot(e);
			else
				return e;
		case NOT:
		{
			if(mustrev)
				return simplify(((GNot)e).getTerm(),false);
			else
				return simplify(((GNot)e).getTerm(),true);
		}
		case OPERATOR:
		{
			GOperator myop = ((GOperator)e);
			switch(((GOperator)e).getOperator().getID()) {
			case TOpeName.OR:
				if(mustrev) {
					return simplOpe(((GOperator)e).getOperands(), mustrev, TOpeName.opAnd);
				}
				else {
					return simplOpe(((GOperator)e).getOperands(), mustrev, TOpeName.opOr);
				}
			case TOpeName.XOR: // a^b = !a&b | a&!b
				if(((GOperator)e).getArity() != 2) throw new TamagoCSPRuntime("XOR arity-n : not yet implemented");

				Iterator<GExpression> xore = ((GOperator)e).getOperands();
				GExpression a = xore.next();
				GExpression b = xore.next();

				ArrayList<GExpression> gauche = new ArrayList<GExpression>();
				gauche.add(new GINot(a));
				gauche.add(b);
				GIOperator etgauche = new GIOperator(TOpeName.opAnd,gauche.iterator());

				ArrayList<GExpression> droite = new ArrayList<GExpression>();
				droite.add(a);
				droite.add(new GINot(b));
				GIOperator etdroite = new GIOperator(TOpeName.opAnd,droite.iterator());

				ArrayList<GExpression> centre = new ArrayList<GExpression>();
				centre.add(etgauche);
				centre.add(etdroite);

				return simplify(new GIOperator(TOpeName.opOr,centre.iterator()), mustrev);
			case TOpeName.AND:
				if(mustrev) {
					return simplOpe(((GOperator)e).getOperands(), mustrev, TOpeName.opOr);
				}
				else {
					return simplOpe(((GOperator)e).getOperands(), mustrev, TOpeName.opAnd);
				}			

			case TOpeName.EG:
				if(mustrev)
					return new GIOperator(TOpeName.opNe,myop.getOperands());
				else
					return e;
			case TOpeName.INF:
				if(mustrev)
					return new GIOperator(TOpeName.opSupEg,myop.getOperands());
				else
					return e;
			case TOpeName.INFEG:
				if(mustrev)
					return new GIOperator(TOpeName.opSup,myop.getOperands());
				else
					return e;
			case TOpeName.SUP:
				if(mustrev)
					return new GIOperator(TOpeName.opInfEg,myop.getOperands());
				else
					return e;
			case TOpeName.SUPEG:
				if(mustrev)
					return new GIOperator(TOpeName.opInf,myop.getOperands());
				else
					return e;
			case TOpeName.NE:
				if(mustrev)
					return new GIOperator(TOpeName.opEg,myop.getOperands());
				else
					return e;
			case TOpeName.IMPLY:
				GINot not = new GINot(myop.getOperand(0));
				GIOperator ope = new GIOperator(TOpeName.opAnd);
				ope.addOperand(myop.getOperand(0));
				ope.addOperand(myop.getOperand(1));
				
				GIOperator fin = new GIOperator(TOpeName.opOr);
				fin.addOperand(not);
				fin.addOperand(ope);
				return simplify(fin, mustrev);
			case TOpeName.EQUIV:
				GIOperator opeequiv = new GIOperator(TOpeName.opAnd);
				
				GIOperator ltor= new GIOperator(TOpeName.opImply);
				ltor.addOperand(myop.getOperand(0));
				ltor.addOperand(myop.getOperand(1));
				
				GIOperator rtol= new GIOperator(TOpeName.opImply);
				rtol.addOperand(myop.getOperand(0));
				rtol.addOperand(myop.getOperand(1));
				
				opeequiv.addOperand(ltor);
				opeequiv.addOperand(rtol);
				return  simplify(opeequiv, mustrev);
			default: // on a que les operateurs d'arithmetique
				return e;
			}
		}
		default:
			throw new TamagoCSPRuntime("Unknown error");
		}
	}

	private GExpression simplOpe(Iterator<GExpression> exprs,boolean mustrev,TOpeName op) {
		ArrayList<GExpression> operands = new ArrayList<GExpression>();
		while(exprs.hasNext()) {
			operands.add(simplify(exprs.next(), mustrev));
		}
		return new GIOperator(op,operands.iterator());
	}

	public boolean isNormalForm(GExpression expr) {
		switch(expr.getCategory()) {
		case OPERATOR:
		{
			switch(((GOperator)expr).getOperator().getID()) {
			case TOpeName.OR:
				return false;
			case TOpeName.XOR:
				return false;//ne pas l'oublier il inclut un OR
			case TOpeName.AND:

				Iterator<GExpression> exprs = ((GOperator)expr).getOperands();
				while(exprs.hasNext()) {
					if(!isNormalForm(exprs.next()))
						return false;
				}
				return true;
			default:
				return true;
			}
		}
		case NOT:
			return true; // on ne peut mettre un not que devant un atome (pas devant un AND,OR,XOR). 
		default:
			return true;
		}
	}

	private Collection<GExpression> decoupe(GExpression expr) throws TamagoCSPRuntime {
		switch(expr.getCategory()) {
		case ATPRE: throw new TamagoCSPRuntime("AtPre in a precondition");
		case OPERATOR:
			return decoupeOperateur((GOperator)expr);
		default:
			ArrayList<GExpression> result = new ArrayList<GExpression>(1);
			result.add(expr);
			return result;
		}
	}



	private Collection<GExpression> decoupeOperateur(GOperator operator) {
		switch(operator.getOperator().getID()) {
		case TOpeName.AND: {
			Iterator<GExpression> operands = operator.getOperands();
			ArrayList<GExpression> result = new ArrayList<GExpression>();
			while(operands.hasNext()) {
				GExpression ope = operands.next();
				Collection<GExpression> decoope = decoupe(ope);
				result = flattern(result,decoope);
			}
			return result;
		}
		case TOpeName.OR: {
			ArrayList<GExpression> result = new ArrayList<GExpression>();
			Iterator<GExpression> operands = operator.getOperands();
			while(operands.hasNext()) {
				Collection<GExpression> decoope = decoupe(operands.next());
				result.addAll(decoope);
			}
			return result;
		}
		default:
			ArrayList<GExpression> result = new ArrayList<GExpression>(1);
			result.add(operator);
			return result;
		}
	}

	private ArrayList<GExpression> flattern(ArrayList<GExpression> result,Collection<GExpression> ope)
	throws TamagoCSPRuntime
	{
		ArrayList<GExpression> res = new ArrayList<GExpression>();
		for (GExpression expression : result) {
			Iterator<GExpression> opes = ope.iterator();
			while(opes.hasNext()) {
				ArrayList<GExpression> centre = new ArrayList<GExpression>();
				centre.add(expression);
				centre.add(opes.next());
				GIOperator giope = new GIOperator(TOpeName.opAnd,centre.iterator());
				TamagoCSPFlatten flat = new TamagoCSPFlatten(giope);
				try {
					res.add(flat.flatten());
				}
				catch(TamagoCCException ex) {
					throw new TamagoCSPRuntime(ex);
				}
				//TamagoCSPDNF dnf = new TamagoCSPDNF(giope);
				//res.add(dnf.getSimplifyExpression());
			}
		}
		if(result.size() == 0) {
			res.addAll(ope);
		}
		
		return res;
	}


	
	public static void main(String args[]) {
		main1();
		System.out.println("----------------------------------------------");
		main2();
	}
	
	public static void main2() {
		try {
			GIVariable a = new GIVariable("A", GIType.TYPE_BOOL);
			GIVariable b = new GIVariable("B", GIType.TYPE_BOOL);
			GIVariable c = new GIVariable("C", GIType.TYPE_BOOL);
			
			ArrayList<GExpression> operands = new ArrayList<GExpression>();
			operands.add(b);
			operands.add(c);
			GIOperator bimplc = new GIOperator(TOpeName.opImply,operands.iterator());
			
			operands = new ArrayList<GExpression>();
			operands.add(c);
			operands.add(b);
			GIOperator cimplb = new GIOperator(TOpeName.opImply,operands.iterator());
			
			operands = new ArrayList<GExpression>();
			operands.add(a);
			operands.add(bimplc);
			operands.add(cimplb);
			GIOperator ands = new GIOperator(TOpeName.opAnd,operands.iterator());
			
			System.out.println(TamagoCCMakeReadableGExpression.toString(ands));
			
			TamagoCSPDNF dnf = new TamagoCSPDNF(ands);
			System.out.println(TamagoCCMakeReadableGExpression.toString(dnf.getSimplifyExpression()));
		
			int i = 1;
			for (GExpression expression : dnf) {
				System.out.println(""+i+" : "+TamagoCCMakeReadableGExpression.toString(expression));
				i++;
			}
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main1() {
		try {
			GIVariable a = new GIVariable("a",GIType.TYPE_INT);
			GIVariable b = new GIVariable("b",GIType.TYPE_BOOL);
			GIInteger cinq = new GIInteger(5);
			GIInteger neuf = new GIInteger(9);
			
			
			ArrayList<GExpression> am5 = new ArrayList<GExpression>();
			am5.add(a);
			am5.add(cinq);
			GIOperator infam5 = new GIOperator(TOpeName.opInf,am5.iterator());
			
			ArrayList<GExpression> am9 = new ArrayList<GExpression>();
			am9.add(a);
			am9.add(neuf);
			GIOperator infam9 = new GIOperator(TOpeName.opSup,am9.iterator());
			
			ArrayList<GExpression> gauche = new ArrayList<GExpression>();
			gauche.add(infam5);
			gauche.add(b);
			GIOperator et1 = new GIOperator(TOpeName.opAnd,gauche.iterator());
			
			ArrayList<GExpression> droite = new ArrayList<GExpression>();
			droite.add(new GINot(et1));
			droite.add(infam9);
			GIOperator et2 = new GIOperator(TOpeName.opAnd,droite.iterator());
			
			TamagoCCMakeReadableGExpression read = new TamagoCCMakeReadableGExpression(et2);
			
			System.out.println(read.getStrExpression());
			
			TamagoCSPDNF dnf = new TamagoCSPDNF(et2);
			
			read = new TamagoCCMakeReadableGExpression(dnf.getSimplifyExpression());			
			System.out.println(read.getStrExpression());
			
			int i = 1;
			for (GExpression expression : dnf) {
				read = new TamagoCCMakeReadableGExpression(expression);			
				System.out.println(""+i+" : "+read.getStrExpression());
				i++;
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int size() {
		if(nf == null)
			this.iterator();
		return nf.size();
	}
	
	
}


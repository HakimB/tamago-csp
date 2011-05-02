package tamago;

import java.util.ArrayList;

import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoBuilderFactory;
import tamago.builder.TamagoEnvironment;
import tamago.builder.impl.Builderarray;
import tamago.builder.impl.Builderint;
import tamago.csp.Backtracking;
import tamago.csp.TamagoCSP;
import tamago.csp.constant.CSPbool;
import tamago.csp.constant.CSPinteger;
import tamago.csp.constant.CSPreal;
import tamago.csp.constraint.NotXBool;
import tamago.csp.constraint.XBool;
import tamago.csp.constraint.XEqC;
import tamago.csp.constraint.XEqY;
import tamago.csp.constraint.XGeqC;
import tamago.csp.constraint.XGtC;
import tamago.csp.constraint.XLeqC;
import tamago.csp.constraint.XNeqC;
import tamago.csp.constraint.XltC;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.var.Boolvar;
import tamago.csp.var.Intvar;
import tamago.csp.var.Realvar;
import tamagocc.api.TOpeName;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GType;
import tamagocc.generic.impl.GIExistRange;
import tamagocc.generic.impl.GIExistSet;
import tamagocc.generic.impl.GIForallColl;
import tamagocc.generic.impl.GIForallRange;
import tamagocc.generic.impl.GIForallSet;
import tamagocc.generic.impl.GIInLabel;
import tamagocc.generic.impl.GIInteger;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GISet;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.logger.TamagoCCLogger;

/**
 * @author Hakim Belhaouari
 *
 */
public class TamagoCSPMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		quantrangeexist();
	}
	
	public static void quantrangeforall() {
		try {
			TamagoCCLogger.setLevel(4);
			TamagoCSP csp = new TamagoCSP();
			
			
			TamagoEnvironment env = new TamagoEnvironment();

			TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

			GType tint = GIType.TYPE_INT;
			Builderint bvar = new Builderint(env,"var",tint,csp.getBacktrack());
			csp.addVariable(bvar.getCSPvar());
			env.put("var", bvar);
			
			/*Builderint bv1 = new Builderint(env,"v1",tint,csp.getBacktrack());
			csp.addVariable(bv1.getCSPvar());
			env.put("v1", bv1);
			
			Builderint bv2 = new Builderint(env,"v2",tint,csp.getBacktrack());
			csp.addVariable(bv2.getCSPvar());
			env.put("v2", bv2);*/
			
			GIVariable i = new GIVariable("i",tint);
			GIVariable var = new GIVariable("var",tint);
			
			GExpression min = new GIInteger(1);
			GExpression max = new GIInteger(5);
			
			GIOperator body = new GIOperator(TOpeName.opInf);
			body.addOperand(i);
			body.addOperand(var);
			
			
			GIForallRange quant = new GIForallRange(tint, i, min, max, body);
			
			System.err.println("CONVERTION:");
			convert.generate(quant.getResultExpression());
			System.err.println("Presentation:");
			System.err.println(csp.toString());
			csp.solve();
			System.err.println(csp.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void quantrangeexist() {
		try {
			TamagoCCLogger.setLevel(4);
			TamagoCSP csp = new TamagoCSP();
			
			
			TamagoEnvironment env = new TamagoEnvironment();

			TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

			GType tint = GIType.TYPE_INT;
			Builderint bvar = new Builderint(env,"var",tint,csp.getBacktrack());
			csp.addVariable(bvar.getCSPvar());
			env.put("var", bvar);
			
			/*Builderint bv1 = new Builderint(env,"v1",tint,csp.getBacktrack());
			csp.addVariable(bv1.getCSPvar());
			env.put("v1", bv1);
			
			Builderint bv2 = new Builderint(env,"v2",tint,csp.getBacktrack());
			csp.addVariable(bv2.getCSPvar());
			env.put("v2", bv2);*/
			
			GIVariable i = new GIVariable("i",tint);
			GIVariable var = new GIVariable("var",tint);
			
			GExpression min = new GIInteger(1);
			GExpression max = new GIInteger(5);
			
			GIOperator body = new GIOperator(TOpeName.opEg);
			body.addOperand(i);
			body.addOperand(var);
			
			
			GIExistRange quant = new GIExistRange(tint, i, min, max, body);
			
			System.err.println("CONVERTION:");
			convert.generate(quant.getResultExpression());
			System.err.println("Presentation:");
			System.err.println(csp.toString());
			csp.solve();
			System.err.println(csp.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void quantsetforall() {
		try {
			TamagoCCLogger.setLevel(4);
			TamagoCSP csp = new TamagoCSP();

			TamagoEnvironment env = new TamagoEnvironment();

			TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

			GType tint = GIType.TYPE_INT;
			Builderint bvar = new Builderint(env,"var",tint,csp.getBacktrack());
			csp.addVariable(bvar.getCSPvar());
			env.put("var", bvar);
			
			Builderint bv1 = new Builderint(env,"v1",tint,csp.getBacktrack());
			csp.addVariable(bv1.getCSPvar());
			env.put("v1", bv1);
			
			Builderint bv2 = new Builderint(env,"v2",tint,csp.getBacktrack());
			csp.addVariable(bv2.getCSPvar());
			env.put("v2", bv2);
			
			GIVariable i = new GIVariable("i",tint);
			ArrayList<GExpression> set = new ArrayList<GExpression>();
			set.add(new GIInteger(3));
			set.add(new GIVariable("v1", tint));
			set.add(new GIVariable("v2", tint));
			GISet s = new GISet(tint,set);
			GIVariable var = new GIVariable("var",tint);
			GIOperator body = new GIOperator(TOpeName.opEg);
			body.addOperand(var);
			body.addOperand(i);
			
			
			GIForallSet existset = new GIForallSet(tint,i,s,body);
			
			System.err.println("CONVERTION:");
			convert.generate(existset.getResultExpression());
			System.err.println("Presentation:");
			System.err.println(csp.toString());
			csp.solve();
			System.err.println(csp.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void quantset() {
		try {
			TamagoCCLogger.setLevel(4);
			TamagoCSP csp = new TamagoCSP();

			TamagoEnvironment env = new TamagoEnvironment();

			TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

			GType tint = GIType.TYPE_INT;
			Builderint bvar = new Builderint(env,"var",tint,csp.getBacktrack());
			csp.addVariable(bvar.getCSPvar());
			env.put("var", bvar);
			
			
			GIVariable i = new GIVariable("i",tint);
			ArrayList<GExpression> set = new ArrayList<GExpression>();
			set.add(new GIInteger(3));
			set.add(new GIInteger(4));
			set.add(new GIInteger(1));
			GISet s = new GISet(tint,set);
			GIVariable var = new GIVariable("var",tint);
			GIOperator body = new GIOperator(TOpeName.opEg);
			body.addOperand(var);
			body.addOperand(i);
			
			
			GIExistSet existset = new GIExistSet(tint,i,s,body);
			
			System.err.println("CONVERTION:");
			convert.generate(existset.getResultExpression());
			System.err.println("Presentation:");
			System.err.println(csp.toString());
			csp.solve();
			System.err.println(csp.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void foralliteration() {
		try {
			TamagoCCLogger.setLevel(3);
			TamagoCSP csp = new TamagoCSP();

			TamagoEnvironment env = new TamagoEnvironment();

			TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

			Builderarray arr = new Builderarray(new TamagoBuilderFactory(){
				public TamagoBuilder searchBuilder(TamagoEnvironment env, String name, GType type, Backtracking b) {
					return new Builderint(env,"int",type,b); 
				}
			},env,"tab",GIType.generateType("int[]"),csp.getBacktrack());
			csp.addVariable(arr.getCSPvar());
			env.put("tab", arr);

			Builderint bp = new Builderint(env,"p",GIType.TYPE_INT,csp.getBacktrack());
			env.put("p", bp);
			csp.addVariable(bp.getCSPvar());

			// tab[0] = 3
			GIVariable tab0 = new GIVariable("tab",GIType.TYPE_INT,new GIInteger(0));
			GIOperator ope1 = new GIOperator(TOpeName.opEg);
			ope1.addOperand(tab0);
			ope1.addOperand(new GIInteger(3));

			// tab[p] = 10;
			GIVariable tabp = new GIVariable("tab",GIType.TYPE_INT,new GIVariable("p",GIType.TYPE_INT));
			GIOperator ope3 = new GIOperator(TOpeName.opEg);
			ope3.addOperand(tabp);
			ope3.addOperand(new GIInteger(10));

			// tab[p] = tab.length - 1;
			GIInLabel intab = new GIInLabel(new GIVariable("tab",GIType.generateType("int[]")),new GIVariable("length",GIType.TYPE_INT));
			GIOperator sub = new GIOperator(TOpeName.opMinus);
			sub.addOperand(intab);
			sub.addOperand(new GIInteger(1));

			GIOperator ope2 = new GIOperator(TOpeName.opEg);
			ope2.addOperand(tabp);
			ope2.addOperand(sub);


			// forall i:int in tab  (i == tab[i-3])
			GIVariable tab = new GIVariable("tab",GIType.generateType("int[]"));
			GIOperator sub2 = new GIOperator(TOpeName.opMinus);
			sub2.addOperand(new GIVariable("i",GIType.TYPE_INT));
			sub2.addOperand(new GIInteger(3));
			GIVariable tabsub = new GIVariable("tab",GIType.TYPE_INT,sub2);

			GIOperator eq = new GIOperator(TOpeName.opEg);
			GIVariable i = new GIVariable("i",GIType.TYPE_INT); 
			eq.addOperand(i);
			eq.addOperand(tabsub);

			GIForallColl coll = new GIForallColl(GIType.TYPE_INT,i,tab,eq);

			System.err.println("CONVERTION PREMIER TERME");
			convert.generate(ope1);

			System.err.println("CONVERTION SECOND TERME");
			convert.generate(ope2);

			System.err.println("CONVERTION TROISIEME TERME");
			convert.generate(ope3);

			System.err.println("CONVERTION QUATRIEME TERME");
			convert.generate(coll.getResultExpression());

			System.err.println("Presentation:");
			System.err.println(csp.toString());
			csp.solve();
			System.err.println(csp.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void reel3() {
		try {
			TamagoCCLogger.setLevel(3);
			TamagoCSP csp = new TamagoCSP();
			Boolvar isempty = new Boolvar("isEmpty",csp.getBacktrack());
			Boolvar isemptypost = new Boolvar("isEmpty@post",csp.getBacktrack());

			Realvar quantity = new Realvar("quantity",csp.getBacktrack());
			Realvar quantitypost = new Realvar("quantity@post",csp.getBacktrack());

			/*
			 * quantity := 374.0786053975563
	isEmpty := false
			 * 
			 * -- constraints : ((#quantity >= 0) and not(#isEmpty) and (#quantity != 0) and  [nocontract]  and #isEmpty@post and  [nocontract]  and (#quantity@post >= 0) and #isEmpty@post and (#quantity@post == 0) and (#quantity@post == 0) and #isEmpty@post)
 -- Conversion sub expr : (#quantity >= 0)
 -- Conversion sub expr : not(#isEmpty)
 -- Conversion sub expr : (#quantity != 0)
 -- Conversion sub expr :  [nocontract] 
  -- Problem to convert the sub expr :  [nocontract] 
 -- Conversion sub expr : #isEmpty@post
 -- Conversion sub expr :  [nocontract] 
  -- Problem to convert the sub expr :  [nocontract] 
 -- Conversion sub expr : (#quantity@post >= 0)
 -- Conversion sub expr : #isEmpty@post
 -- Conversion sub expr : (#quantity@post == 0)
 -- Conversion sub expr : (#quantity@post == 0)
 -- Conversion sub expr : #isEmpty@post
			 */


			System.out.println(csp.toString());

			csp.solve();

			System.out.println(csp.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void reel2() {
		try {
			TamagoCCLogger.setLevel(3);
			TamagoCSP csp = new TamagoCSP();
			Boolvar isempty = new Boolvar("isEmpty",csp.getBacktrack());
			Boolvar isemptypost = new Boolvar("isEmpty@post",csp.getBacktrack());

			Realvar quantity = new Realvar("quantity",csp.getBacktrack());
			Realvar quantitypost = new Realvar("quantity@post",csp.getBacktrack());

			csp.addVariable(isempty);
			isempty.remove(new CSPbool(true));

			csp.addVariable(isemptypost);

			csp.addVariable(quantity);
			quantity.fix(new CSPreal(776.860558705876));

			csp.addVariable(quantitypost);
			csp.getBacktrack().clear();
			// ----------------------------
			XGeqC c1 = new XGeqC(quantity,new CSPinteger(0));
			NotXBool c2 = new NotXBool(isempty);
			XNeqC c3 = new XNeqC(quantity,new CSPinteger(0));
			XBool c4 = new XBool(isemptypost);
			XGeqC c5 = new XGeqC(quantitypost,new CSPinteger(0));
			XBool c6 = new XBool(isemptypost);
			XEqC c7 = new XEqC(quantitypost,new CSPinteger(0));
			XEqC c8 = new XEqC(quantitypost,new CSPinteger(0));
			XBool c9 = new XBool(isemptypost);
			csp.addConstraint(c1);
			csp.addConstraint(c2);
			csp.addConstraint(c3);
			csp.addConstraint(c4);
			csp.addConstraint(c5);
			csp.addConstraint(c6);
			csp.addConstraint(c7);
			csp.addConstraint(c8);
			csp.addConstraint(c9);
			System.out.println(csp.toString());

			csp.solve();

			System.out.println(csp.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void reel() {
		try {
			TamagoCCLogger.setLevel(3);
			TamagoCSP csp = new TamagoCSP();
			Boolvar isempty = new Boolvar("isEmpty",csp.getBacktrack());
			Boolvar isemptypost = new Boolvar("isEmpty@post",csp.getBacktrack());

			Realvar q = new Realvar("q",csp.getBacktrack());
			Realvar quantity = new Realvar("quantity",csp.getBacktrack());
			Realvar quantitypost = new Realvar("quantity@post",csp.getBacktrack());

			csp.addVariable(isempty);
			isempty.remove(new CSPbool(true));

			csp.addVariable(isemptypost);

			csp.addVariable(q);
			csp.addVariable(quantity);
			quantity.fix(new CSPreal(606.4925048686936));

			csp.addVariable(quantitypost);
			csp.getBacktrack().clear();
			// ----------------------------
			XGeqC c1 = new XGeqC(quantity,new CSPinteger(0));
			NotXBool notis = new NotXBool(isempty);
			XNeqC c2 = new XNeqC(quantity,new CSPinteger(0));
			XGeqC c3 = new XGeqC(q,new CSPinteger(0));
			XBool ispost = new XBool(isemptypost);
			XBool is = new XBool(isempty);
			XEqC c4 = new XEqC(q,new CSPinteger(0));
			XGeqC c5 = new XGeqC(quantitypost,new CSPinteger(0));
			XGtC c6 = new XGtC(quantitypost,new CSPinteger(0));
			NotXBool c7 = new NotXBool(isemptypost);
			XNeqC c8 = new XNeqC(quantitypost,new CSPinteger(0));
			csp.addConstraint(c1);
			csp.addConstraint(notis);
			csp.addConstraint(c2);
			csp.addConstraint(c3);
			csp.addConstraint(ispost);
			csp.addConstraint(is);
			csp.addConstraint(c4);
			csp.addConstraint(c5);
			csp.addConstraint(c6);
			csp.addConstraint(c7);
			csp.addConstraint(c8);
			System.out.println(csp.toString());

			csp.solve();

			System.out.println(csp.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void debutant() {
		try {
			TamagoCSP csp = new TamagoCSP(); 
			Intvar x = new Intvar("x",csp.getBacktrack());
			Intvar y = new Intvar("y",csp.getBacktrack());
			Intvar z = new Intvar("z",csp.getBacktrack());
			Boolvar b = new Boolvar("b",csp.getBacktrack());
			Boolvar a = new Boolvar("a",csp.getBacktrack());
			Realvar p = new Realvar("p",csp.getBacktrack());
			Realvar q = new Realvar("q",csp.getBacktrack());

			XltC xinf10 = new XltC(x,new CSPreal(10.0));
			XNeqC yneq2 = new XNeqC(y,new CSPinteger(2));
			XEqC xeg2 = new XEqC(x,new CSPreal(2));
			XEqY xeqz = new XEqY(x,z);
			XGeqC ygeq10 = new XGeqC(y,new CSPinteger(10));
			XLeqC yleq20 = new XLeqC(y,new CSPinteger(20));

			XBool btrue = new XBool(b);
			NotXBool nota = new NotXBool(a);

			XltC pinf10 = new XltC(p,new CSPreal(10.0));
			XGtC psup9_8 = new XGtC(p,new CSPreal(9.8));

			XGtC qsup10000 = new XGtC(q,new CSPreal(999.99999999));

			csp.addConstraint(xinf10);
			csp.addConstraint(yneq2);
			csp.addConstraint(xeg2);
			csp.addConstraint(xeqz);
			csp.addConstraint(ygeq10);
			csp.addConstraint(yleq20);
			csp.addConstraint(btrue);
			csp.addConstraint(nota);
			csp.addConstraint(pinf10);
			csp.addConstraint(psup9_8);
			csp.addConstraint(qsup10000);


			csp.addVariable(x);
			csp.addVariable(y);
			csp.addVariable(z);
			csp.addVariable(a);
			csp.addVariable(b);
			csp.addVariable(p);
			csp.addVariable(q);

			csp.solve();

			System.out.println(csp.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}

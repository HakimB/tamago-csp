/**
 * 
 */
package tamago.csp.test;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import tamago.builder.IntegerBuilderFactory;
import tamago.builder.TamagoBuilder;
import tamago.builder.TamagoEnvironment;
import tamago.builder.impl.Builderarray;
import tamago.csp.TamagoCSP;
import tamago.csp.constant.CSParray;
import tamago.csp.constant.CSPinteger;
import tamago.csp.convert.TamagoCSPInferConstraint;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.generic.FDvar;
import tamagocc.api.TOpeName;
import tamagocc.generic.api.GExpression;
import tamagocc.generic.api.GInteger;
import tamagocc.generic.api.GType;
import tamagocc.generic.impl.GIForallRange;
import tamagocc.generic.impl.GIInLabel;
import tamagocc.generic.impl.GIInteger;
import tamagocc.generic.impl.GIOperator;
import tamagocc.generic.impl.GIType;
import tamagocc.generic.impl.GIVariable;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.Pair;
import tamagocc.util.TamagoCCMakeReadableGExpression;

/**
 * @author Hakim Belhaouari
 *
 */
public class TestCSPConcept {

	private static final boolean SEECONSTRAINTS = true;

	private static GIVariable var(String n, GType t) {
		return new GIVariable(n, t);
	}
	private static GIVariable var(String n,GType t, GExpression idx) {
		return new GIVariable(n, t, idx);
	}
	private static GInteger ent(int v) {
		return new GIInteger(v);
	}
	static GType tint = GIType.TYPE_INT;
	static GType tab = GIType.generateType("int[]");
	
	static StringBuilder constraints = new StringBuilder();
	
	public static void main(String args[]) throws TamagoCSPException {
		TamagoCCLogger.setLevel(4);
		TamagoCSP csp = new TamagoCSP();
		
		
		TamagoEnvironment env = new TamagoEnvironment();

		TamagoCSPInferConstraint convert = new TamagoCSPInferConstraint(csp,env);

		
		
		IntegerBuilderFactory ibf = new IntegerBuilderFactory();
		Builderarray barray = new Builderarray(ibf, env, "grille", GIType.generateType("int[]"), csp.getBacktrack());
		csp.addVariable(barray.getCSPvar());
		env.put("grille",barray);
		
		
		
		{ // longueur egale a 81
			GIInLabel inlabel = new GIInLabel(var("grille",tab), var("length",tint));
			GIOperator eg = new GIOperator(TOpeName.opEg);
			eg.addOperand(inlabel);
			eg.addOperand(ent(81));
			convert.generate(eg);
			constraints.append(TamagoCCMakeReadableGExpression.toString(eg)+" && \n");
		}
		
		
		
		
		{ // all differents en binaire
			for (int k = 0; k < 9; k++) {
				for (int i = (9 * k); i < (9 * (k + 1)); i++) {
					for (int j = (i + 1); j < (9 * (k + 1)); j++) {
						convert.generate(different(i, j));
						
					}
				}
			}
			
			for (int k = 0; k < 9; k++) {
				for (int i = k; i < 81; i+=9) {
					for (int j = (i + 9); j < 81; j+=9) {
						convert.generate(different(i, j));
					}
				}
			}
			
			for(int k=0; k < 81;k+= 3) {
				switch(k % 27) {
				case 0:
				case 3:
				case 6:
					int[] block = new int[] { 0,1,2,9,10,11,18,19,20 };
					for(int i : block) {
						for(int j : block) {
							if(i != j) {
								convert.generate(different(i+k,j+k));
							}
						}
					}
				}
			}
			
			/*int[] regions = new int[] { 10 , 13 ,16,37,40,43,64,67,70 };
			for(int k : regions) {
				convert.generate(different(k, k-10));
				convert.generate(different(k, k-9));
				convert.generate(different(k, k-8));
				convert.generate(different(k, k-1));
				convert.generate(different(k, k+1));
				convert.generate(different(k, k+8));
				convert.generate(different(k, k+9));
				convert.generate(different(k, k+10));
				
			}*/
		}
		
		{ // verif nombre compris entre 1 et 9
			GIVariable v = new GIVariable("v", tint);
			GIOperator aumoins1 = new GIOperator(TOpeName.opSupEg);
			aumoins1.addOperand(var("grille", tint, v));
			aumoins1.addOperand(ent(1));

			GIOperator auplus9 = new GIOperator(TOpeName.opInfEg);
			auplus9.addOperand(var("grille", tint, v));
			auplus9.addOperand(ent(9));

			GIOperator and = new GIOperator(TOpeName.opAnd);
			and.addOperand(aumoins1);
			and.addOperand(auplus9);

			GIForallRange quant;
			 /*quant = new GIForallRange(tint, v, ent(0), ent(80),
					aumoins1);
			convert.generate(quant.getResultExpression());
			quant = new GIForallRange(tint, v, ent(0), ent(80),
					auplus9);
			convert.generate(quant.getResultExpression());*/
			quant = new GIForallRange(tint, v, ent(0), ent(80),
					and);
			convert.generate(quant.getResultExpression());
			//constraints.append(TamagoCCMakeReadableGExpression.toString(quant.getResultExpression()));
			
			/*for(int i=0; i < 81;i++) {
				GIOperator aumoins1 = new GIOperator(TOpeName.opSupEg);
				aumoins1.addOperand(var("grille", tint, ent(i)));
				aumoins1.addOperand(ent(1));

				GIOperator auplus9 = new GIOperator(TOpeName.opInfEg);
				auplus9.addOperand(var("grille", tint, ent(i)));
				auplus9.addOperand(ent(9));

				convert.generate(aumoins1);
				convert.generate(auplus9);
			}*/
		}
		
		System.err.println(csp.toString());
		csp.solve();
		System.err.println(csp.toString());
		
		if(barray.getCSPvar().isFixed()) {
			System.err.println("grille est fixée:");
			CSParray carray = (CSParray) barray.getCSPvar().getValue();
			int[] sudoku = new int[81];
			for (Pair<FDvar,TamagoBuilder> p : carray.getElements()) {
				int i = ((CSPinteger)p.getL().getValue()).intValue();
				int v = p.getR().getCSPvar().getValue().intValue();
				if(sudoku[i] != 0 && sudoku[i] != v) {
					System.err.println("PROBLEME INDEX: "+i+" -> "+sudoku[i]+" NEW VALUE:"+v);
				}
				else
					sudoku[i] = v;
			}
			for(int i= 0; i < 81; i++) {
				if(i % 9 == 0)
					System.err.println("|");
				if(i%27 == 0)
					System.err.println("----------+---------+---------+");
				if(i%3 == 0)
					System.err.print("|");
				System.err.print(sudoku[i]+"  ");
			}
			System.err.println("|");
			System.err.println("----------+---------+---------+");
		}
		
		if(SEECONSTRAINTS) {
			//System.err.println(constraints.toString());
			try {
				FileOutputStream fos = new FileOutputStream("Sudoku.cdl");
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeBytes("module tamago.example;\n\nservice Sudoku { \n method void initGrille(int[] grille) {\nid initGrille;\npre  ");
				dos.writeBytes(constraints.toString());
				dos.writeBytes("(forall g:int in 0..80 { (0 < grille[g]) && (grille[g] <= 9) });\n}\n}");
				dos.close();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	private static GIOperator different(int i, int j) {
		GIOperator diff = new GIOperator(TOpeName.opNe);
		diff.addOperand(var("grille",tab,ent(i)));
		diff.addOperand(var("grille",tab,ent(j)));
		//System.err.println("grille["+i+"] != grille["+j+"]");
		constraints.append(TamagoCCMakeReadableGExpression.toString(diff)+ " &&\n");
		return diff;
	}
}

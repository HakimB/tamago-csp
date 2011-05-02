/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import tamago.csp.Backtracking;
import tamago.csp.exception.TamagoCSPException;
import tamago.csp.stringbuilder.InferAutoFD;
import tamago.csp.var.Intvar;
import tamagocc.logger.TamagoCCLogger;
import tamagocc.util.Pair;

/**
 * @author Hakim Belhaouari
 *
 */
public class SAutoFusion {

	
	private static void getAccessibleState(Set<SState> set, SState a) {
		if(!set.add(a)) 
			return;
		for (SAbsTransition trans : a.getTransitions()) {
			if(trans.isEpsilon()) {
				getAccessibleState(set, trans.getEnd());
			}
		}
	}
	
	public static boolean hasFinishState(SAuto auto, Set<SState> set) {
		for(SState state : set) {
			if(auto.isFinalState(state)) {
				return true;
			}
		}
		return false;
	}
	
	
	private static String gen(SState a, SState b) {
		return ""+a.id()+"/"+b.id();
	}
	
	public static SAuto fusion(SAuto a, SAuto b) throws Exception {
		
		SAuto res = new SAuto();
		ArrayList<SState> visited = new ArrayList<SState>();
		fusion(res,visited,new Hashtable<String, SState>(), a, a.init(),b,b.init());
		if(res.getFinals().size() == 0) {
			if(TamagoCCLogger.getLevel() > 6) {
				new SPrintAutomaton(a,"FUSION A");
				new SPrintAutomaton(b,"FUSION B");
				new SPrintAutomaton(res,"RES FUSION");
			}
			throw new TamagoCSPException("No final state in the fusionned automated");
		}
		return res;
	}
	
	
	private static long fusionRedundancy(STransition ta,STransition tb) {
		if(ta.getRedundancy() <= 0)
			return tb.getRedundancy();
		if(tb.getRedundancy() <= 0)
			return ta.getRedundancy();
		
		return Math.min(ta.getRedundancy(), tb.getRedundancy());
	}
	
	private static SState getOrCreat(Hashtable<String, SState> hash,SAuto auto, SState sa,SState sb) {
		String idx = gen(sa,sb);
		SState current = null;
		if(hash.containsKey(idx)) 
			current = hash.get(idx);
		else {
			current = auto.creatState();
			hash.put(idx, current);
		}
		return current;
	}
	
	private static boolean hasAtleastOneTransition(SState a) {
		for (SAbsTransition trans : a.getTransitions()) {
			if(trans instanceof STransition)
				return true;
		}
		return false;
	}
	
	private static void fusion(SAuto auto,Collection<SState> visited, Hashtable<String, SState> hash,SAuto autoA, SState inita,SAuto autoB, SState initb) throws Exception {
		Stack<Pair<SState, SState>> pile = new Stack<Pair<SState,SState>>();
		
		pile.push(new Pair<SState, SState>(inita,initb));
		
		while(pile.size() > 0) {
			Pair<SState, SState> pair = pile.pop();
			SState sa = pair.l();
			SState sb = pair.r();
			
			SState current = getOrCreat(hash,auto,sa,sb); 
			TreeSet<SState> seta = new TreeSet<SState>();
			getAccessibleState(seta, sa);

			TreeSet<SState> setb = new TreeSet<SState>();
			getAccessibleState(setb, sb);


			if(hasFinishState(autoA, seta) && hasFinishState(autoB, setb))
				auto.addFinalState(current);

			for (SState stateA : seta) {
				if(hasAtleastOneTransition(stateA)) {				
					for (SState stateB : setb) {
						if(hasAtleastOneTransition(stateB)) {
							SState tmp = getOrCreat(hash, auto, stateA, stateB);
							if(tmp != current)
								new SEpsTransition(current,tmp);
						}
					}
				}
			}

			Collection<STransition> transA = SAuto.getAccessibleTransition(sa);
			Collection<STransition> transB = SAuto.getAccessibleTransition(sb);

			for (STransition ta : transA) {
				for (STransition tb : transB) {
					SLetter lettera = ta.getLetter();
					SLetter letterb = tb.getLetter();
					try {
						SLetter letter  = lettera.intersect(letterb);
						SState na = ta.getEnd();
						SState nb = tb.getEnd();
						SState next = getOrCreat(hash, auto, na, nb);
						long redundancy = fusionRedundancy((STransition)ta,(STransition)tb);
						new STransition(current,next,letter,redundancy);

						// condition d'arret dans les boucles
						//Pair<STransition, STransition> couple = new Pair<STransition, STransition>(ta,tb);
						if(visited.contains(next)) {
							//return;
						}
						else {
							visited.add(next);
							//fusion(auto, visited, hash, autoA, na, autoB, nb);
							pile.push(new Pair<SState, SState>(na,nb));
						}
					}
					catch(LetterNotCompatible lnc) {
						// lettre non compatible donc on passe
					}
				}
			}
		}
	}

	public static boolean isSimule(SAuto b,SAuto a) {
		ArrayList<Pair<SState, SState>> marked = new ArrayList<Pair<SState, SState>>();
		Stack<Pair<SState, SState>> pile = new Stack<Pair<SState,SState>>();
		
		pile.push(new Pair<SState, SState>(a.init(), b.init()));
		while(pile.size() > 0) {
			Pair<SState, SState> pair = pile.pop();
			if(!marked.contains(pair)) {
				marked.add(pair);
				SState sa = pair.l();
				SState sb = pair.r();

				Collection<STransition> tsa = SAuto.getAccessibleTransition(sa);
				for (STransition ta : tsa) {
					Collection<STransition> tsb = SAuto.getAccessibleTransition(sb);
					boolean find = false;
					for (STransition tb : tsb) {
						if(ta.equalsWithoutState(tb)) {
							find = true;
							pile.push(new Pair<SState, SState>(ta.getEnd(), tb.getEnd()));
						}	
					}
					if(!find)
						return false;
				}
			}
		}
		return true;
	}
	public static boolean isBisim(SAuto b, SAuto a) {
		return isSimule(b, a) && isSimule(a, b);
	}
	
	
	/*public static void main(String args[]) throws Exception {
		SAuto auto1 = SAuto.convertStringToAuto("xml");
		auto1 = SAuto.motifSequence(SAuto.motifAny(), auto1);
		
		SAuto auto2 = SAuto.motifSequence(SAuto.motifChar(new SAnyLetter()),SAuto.motifChar(new SAnyLetter()));
		auto2 = SAuto.motifSequence(auto2, auto2);
		auto2 = SAuto.motifSequence(auto2, SAuto.motifAny());
		
		new SPrintAutomaton(auto1,".xml");
		new SPrintAutomaton(auto2,"....");
		new SPrintAutomaton(SAutoFusion.fusion(auto1, auto2),"RES");
	}*/
	
	public static void main(String args[]) throws Exception {
		SAuto a = SAuto.motifAny();
		SAuto star = SAuto.convertStringToAuto("*");
		
		InferAutoFD fd = new InferAutoFD(new Intvar("fd",new Backtracking(),1,5));
		a = SAuto.motifSequence(a, star);
		
		new SPrintAutomaton(a,"pre+me");
		new SPrintAutomaton(fd.infer(),"post");
		
		SAuto fus = SAutoFusion.fusion(a, fd.infer());
		new SPrintAutomaton(fus,"res");
		
		System.out.println("Fin");
	}
	
}

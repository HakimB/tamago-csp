/**
 * 
 */
package tamago.csp.stringbuilder.automaton;

import java.util.Properties;

import tamago.csp.exception.TamagoCSPException;
import junit.framework.TestCase;

/**
 * @author Hakim Belhaouari
 *
 */
public class SAutoTest extends TestCase {

	public SAutoTest(String name) {
		super(name);
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * Test method for {@link tamago.csp.stringbuilder.automaton.SAuto#evalMinSize(tamago.csp.stringbuilder.automaton.SAuto)}.
	 */
	public final void testEvalMinSize() {
		SAuto a = SAuto.motifAny();
		assertEquals(0, SAuto.evalMinSize(a));
		
		SAuto b = SAuto.motifChar(new SAnyLetter());
		assertEquals(1, SAuto.evalMinSize(b));
		
		a = SAuto.motifEmpty();
		assertEquals(0, SAuto.evalMinSize(a));
		
		a = SAuto.motifRepeat(b);
		assertEquals(0, SAuto.evalMinSize(a));
		
		
		SAuto c ;
				
		c = SAuto.motifSequence(b,a);
		assertEquals(1, SAuto.evalMinSize(c));
		
	}

	/**
	 * Test method for {@link tamago.csp.stringbuilder.automaton.SAuto#evalMaxSize(tamago.csp.stringbuilder.automaton.SAuto)}.
	 */
	public final void testEvalMaxSize() {
		SAuto a = SAuto.motifAny();
		assertEquals(-2, SAuto.evalMaxSize(a));
		
		SAuto b = SAuto.motifChar(new SAnyLetter());
		assertEquals(1, SAuto.evalMaxSize(b));
		
		a = SAuto.motifEmpty();
		assertEquals(0, SAuto.evalMaxSize(a));
		
		a = SAuto.motifRepeat(b);
		assertEquals(-2, SAuto.evalMaxSize(a));
		
		
		SAuto c ;
				
		c = SAuto.motifSequence(b,a);
		assertEquals(-2, SAuto.evalMaxSize(c));
	}

	/**
	 * Test method for {@link tamago.csp.stringbuilder.automaton.SAuto#subgraph(tamago.csp.stringbuilder.automaton.SAuto, int, int)}.
	 */
	public final void testSubgraph() {
		SAuto auto = new SAuto();
		SState[] p = new SState[8];
		for(int i = 0; i < 8;i++) {
			p[i] = auto.creatState();
		}
		
		auto.addFinalState(p[6]);
		
		SLetter any = new SAnyLetter();
		new STransition(p[0],p[1],any);
		new STransition(p[0],p[2],any);
		new STransition(p[1],p[3],new SGenericLetter("a"));
		new STransition(p[2],p[4],new SGenericLetter("b"));
		new STransition(p[4],p[3],any);
		new STransition(p[2],p[5],new SGenericLetter("c"));
		new STransition(p[5],p[7],any);
		new STransition(p[7],p[6],any);
		new STransition(p[7],p[7],any);
		new STransition(p[3],p[6],any);
		
		SAuto sub = SAuto.subgraph(auto, 1, 2);
		try {
			new SPrintAutomaton(auto);
			new SPrintAutomaton(sub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(sub.isAccepted("a"));
		assertTrue(sub.isAccepted("b"));
		assertTrue(sub.isAccepted("c"));
		assertFalse(sub.isAccepted("d"));
	}

	/**
	 * Test method for {@link tamago.csp.stringbuilder.automaton.SAuto#searchRandomWord(tamago.csp.stringbuilder.automaton.SAuto)}.
	 */
	public final void testSearchRandomWord() {
		SAuto auto = new SAuto();
		SState[] p = new SState[8];
		for(int i = 0; i < 8;i++) {
			p[i] = auto.creatState();
		}
		auto.addFinalState(p[6]);
		
		SLetter any = new SAnyLetter();
		new STransition(p[0],p[1],any);
		new STransition(p[0],p[2],any);
		new STransition(p[1],p[3],new SGenericLetter("a"));
		new STransition(p[2],p[4],new SGenericLetter("b"));
		new STransition(p[4],p[3],any);
		new STransition(p[2],p[5],new SGenericLetter("c"));
		new STransition(p[5],p[7],any);
		new STransition(p[7],p[6],any);
		new STransition(p[7],p[7],any);
		new STransition(p[3],p[6],any);
		
		try {
			String mot = SAuto.searchRandomWord(auto);
			System.out.println("Mot:"+mot);
			assertTrue(auto.isAccepted(mot));
		} catch (TamagoCSPException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static void main(String args[]) {
		Properties props = System.getProperties();
		for (Object string : props.keySet()) {
			System.out.println(string+"\t\t\t\t\t\t"+props.getProperty((String) string));	
		}
		
	}
}

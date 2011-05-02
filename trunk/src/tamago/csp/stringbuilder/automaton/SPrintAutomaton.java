/**
 * 
 */
package tamago.csp.stringbuilder.automaton;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.TreeSet;

import javax.swing.JFrame;

import salvo.jesus.graph.DirectedGraph;
import salvo.jesus.graph.DirectedGraphImpl;
import salvo.jesus.graph.Edge;
import salvo.jesus.graph.Vertex;
import salvo.jesus.graph.VertexImpl;
import salvo.jesus.graph.visual.GraphEditor;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.layout.ForceDirectedLayout;

/**
 * This class is usefull only if we want debug a contract is show a frame with the 
 * graph of the specified finite state automaton of the behavior.
 * <br>
 * This class need the openjgraph.jar.
 * @author Hakim Belhaouari
 *
 */
public final class SPrintAutomaton extends JFrame {

	private static final long serialVersionUID = 1273032580378196435L;
	private DirectedGraph graph;
	private GraphEditor grapheditor;
	private SAuto auto;
	private Hashtable<SState, Vertex> allstates;
	
	private static long fen = 0; 
	
	public SPrintAutomaton(SAuto auto) throws Exception {
		this(auto,"");
	}
	
	public SPrintAutomaton(SAuto auto,String title) throws Exception {
		setTitle("F"+(fen++)+": "+title);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.auto = auto;
		allstates = new Hashtable<SState, Vertex>();
		
		graph = new DirectedGraphImpl();
		grapheditor = new GraphEditor();
		
		
		grapheditor.setGraph(graph);
		prepareGraph();
				
		ForceDirectedLayout fdl = new ForceDirectedLayout(grapheditor.getVisualGraph());
		fdl.setSpringLength(150);
		
		grapheditor.getVisualGraph().setGraphLayoutManager(fdl);		
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(grapheditor,BorderLayout.CENTER);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = new Dimension( screenSize.width/2, screenSize.height/2);
		    
		this.setSize( frameSize );
		//this.setLocation((int)(screenSize.getWidth() - frameSize.getWidth()) / 2, (int)(screenSize.getHeight() - frameSize.getHeight()) / 2);
		setVisible(true);
		
	}
	
	
	
	private void prepareGraph() throws Exception {
		for(SState key : auto.getStates()) {
			Vertex vertex = new VertexImpl(""+ key.id());
			allstates.put(key,vertex);
			graph.add(vertex);
			if(key == auto.init()) {
				VisualVertex vv = grapheditor.getVisualGraph().getVisualVertex(vertex);
				vv.setFillcolor(Color.GREEN);
			}
			else if(auto.getFinals().contains(key)) {
				VisualVertex vv = grapheditor.getVisualGraph().getVisualVertex(vertex);
				vv.setFillcolor(Color.RED);
			}
		}
		
		TreeSet<SState> visited = new TreeSet<SState>();
		
		ride(visited,auto.init());
	}



	private void ride(TreeSet<SState> visited, SState state) throws Exception {
		if(visited.contains(state)) return;
		else
			visited.add(state);
		for(SAbsTransition t : state.getTransitions()) {
			Vertex v1 = allstates.get(state);
			Vertex v2 = allstates.get(t.getEnd());
			if(v1 == v2) {
				VisualVertex vv = grapheditor.getVisualGraph().getVisualVertex(v1);
				vv.setOutlinecolor(Color.ORANGE);
				vv.setLabel(vv.getLabel()+t.toString());
			}
			Edge edge = graph.addEdge(v1,v2);
			edge.setFollowVertexLabel(false);
			edge.setLabel(t.toString());
			ride(visited,t.getEnd());
		}
		
	}

	
	
}

package pentos.g3;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import pentos.sim.Move;

import java.util.*;

public class Player implements pentos.sim.Player {

	// private Random gen = new Random();
	private Set<Cell> road_cells = new HashSet<Cell>();
	private boolean road_built;


	public void init() { // function is called once at the beginning before play is called
		this.road_built = false;
	}

	public Move play(Building request, Land land) {

		// find all valid building locations and orientations
		ArrayList<Move> moves = findBuildableMoves(request, land);

		if (moves.isEmpty()) return new Move(false);
		
		// find all objective function values of each move, means "how good the move is"
		ArrayList<Integer> objs = findObjectiveOfMoves(moves, land, request);
		int index = findSmallestObj(objs);
		
		Move chosen = moves.get(index);
		Set<Cell> shiftedCells = shiftedCellsFromMove(chosen);
		Set<Cell> roadCells = new HashSet<Cell>();

		if(!road_built)
		{
			roadCells = buildRoad1(land.side);
			road_built = true;
		}
		else
		{
			roadCells = findShortestRoad(shiftedCells, land);
		}

		if (roadCells != null) {
			chosen.road = roadCells;
			road_cells.addAll(roadCells);
				/*
				if (request.type == Building.Type.RESIDENCE) { // for residences, build random ponds and fields connected to it
					Set<Cell> markedForConstruction = new HashSet<Cell>();
					markedForConstruction.addAll(roadCells);
					chosen.water = randomWalk(shiftedCells, markedForConstruction, land, 4);
					markedForConstruction.addAll(chosen.water);
					chosen.park = randomWalk(shiftedCells, markedForConstruction, land, 4);
				}
				*/
			return chosen;
		} else {
			return new Move(false);
		}
	}

	//build the roads based on size of land
    private Set<Cell> buildRoad1(int side){
		Set<Cell> initRoadCells = new HashSet<Cell>();
		int roadOffset = 10;
		int bottomRow = roadOffset;
		int topRow = side-roadOffset-1; // subtract extra 1 for indexing
		int leftColumn = roadOffset;
		int rightColumn = side-roadOffset-1;

		for (int i=0; i<40; i++)
		{
			initRoadCells.add(new Cell(bottomRow, i));
		}
		for (int i=11; i<40; i++)
		{
			initRoadCells.add(new Cell(i, rightColumn));	
		}
		for (int i=38; i>9; i--)
		{
			initRoadCells.add(new Cell(topRow, i));
		}
		for (int i=38; i>9; i--)
		{
			initRoadCells.add(new Cell(i, leftColumn));
		}
		return initRoadCells;
    }

	private int objective(Set<Cell> shiftedCells, Land land) {
		int edge_increase = 0; // the increase of new edge: the less, the better

		for (Cell shiftedCell : shiftedCells) {
			for (Cell adjCell : shiftedCell.neighbors()) {
				if (!shiftedCells.contains(adjCell) && land.unoccupied(adjCell))
					edge_increase += 1;
				if (!shiftedCells.contains(adjCell) && !land.unoccupied(adjCell))
					edge_increase -= 2;
			}
		}

		return edge_increase;
	}

	private ArrayList<Move> findBuildableMoves(Building request, Land land) {
		ArrayList<Move> moves = new ArrayList<Move> ();
		for (int i = 0 ; i < land.side ; i++) {
			for (int j = 0 ; j < land.side ; j++) {
				Cell p = new Cell(i, j);
				Building[] rotations = request.rotations();
				for (int ri = 0 ; ri < rotations.length ; ri++) {
					Building b = rotations[ri];
					if (land.buildable(b, p)) {
						moves.add(new Move(true, request, p, ri, new HashSet<Cell>(), new HashSet<Cell>(), new HashSet<Cell>()));
					}
				}
			}
		}
		return moves;
	}

	private Set<Cell> shiftedCellsFromMove(Move move) {
		Set<Cell> shiftedCells = new HashSet<Cell>();
		for (Cell x : move.request.rotations()[move.rotation])
			shiftedCells.add(new Cell(x.i+move.location.i,x.j+move.location.j));
		return shiftedCells;
	}

	private ArrayList<Integer> findObjectiveOfMoves(ArrayList<Move> moves, Land land, Building request) {
		ArrayList<Integer> objs = new ArrayList<Integer> ();
		int i = 0;
		for (Move move : moves) {
			Set<Cell> shiftedCells = shiftedCellsFromMove(move);
			int obj = 0;
			obj += (request.type == Building.Type.FACTORY ? i : -i);
			obj += 100 * objective(shiftedCells, land);
			// obj = findShortestRoad(shiftedCells, land) == null ? obj : Integer.MAX_VALUE;
			// include other objective functions
			objs.add(obj);
			i += 1;
		}

		return objs;
	}

	private int findSmallestObj(ArrayList<Integer> objs) {
		int index = 0;
		int i = 0;
		int min_value = Integer.MAX_VALUE;
		for (Integer obj : objs) {
			if (min_value > obj) {
				min_value = obj;
				index = i;
			}
			i += 1;
		}
		return index;
	}

	// build shortest sequence of road cells to connect to a set of cells b
	private Set<Cell> findShortestRoad(Set<Cell> b, Land land) {
		Set<Cell> output = new HashSet<Cell>();
		boolean[][] checked = new boolean[land.side][land.side];
		Queue<Cell> queue = new LinkedList<Cell>();
		// add border cells that don't have a road currently
		Cell source = new Cell(Integer.MAX_VALUE,Integer.MAX_VALUE); // dummy cell to serve as road connector to perimeter cells
		for (int z=0; z<land.side; z++) {
			if (b.contains(new Cell(0,z)) || b.contains(new Cell(z,0)) || b.contains(new Cell(land.side-1,z)) || b.contains(new Cell(z,land.side-1))) //if already on border don't build any roads
				return output;
			if (land.unoccupied(0,z))
				queue.add(new Cell(0,z,source));
			if (land.unoccupied(z,0))
				queue.add(new Cell(z,0,source));
			if (land.unoccupied(z,land.side-1))
				queue.add(new Cell(z,land.side-1,source));
			if (land.unoccupied(land.side-1,z))
				queue.add(new Cell(land.side-1,z,source));
		}
		// add cells adjacent to current road cells
		for (Cell p : road_cells) {
			for (Cell q : p.neighbors()) {
				if (!road_cells.contains(q) && land.unoccupied(q) && !b.contains(q)) 
					queue.add(new Cell(q.i,q.j,p)); // use tail field of cell to keep track of previous road cell during the search
			}
		}
		while (!queue.isEmpty()) {
			Cell p = queue.remove();
			checked[p.i][p.j] = true;
			for (Cell x : p.neighbors()) {		
				if (b.contains(x)) { // trace back through search tree to find path
					Cell tail = p;
					while (!b.contains(tail) && !road_cells.contains(tail) && !tail.equals(source)) {
						output.add(new Cell(tail.i,tail.j));
						tail = tail.previous;
					}
					if (!output.isEmpty())
						return output;
				}
				else if (!checked[x.i][x.j] && land.unoccupied(x.i,x.j)) {
					x.previous = p;
					queue.add(x);	      
				}
			}
		}
		if (output.isEmpty() && queue.isEmpty())
			return null;
		else
			return output;
    }

	/*
	// walk n consecutive cells starting from a building. Used to build a random field or pond. 
	private Set<Cell> randomWalk(Set<Cell> b, Set<Cell> marked, Land land, int n) {
		ArrayList<Cell> adjCells = new ArrayList<Cell>();
		Set<Cell> output = new HashSet<Cell>();
		for (Cell p : b) {
			for (Cell q : p.neighbors()) {
				if (land.isField(q) || land.isPond(q))
					return new HashSet<Cell>();
				if (!b.contains(q) && !marked.contains(q) && land.unoccupied(q))
					adjCells.add(q); 
			}
		}
		if (adjCells.isEmpty()) {
			return new HashSet<Cell>();
		}
		Cell tail = adjCells.get(gen.nextInt(adjCells.size()));
		for (int ii=0; ii<n; ii++) {
			ArrayList<Cell> walk_cells = new ArrayList<Cell>();
			for (Cell p : tail.neighbors()) {
				if (!b.contains(p) && !marked.contains(p) && land.unoccupied(p) && !output.contains(p))
					walk_cells.add(p);		
			}
			if (walk_cells.isEmpty()) {
				//return output; //if you want to build it anyway
				return new HashSet<Cell>();
			}
			output.add(tail);	    
			tail = walk_cells.get(gen.nextInt(walk_cells.size()));
		}
		return output;
	}
	*/
}

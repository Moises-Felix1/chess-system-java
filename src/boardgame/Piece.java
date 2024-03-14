package boardgame;

public class Piece {
	
	protected Position position;
	//association:
	private Board board;
	
	public Piece() {
	}

	protected Piece(Board board) {
		this.board = board;
		position = null;
	}

	protected Board getBoard() {
		return board;
	}
	
	
	
	
	
	
}

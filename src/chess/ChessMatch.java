package chess;

import boardgame.Board;

public class ChessMatch {

	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
	}
	
	public ChessPiece[][] getPieces(){
		ChessPiece mat[][] = new ChessPiece[board.getRows()][board.getColumns()];
		for(int l=0; l<board.getRows();l++) {
			for(int c=0; c<board.getColumns(); c++) {
				mat [l][c] = (ChessPiece) board.piece(l, c);
			}
		}
		return mat;
	}
}

package application;

import boardgame.Board;
import chess.ChessMatch;

public class Program {

	public static void main(String[] args) {
		
		ChessMatch cmatch = new ChessMatch();
		UI.printBoard(cmatch.getPieces());

	}

}

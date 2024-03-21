package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkmate;
	
	private List<Piece>piecesOnTheBoard;
	private List<Piece>capturedPieces;
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn =1;
		currentPlayer = Color.WHITE;
		check = false;
		piecesOnTheBoard = new ArrayList<>();
		capturedPieces = new ArrayList<>();
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckmate() {
		return checkmate;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece PerformChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturePiece = makeMove(source, target);
		
		if(testCheck(currentPlayer) == true) {
			undoMove(source, target, capturePiece);
			throw new ChessException("You cant put yourseft in check!");
		}
		
		check = (testCheck(opponent(currentPlayer)))? true: false;
		
		if(testCheckmate(opponent(currentPlayer))) {
			checkmate = true;
		}else {
			nextTurn();
		}
		return (ChessPiece) capturePiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.romvePiece(source);
		p.increaseMoveCount();
		Piece capturePiece = board.romvePiece(target);
		board.placePiece(p, target);
		
		if(capturePiece != null) {
			piecesOnTheBoard.remove(capturePiece);
			capturedPieces.add(capturePiece);
		}
		return capturePiece;
	}
	
	private void undoMove(Position source, Position Target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.romvePiece(Target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, Target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece cant move to target position.");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE)? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		//List<Piece>list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : piecesOnTheBoard) {
			if(p instanceof King && ((ChessPiece)p).getColor() == color) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no "+color+ " King on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		//List<Piece>opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for(Piece op : piecesOnTheBoard) {
			boolean mat[][] = op.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()] && ((ChessPiece)op).getColor() == opponent(color)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckmate(Color color) {
		if(!testCheck(color) == true) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			boolean mat[][] = p.possibleMoves();
			for(int i = 0; i < board.getRows(); i++) {
				for(int j = 0; j < board.getColumns(); j++) {
					if(mat[i][j] == true) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('h', 7, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));

        placeNewPiece('b', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 8, new King(board, Color.BLACK));
	}
}

package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private final Board _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        _board = new Board(size);
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        _board = new Board(rawValues);
        this._score = score;
        this._maxScore = maxScore;
        this._gameOver = gameOver;
    }

    /** Same as above, but gameOver is false. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore) {
        this(rawValues, score, maxScore, false);
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     * */
    public Tile tile(int col, int row) {
        return _board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return _board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (_gameOver) {
            _maxScore = Math.max(_score, _maxScore);
        }
        return _gameOver;
    }

    /** Return the current score. */
    public int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        _score = 0;
        _gameOver = false;
        _board.clear();
        setChanged();
    }

    /** Allow initial game board to announce a hot start to the GUI. */
    public void hotStartAnnounce() {
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        _board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void tilt(Side side) {
        _board.setViewingPerspective(side);
        for(int col = 0; col <_board.size();col++){
            removenull(col);
            merger(col);
            removenull(col);
            }
        _board.setViewingPerspective(Side.NORTH);
        checkGameOver();

    }

    public void removenull(int col){ //checks for null spaces if any and moves each position to the position above
        for(int row = _board.size()-1; row >= 0; row--){
            Tile ct = _board.tile(col, row);
            if(ct == null){
                for(int row2= row-1; row2>= 0; row2--){
                    if(_board.tile(col, row2) != null){
                    _board.move(col, row, _board.tile(col, row2));
                    break;

                }
            }
    }}}
    public void merger(int col)     //performs the operations if the current tile is not null
        {
        for(int ra = _board.size()-1; ra >= 0; ra--){
            Tile ct = _board.tile(col, ra);
            if(ct != null){
                for(int row2 = ra-1; row2 >= 0; row2--){
                    Tile nt = _board.tile(col,row2);
                        if(nt != null)
                        {
                            if(ct.value() == nt.value()){
                                _score+= (2*nt.value());
                                _board.move(col, ra, nt);
                                ra = row2;
                                break;
                            }
                            if(ct.value() != nt.value()){
                        }

                        }
                    }}

            }
        }









    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        _gameOver = checkGameOver(_board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        int i = 0; int j = 0;
        for (i =0; i < b.size(); i++) {
            for(j = 0; j< b.size(); j ++) {
                if((b.tile(i, j))== null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int i = 0; int j = 0;
        for (i =0; i < b.size(); i++) {
            for(j = 0; j< b.size(); j ++) {
                if((b.tile(i, j)) != null && b.tile(i, j).value() == MAX_PIECE){
                    return true;
                }
            }
    }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if ((emptySpaceExists(b)) == true) {
            return true;
        }

        int p = 0;
        int q = 0;
        for (p = 0; p < b.size() - 1; p++) {
            for (q = 0; q < b.size() - 1; q++) {
                if (b.tile(p, q).value() == b.tile(p, q + 1).value() || (b.tile(p, q).value() == b.tile(p + 1, q).value())) {
                    return true;
                }

            }

        }
        int fir_row = b.size() - 1;
        int x;
        for (x = 0; x < b.size() - 1; x++) {
            if (b.tile(x, fir_row).value() == b.tile(x + 1, fir_row).value()) {
                return true;
            }
        }
        int col = b.size() - 1;
        int y;
        for (y = 0; y < b.size() - 1; y++) {
            if (b.tile(col, y).value() == b.tile(col, y + 1).value()) {
                return true;
            }
        }
        return false;
    }
    /** Returns the model as a string, used for debugging. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    /** Returns whether two models are equal. */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    /** Returns hash code of Model’s string. */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

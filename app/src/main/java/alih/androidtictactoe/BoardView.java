package alih.androidtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import alih.androidtictactoe.R;

/**
 * Created by alih on 2/17/15.
 */
public class BoardView extends View {
    // Width of the board grid lines
    public static final int GRID_LINE_WIDTH = 6;

    private TicTacToeGame mGame;
    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;
    private Paint mPaint;

    public BoardView(Context context) {
        super(context);
        initialize();
    }
    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    public void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_co);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_st);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
// Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();
// Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_LINE_WIDTH);

        // Draw the vertical and horizontal board lines
        // horiz
        canvas.drawLine(0, boardHeight/3, boardWidth, boardWidth/3, mPaint);
        canvas.drawLine(0, boardHeight*2/3, boardWidth, boardWidth*2/3, mPaint);
        // vert
        canvas.drawLine(boardWidth/3, 0, boardWidth/3, boardWidth, mPaint);
        canvas.drawLine(boardWidth*2/3, 0, boardWidth*2/3, boardWidth, mPaint);


        // Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;
// Define the boundaries of a destination rectangle for the image
            int yTopLeft = row * getBoardCellWidth();
            int xTopLeft = col * getBoardCellHeight();
            int yBottomRight = row * getBoardCellWidth() + getBoardCellWidth();
            int xBottomRight = col * getBoardCellHeight() + getBoardCellHeight();
            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null, // src
                        new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
                        null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null, // src
                        new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
                        null);
            }
        }
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }
    public int getBoardCellHeight() {
        return getHeight() / 3;
    }
}

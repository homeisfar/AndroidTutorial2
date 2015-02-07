package alih.androidtictactoe;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AndroidTicTacToe extends ActionBarActivity {

    private TicTacToeGame mGame;
    private boolean mGameOver;

    // Buttons making up the game board
    private Button mBoardButtons[];

    // Text to be displayed
    private TextView mInfoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById (R.id.one);
        mBoardButtons[1] = (Button) findViewById (R.id.two);
        mBoardButtons[2] = (Button) findViewById (R.id.three);
        mBoardButtons[3] = (Button) findViewById (R.id.four);
        mBoardButtons[4] = (Button) findViewById (R.id.five);
        mBoardButtons[5] = (Button) findViewById (R.id.six);
        mBoardButtons[6] = (Button) findViewById (R.id.seven);
        mBoardButtons[7] = (Button) findViewById (R.id.eight);
        mBoardButtons[8] = (Button) findViewById (R.id.nine);

        mInfoTextView = (TextView) findViewById (R.id.information);

        mGame = new TicTacToeGame ();

        startNewGame ();

    }

    // Set up the game board

    private void startNewGame (){
        mGame.clearBoard ();

        mGameOver = false;

        // reset the board
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText ("");
            mBoardButtons[i].setEnabled (true);
            mBoardButtons[i].setOnClickListener (new ButtonClickListener(i));
        }

        //Human goes first
        mInfoTextView.setText ("You go first");
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("It's Android's turn.");
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText("It's your turn.");
                else if (winner == 1)
                    mInfoTextView.setText("It's a tie!");
                else if (winner == 2)
                    mInfoTextView.setText("You won!");
                else
                    mInfoTextView.setText("Android won!");
                if (winner > 0)
                    mGameOver = true;
            }
        }


        private void setMove(char player, int location) {

            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }
    }

}

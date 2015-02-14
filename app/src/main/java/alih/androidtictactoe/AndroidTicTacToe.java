package alih.androidtictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidTicTacToe extends ActionBarActivity {

    private TicTacToeGame mGame;
    private boolean mGameOver;
    private boolean mFirstPlayer;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    // Buttons making up the game board
    private Button mBoardButtons[];
    private Button mNewGameB;

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
        mNewGameB = (Button) findViewById(R.id.newGame);


        mInfoTextView = (TextView) findViewById (R.id.information);

        mGame = new TicTacToeGame ();
        mFirstPlayer = false;

        startNewGame ();

    }

    // Set up the game board

    private void startNewGame (){
        mGame.clearBoard ();

        mGameOver = false;
        mFirstPlayer = !mFirstPlayer;

        // reset the board
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText ("");
            mBoardButtons[i].setEnabled (true);
            mBoardButtons[i].setOnClickListener (new ButtonClickListener(i));
        }
        mNewGameB.setOnClickListener (new NewGameClickListener());

        //Human goes first
        mInfoTextView.setText(R.string.turn_human);

        if (mFirstPlayer)
        {
            int move = mGame.getComputerMove();
            setCMove(TicTacToeGame.COMPUTER_PLAYER, move);
        }
    }


    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_medium),
                        getResources().getString(R.string.difficulty_expert)};


                // selected is the radio button that should be selected.

                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog

                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;

        case DIALOG_QUIT_ID:
        // Create the quit confirmation dialog

        builder.setMessage(R.string.quit_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AndroidTicTacToe.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null);
        dialog = builder.create();

        break;
    }
        return dialog;
    }




    private void setCMove(char player, int location) {

        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.new_game)
        {
            startNewGame ();
            return true;
        }
        if (id == R.id.ai_difficulty)
        {
            showDialog (DIALOG_DIFFICULTY_ID);
            return true;
        }
        if (id == R.id.quit)
        {
            showDialog (DIALOG_QUIT_ID);
            return true;
        }


        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startNewGame ();
//            return true;
//        }
//        else // This is super awful but I can't figure out the ID
//            startNewGame ();



        return super.onOptionsItemSelected (item);
    }

    // New Game click. Can this be combined into the other click listener?
    private class NewGameClickListener implements View.OnClickListener {
        public void onClick (View view)
        {
            startNewGame();
        }
    }


    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener (int location) {
            this.location = location;
        }

        public void onClick (View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove (TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1)
                    mInfoTextView.setText(R.string.result_tie);
                else if (winner == 2)
                    mInfoTextView.setText(R.string.result_human_wins);
                else
                    mInfoTextView.setText(R.string.result_computer_wins);
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

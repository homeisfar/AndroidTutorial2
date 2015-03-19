package alih.androidtictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AndroidTicTacToe extends ActionBarActivity {

    private TicTacToeGame mGame;
    private boolean mGameOver;
    private boolean mFirstPlayer;
    private BoardView mBoardView;
    private int hWin, cWin, tie;
    private char mTurn;
    private SharedPreferences mPrefs;
    private boolean mSoundOn;

    // for all the sounds we play, even though the human and CPU sounds play almost simultaneously
    private SoundPool mSounds;
    private int mHumanMoveSoundID;
    private int mComputerMoveSoundID;

//    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    private static final String TAG = "TicTacToeGame";

    // Text to be displayed
    private TextView mInfoTextView;
    private TextView mWinTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        hWin = 0;
        cWin = 0;
        tie = 0;

        mInfoTextView = (TextView) findViewById (R.id.information);
        mWinTextView = (TextView) findViewById (R.id.winstats);
        mGame = new TicTacToeGame ();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        mFirstPlayer = false;

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        hWin = mPrefs.getInt("hWin", 0);
        cWin = mPrefs.getInt("cWin", 0);
        tie = mPrefs.getInt("tie", 0);
        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[mPrefs.getInt("difficulty", 0)]);

        /* If the game begins without a bundled state, that means this device has never played
           this version of TTT before. If so, start a new game. If there are saved preferences,
           load them in. This means stats will be saved until reset from within the app itself.
         */
        if (savedInstanceState == null)
        {
            mSoundOn = true;
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            startNewGame ();
        }
        else
        {
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mTurn = savedInstanceState.getChar("mTurn");
            mSoundOn = savedInstanceState.getBoolean("mSoundOn");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            displayScores();
        }
    }

    /* Bundled up to allow for rotating the device */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mTurn", mTurn);
        outState.putBoolean("mSoundOn", mSoundOn);
    }

    // Set up the game board
    private void startNewGame (){
        mGame.clearBoard ();

        mGameOver = false;
        mFirstPlayer = !mFirstPlayer;

        // reset the board
        mBoardView.invalidate(); // Leads to a redraw of the board view

        //Human (now always) goes first
        mInfoTextView.setText(R.string.turn_human);

        displayScores();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("hWin", hWin);
        ed.putInt("cWin", cWin);
        ed.putInt("tie", tie);
        ed.putInt("difficulty", mGame.getDifficultyLevel().ordinal());
        ed.apply();
    }

    private void displayScores(){
        mWinTextView.setText("You: " + hWin + " Cpu: " + cWin + " Tie: " + tie);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
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

        if (id == R.id.new_game)
        {
            startNewGame ();
            return true;
        }
        if (id == R.id.settings)
        {
            startActivityForResult (new Intent(this, Settings.class), 0);
            return true;
        }
        if (id == R.id.quit)
        {
            showDialog (DIALOG_QUIT_ID);
            return true;
        }
        if (id == R.id.reset)
        {
            hWin = 0;
            cWin = 0;
            tie = 0;
            startNewGame ();
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
// Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String[] levels = getResources().getStringArray(R.array.list_difficulty_level);
// set difficulty, or use hardest if not present,
            String difficultyLevel = mPrefs.getString("difficulty_level", levels[levels.length - 1]);
            int i = 0;
            while(i < levels.length) {
                if(difficultyLevel.equals(levels[i])) {
                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[i]);
                    i = levels.length; // to stop loop
                }
                i++;
            }
        }
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){

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
                {
                    mInfoTextView.setText(R.string.result_tie);
                    tie++;
                    prepDownloadImageActivity(1, getString(R.string.result_tie));
                }
                else if (winner == 2)
                {
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    hWin++;
                    String victory_message = mPrefs.getString("victory_message", defaultMessage);
                    prepDownloadImageActivity(2, victory_message);
                }
                else
                {
                    String defaultMessage = getResources().getString(R.string.result_computer_wins);
                    mInfoTextView.setText(mPrefs.getString("loss_message", defaultMessage));
                    cWin++;
                    String loss_message = mPrefs.getString("loss_message", defaultMessage);
                    prepDownloadImageActivity(3, loss_message);
                }
                if (winner > 0)
                {
                    mGameOver = true;
                    mWinTextView.setText("You: " + hWin + " Cpu: " + cWin + " Tie: " + tie);
                }
            }

// So we aren't notified of continued events when finger is moved
            return false;
        }
    };

        private boolean setMove(char player, int location) {
            if (mGame.setMove(player, location))
            {
                mBoardView.invalidate(); //Board needs to be redrawn

                // soundID, leftVolume, rightVolume, priority, loop, rate
                if (mSoundOn)
                if (player == mGame.HUMAN_PLAYER)
                    mSounds.play(mHumanMoveSoundID, 1, 1, 1, 0, 1);
                else
                    mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);

                return true;
            }
            return false;
        }

    @Override
    protected void onResume() {
        super.onResume();
        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
// 2 = maximum sounds to play at the same time,
// AudioManager.STREAM_MUSIC is the stream type typically used for games
// 0 is the "the sample-rate converter quality. Currently has no effect. Use 0 for the default."
        mHumanMoveSoundID = mSounds.load(this, R.raw.press, 1);
// Context, id of resource, priority (currently no effect)
        mComputerMoveSoundID = mSounds.load(this, R.raw.ai_press, 1);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "in onPause");
        if(mSounds != null) {
            mSounds.release();
            mSounds = null;
        }
    }

    private void prepDownloadImageActivity(int winner, String message){
        Intent intent = new Intent(this, DownloadImage.class);
        intent.putExtra("winner", winner);
        intent.putExtra("message", message);
        startActivity(intent);
    }
}

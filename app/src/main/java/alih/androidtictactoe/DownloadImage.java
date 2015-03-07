package alih.androidtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadImage extends ActionBarActivity {
    TextView mDownloadingMessage;
    TextView mWinnerMessage;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);

        //Initialize TextViews and ImageView:
        mDownloadingMessage = (TextView) findViewById(R.id.message_downloading);
        mWinnerMessage = (TextView) findViewById(R.id.message_winner);
        mImageView = (ImageView) findViewById(R.id.image);

        //Set the TextView with the downloading image status to its initial value:
        mDownloadingMessage.setText(R.string.downloading_image);

        int winner = getIntent().getIntExtra("winner", 0);
        String message = getIntent().getStringExtra("message");

        String urlString = "";
        if(winner == 0){
            mWinnerMessage.setText("ERROR!!!");
        }else if(winner == 1){
            urlString = getString(R.string.url_tie);
            mWinnerMessage.setText(message);
        }else if(winner == 2){
            urlString = getString(R.string.url_winner);
            mWinnerMessage.setText(message);
        }else if (winner == 3){
            urlString = getString(R.string.url_loser);
            mWinnerMessage.setText(message);
        }//end if else

        //check to see if device is connected to the internet before proceeding to download image:
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data:
            new DownloadImageTask().execute(urlString);
        } else {
            // display error:
            mDownloadingMessage.setText(R.string.no_network_connection);
        }//end if else
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download_image, menu);
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

        return super.onOptionsItemSelected(item);
    }

    //INNER CLASS FOR ASYNC TASK:
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... params) {

            Bitmap returnImage = null;
            // params comes from the execute() call: params[0] is the urlString.
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }//end try catch block

            HttpURLConnection urlConnection = null;
            if(url != null){
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }//end try catch block
            }//end if

            if(urlConnection != null){
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    returnImage = BitmapFactory.decodeStream(in); //note, this is not a return statement…the variable
                                                                //is named ‘returnImage’
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }//end try catch finally block
            }//end if
            return returnImage;
        }//end doInBackground method
        protected void onPostExecute(Bitmap result) {
            if(result != null){
                mImageView.setImageBitmap(result);
                mDownloadingMessage.setText(R.string.download_complete);
            }//end if
        }//end onPostExecute method
    }//end DownloadImageTask private inner Async Task Class.
}//end DownloadImage Class.
//}

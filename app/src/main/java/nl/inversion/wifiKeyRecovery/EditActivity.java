package nl.inversion.wifiKeyRecovery;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

import nl.inversion.wifiKeyRecovery.util.UsefulBits;

public class EditActivity extends Activity {
	final String TAG =  this.getClass().getName();
    int sdkInt;

    private static final String INTENT_EXPORT_NAME_INFO = "info";
    private static final String INTENT_EXPORT_NAME_TIME = "time";

	private EditText mFldInfo;
    private String mTimeDate;
	private UsefulBits mUsefulBits;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit);

        sdkInt = android.os.Build.VERSION.SDK_INT;
        if (sdkInt > Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

		final Bundle extras = getIntent().getExtras();
		mUsefulBits = new UsefulBits(getApplicationContext());

        mFldInfo = (EditText) findViewById(R.id.fld_export_text);

		if(extras != null) {
			mTimeDate = extras.getString(INTENT_EXPORT_NAME_TIME);
			mFldInfo.setText(getString(R.string.text_wifi_password_recovery)  + " @ " + mTimeDate +"\n\n");
			mFldInfo.append(extras.getString(INTENT_EXPORT_NAME_INFO));
		}

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_share:
                shareResults();
                return true;

            case R.id.action_save:
                writeToSD();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeToSD() {
        try {
            final File folder = Environment.getExternalStorageDirectory();
            final String filename = "wifikeyrecovery_" + mTimeDate + ".txt";
            final String contents = mFldInfo.getText().toString();
            mUsefulBits.saveToFile(filename, folder, contents);
        } catch (Exception e) {
            Toast.makeText(this, R.string.text_could_not_write_file, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed get external storage directory ");
            e.printStackTrace();
        }
    }

	private void shareResults(){
		final Intent sendIntent = new Intent(Intent.ACTION_SEND);
		final String text = mFldInfo.getText().toString();
		final String subject =  getString(R.string.text_wifi_password_recovery)  + " @ " + mTimeDate;

		sendIntent.setType("text/plain");
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
		final Intent share = Intent.createChooser(
				sendIntent,
				getString(R.string.label_share_dialogue_title));
		startActivity(share);
	}
}
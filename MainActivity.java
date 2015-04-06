package com.dummies.android.silentmodetoggle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import java.util.Calendar;

import static java.util.Calendar.*;

public class MainActivity extends Activity
{
    // Debugging tags
    private static final String DEBUG_TAG = "Calendar Activity";

    //private Context context;

    Button toggleCalView;
    Switch toggleSound;
    //NumberPicker numPicker;
    //CheckBox toggleCalSync;
	private AudioManager mAudioManager;

    // array of Calendar instances within its given cursor
    public static final String[] INSTANCE_PROJECTION = new String[]
    {
        CalendarContract.Instances.EVENT_ID,      // 0
        CalendarContract.Instances.BEGIN,         // 1
        CalendarContract.Instances.TITLE          // 2
    };

	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        initialize();

		setButtonClickListener();

		Log.d("SilentModeApp", "This is a test");
	}

    // Initialize data, cleans up code.
    private void initialize()
    {
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        toggleCalView = (Button) findViewById(R.id.calendarView);

        /**
        * Instantiates toggleSounds and sets the Switch to equal the status of the ringer option
        * I was unable to get the button to react to the sliding option in the switch until I Did this
        */
        toggleSound = (Switch) findViewById(R.id.toggleButton);
        toggleSound.setChecked(checkIfPhoneIsSilent());

        //toggleCalSync = (CheckBox) findViewById(R.id.toggleCalSync);
    }

	private void setButtonClickListener()
    {
        toggleCalView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                viewCalendar();
            }
        });

        /*
        toggleCalSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    checkCalendarValues();
            }
        });
        */

        // Enter in a method for determining when this number picker is changed.

        //toggle silent mode main switch when clicked
		toggleSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                   mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                else
                   mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        });
	}

	/**
	 * Checks to see if the phone is currently in silent mode.
	 */
	private boolean checkIfPhoneIsSilent()
    {
		int ringerMode = mAudioManager.getRingerMode();
        boolean mPhoneIsSilent;

		if (ringerMode == AudioManager.RINGER_MODE_SILENT)
			mPhoneIsSilent = true;
		else
			mPhoneIsSilent = false;

        return mPhoneIsSilent;
	}

    /**
     * Opens the current users calendar for viewing
     */
    private void viewCalendar()
    {
        // A date-time specified in milliseconds since the epoch.
        Calendar beginTime = getInstance();
        long startMillis = beginTime.getTimeInMillis();

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

    private void checkCalendarValues()
    {
        Cursor cursor;

        ContentResolver cr = getContentResolver();

        Calendar beginTime = getInstance();
        //beginTime.set(YEAR, MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE);
        beginTime.set(2015,4,1,1,1);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = getInstance();
        //endTime.set(YEAR, MONTH, DAY_OF_MONTH + 1, HOUR_OF_DAY, MINUTE);
        endTime.set(2015,4,2,1,1);
        long endMillis = endTime.getTimeInMillis();

        // Submit the query
        cursor = CalendarContract.Instances.query(cr, INSTANCE_PROJECTION, startMillis, endMillis);

        while (cursor.moveToNext())
        {
            // Get the field values
            if (cursor.getCount() > 0)
            {
                Long eventID = cursor.getLong(0);
                cursor.getColumnNames();

                Log.i(DEBUG_TAG, "Calendar Values   " + eventID);
                Log.i(DEBUG_TAG, "CalendarBegin:  " + beginTime);
                Log.i(DEBUG_TAG, "CalendarEnd:   " + endTime);
            }
        }
        cursor.close();
    }

	/**
	 * Toggles the UI images from silent to normal and vice versa.
     * Deprecated until further notice
	private void toggleUi()
    {
		ImageView imageView = (ImageView) findViewById(R.id.phone_icon);
		Drawable newPhoneImage;

		if (checkIfPhoneIsSilent())
			newPhoneImage = getResources().getDrawable(R.drawable.phone_silent);
		else
			newPhoneImage = getResources().getDrawable(R.drawable.phone_on);

		imageView.setImageDrawable(newPhoneImage);
	}
    */

	@Override
	protected void onResume()
    {
		super.onResume();

		checkIfPhoneIsSilent();

        checkCalendarValues();

		//toggleUi();
	}
}
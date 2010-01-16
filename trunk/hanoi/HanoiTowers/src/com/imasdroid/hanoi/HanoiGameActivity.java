package com.imasdroid.hanoi;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class HanoiGameActivity extends Activity {

	private static final int DEFAULT_NUMBER_OF_DISKS = 6;

	private HanoiGameView hanoiGame;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialize Hanoi Game
		hanoiGame = new HanoiGameView(this, DEFAULT_NUMBER_OF_DISKS);

		setContentView(hanoiGame);
	}

	/**
	 * Handles the touch screen motion event to detect when user touches down
	 * the screen and pass the control to hanoiGame.onTouch() to perform the
	 * specific action
	 * 
	 * @param event The motion event
	 * @return True if the event was handled, false otherwise.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if (hanoiGame.onTouch(event.getX(), event.getY())) {

				// when game is completed, shows an alert and starts a new game

				Toast.makeText(this, "You win!!", Toast.LENGTH_LONG).show();

				hanoiGame.startGame(DEFAULT_NUMBER_OF_DISKS);
			}
		}

		return true;
	}
}
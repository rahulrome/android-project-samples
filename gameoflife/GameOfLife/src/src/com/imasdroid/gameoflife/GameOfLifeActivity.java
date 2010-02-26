package src.com.imasdroid.gameoflife;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class GameOfLifeActivity extends Activity {
	
	private int rows = 10, columns = 8;
	private int widthFactor, heightFactor;
	
	private GridView grid;
	
	private static final int MENU_NEXT_GENERATION = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// Hide the window title and top bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		widthFactor = metrics.widthPixels / columns;
		heightFactor = metrics.heightPixels / rows;
				
        grid = new GridView(this, rows, columns, heightFactor, widthFactor);
        
		setContentView(grid);
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			int row		= new Float(Math.floor(event.getY() / heightFactor)).intValue();
			int column	= new Float(Math.floor(event.getX() / widthFactor)).intValue();
			
			grid.touchCell(row, column);
		}
		
		return true;
	}
	
	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_NEXT_GENERATION, 0, R.string.menu_next_generation);
	    return true;
	}
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case MENU_NEXT_GENERATION:

        	grid.executeNextGeneration();
        	
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
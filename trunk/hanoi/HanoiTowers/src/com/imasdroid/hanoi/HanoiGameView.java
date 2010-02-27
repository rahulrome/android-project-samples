package com.imasdroid.hanoi;

import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

public class HanoiGameView extends View {

	/** number of disks of the game */
	private int numberOfDisks;

	/**
	 * points to the rod with a disk selected. Used on
	 * {@link #actionOnSelectedRod} to distinguish when a rod contains a disk
	 * selected and ready to move to a different rod
	 */
	private Stack<HanoiDiskShape> rodWithDiskSelected = null;

	private static final int MAX_DISKS = 6;
	private static final String ERROR_MAX_NUMBER_DISKS_EXCEEDED = "Max number of disks is "
			+ MAX_DISKS;

	/** the three rods that form the towers of hanoi game */
	private Stack<HanoiDiskShape> leftRod, middleRod, rightRod;

	////////////////////////////
	// static disk properties //
	////////////////////////////

	// creates a rounded rectangle by the top side
	private static float[] diskOuterRadius = new float[] { 12, 12, 12, 12, 0,
			0, 0, 0 };
	private static int diskSelectedColor = 0x88FF8844;
	private static int diskUnselectedColor = 0xFFFF8844;

	/**
	 * @param context
	 *            requiered for the constructor super View class
	 * @param _numberOfDisks
	 */
	public HanoiGameView(Context context, int _numberOfDisks) {
		super(context);

		// loads the background image that contains the bottom wood and the
		// figure of the three rods
		Bitmap hanoiBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.hanoi_background);
		setBackgroundDrawable(new BitmapDrawable(hanoiBackground));

		startGame(_numberOfDisks);
	}

	/**
	 * Starts the hanoi game with the specified number of disks
	 * 
	 * @param _numberOfDisks
	 */
	protected void startGame(int _numberOfDisks) {

		if (_numberOfDisks > MAX_DISKS) {

			throw new RuntimeException(ERROR_MAX_NUMBER_DISKS_EXCEEDED);
		}

		numberOfDisks = _numberOfDisks;

		// create the three rod objects and fill the left one with
		// the total number of disks

		leftRod = new Stack<HanoiDiskShape>();
		middleRod = new Stack<HanoiDiskShape>();
		rightRod = new Stack<HanoiDiskShape>();

		for (int diskSize = numberOfDisks; diskSize >= 1; diskSize--) {

			leftRod.push(new HanoiDiskShape(diskSize));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// draw the disks of the three rods

		// the idea is to translate the canvas to the left
		// for each rod. inside of the loops, the canvas is translated
		// upward for each disk.

		// some numbers to understand the translations
		// first rod located at: x = 90px
		// first disk located at: y = 230px
		// distance between rods: x = 150px
		// distance between disks: y -= 25px

		canvas.translate(90, 230);
		canvas.save();
		for (HanoiDiskShape disk : leftRod) {

			disk.draw(canvas);
			canvas.translate(0, -25);
		}
		canvas.restore();

		canvas.translate(150, 0);
		canvas.save();
		for (HanoiDiskShape disk : middleRod) {

			disk.draw(canvas);
			canvas.translate(0, -25);
		}
		canvas.restore();

		canvas.translate(150, 0);
		canvas.save();
		for (HanoiDiskShape disk : rightRod) {

			disk.draw(canvas);
			canvas.translate(0, -25);
		}
		canvas.restore();
	}

	/**
	 * Determines where has touched the user (left, middle or right rod) and
	 * perform the required action: select the top disk, unselect the top disk,
	 * move the selected disk, has been the game completed?
	 * 
	 * @param x
	 *            vertical position touched
	 * @param y
	 *            horizontal position touched
	 * @return has been the game completed?
	 */
	public boolean onTouch(float x, float y) {

		boolean gameFinished = false;

		// limits to detect which rod has been touched

		int topLimit = 30;
		int bottomLimit = 250;

		int leftLimitLeftRod = 20;
		int rightLimitLeftRod = leftLimitLeftRod + 150;

		int rightLimitMiddleRod = rightLimitLeftRod + 150;

		int rightLimitRightRod = rightLimitMiddleRod + 150;

		if (y > topLimit && y < bottomLimit) {

			if (x > leftLimitLeftRod && x < rightLimitLeftRod) {

				gameFinished = actionOnTouchedRod(leftRod);
			} else if (x > rightLimitLeftRod && x < rightLimitMiddleRod) {

				gameFinished = actionOnTouchedRod(middleRod);
			} else if (x > rightLimitMiddleRod && x < rightLimitRightRod) {

				gameFinished = actionOnTouchedRod(rightRod);
			}

			// forces to redraw
			invalidate();
		}

		return gameFinished;
	}

	/**
	 * The rod in the argument has been touched and this method determines the
	 * action to perform
	 * 
	 * @param touchedRod
	 * @return
	 */
	private boolean actionOnTouchedRod(Stack<HanoiDiskShape> touchedRod) {

		boolean gameFinished = false;

		// if there isn't any selected rod and touchedRod contains disks...
		if (rodWithDiskSelected == null && touchedRod.size() > 0) {

			touchedRod.lastElement().select();
			rodWithDiskSelected = touchedRod;
		} 
		else if (rodWithDiskSelected != null) {

			// there is a rod with a disk selected, so...

			// if user has touched the same rod -> unselect disk
			if (rodWithDiskSelected.equals(touchedRod)) {

				touchedRod.lastElement().unselect();
				rodWithDiskSelected = null;
				
			// if not, check if it's a valid move 
			} else if (touchedRod.size() == 0
					|| (rodWithDiskSelected.lastElement().size < touchedRod
							.lastElement().size)) {

				rodWithDiskSelected.lastElement().unselect();
				touchedRod.push(rodWithDiskSelected.pop());
				rodWithDiskSelected = null;
			}
		}

		// if all disks are in the middle or right rod, game finished!
		if (middleRod.size() == numberOfDisks || rightRod.size() == numberOfDisks) {

			gameFinished = true;
		}

		return gameFinished;
	}

	private class HanoiDiskShape extends ShapeDrawable {

		private int size;

		public HanoiDiskShape(int _size) {
			super(new RoundRectShape(diskOuterRadius, null, null));

			this.unselect();

			this.size = _size * 25;
			this.setBounds(0, 0, this.size, 20);
		}

		public void select() {

			this.getPaint().setColor(diskSelectedColor);
		}

		public void unselect() {

			this.getPaint().setColor(diskUnselectedColor);
		}

		@Override
		protected void onDraw(Shape shape, Canvas canvas, Paint paint) {

			canvas.save();
			
			// translates the half of the size to the left, to draw
			// the disk on the center of the rod
			canvas.translate(-size / 2, 0);
			shape.draw(canvas, paint);

			canvas.restore();
		}

		@Override
		public String toString() {
			return "HanoiDisk [size=" + size + "]";
		}
	}
}

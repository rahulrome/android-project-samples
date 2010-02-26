package src.com.imasdroid.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.View;

public class GridView extends View {

	private static final boolean DEBUG_MODE = false;
	
	private enum CellStatus {
		LIVE, DEAD
	};

	private CellShape gridCell[][];

	private static int rowsTotal, columnsTotal;
	private int heightFactor, widthFactor;

	public GridView(Context context, int _rows, int _columns, int _heightFactor, int _widthFactor) {
		super(context);

		rowsTotal = _rows;
		columnsTotal = _columns;

		heightFactor = _heightFactor;
		widthFactor = _widthFactor;

		// initialize grid cell
		
		gridCell = new CellShape[rowsTotal][columnsTotal];
		
		for (int row = 0; row < rowsTotal; row++) {
						
			for (int column = 0; column < columnsTotal; column++) {

				gridCell[row][column] = new CellShape(CellStatus.DEAD);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// draw all separator lines
		Paint lineSeparator = new Paint();
		lineSeparator.setColor(Color.WHITE);

		// draw all alive cells
		for (int row = 0; row < rowsTotal; row++) {
			for (int column = 0; column < columnsTotal; column++) {

				CellShape cell = gridCell[row][column];

				if (cell.status == CellStatus.LIVE) {

					canvas.save();
					canvas.translate(column * widthFactor, row * heightFactor);

					cell.draw(canvas);
					
					canvas.restore();					
				}
				
				if (DEBUG_MODE) {
				
					canvas.save();
					canvas.translate(column * widthFactor, row * heightFactor);

					canvas.drawText(new Integer(cell.neighboursNumber).toString(), widthFactor / 2, heightFactor - 10, lineSeparator);
				
					canvas.restore();
				}
				
				// TODO: only paint column separator once
				canvas.drawLine(widthFactor * column, 0, widthFactor * column, heightFactor * rowsTotal, lineSeparator);
			}
			
			canvas.drawLine(0, heightFactor * row, widthFactor * columnsTotal, heightFactor * row, lineSeparator);
		}
	}

	private boolean isCaseIncreasingSameCellLookingForNeighbours(int neighbourRow, int neighbourColumn, int cellRow, int cellColumn) {

		return (neighbourRow == cellRow && neighbourColumn == cellColumn);
	}
		
	private boolean isOutOfGrid(int neighbourRow, int neighbourColumn) {
		
		boolean isOut = true;
		
		if (
				neighbourRow >= 0 && neighbourRow < rowsTotal &&
				neighbourColumn >= 0 && neighbourColumn < columnsTotal
		) {
			isOut = false;
		}	
		
		return isOut;
	}
	
	private void updateNeighbourCounterAndStatusOnGridCell(CellShape gridCell[][], int row, int column, CellStatus newCellStatus) {
		
		// a) update neighbour counter of adyacent cells
		
		CellShape cellNeighbour = null;
		for (int neighbourRow = row - 1; neighbourRow <= row + 1; neighbourRow++) {
			for (int neighbourColumn = column - 1; neighbourColumn <= column + 1; neighbourColumn++) {
				
				if (
						!isCaseIncreasingSameCellLookingForNeighbours(neighbourRow, neighbourColumn, row, column) &&
						!isOutOfGrid(neighbourRow, neighbourColumn)
				) {
					cellNeighbour = gridCell[neighbourRow][neighbourColumn];
					
					cellNeighbour.neighboursNumber = (newCellStatus == CellStatus.LIVE)?cellNeighbour.neighboursNumber+1:cellNeighbour.neighboursNumber-1;
				}
			}
		}
		
		// b) update new cell status
		
		gridCell[row][column].status = newCellStatus;
	}
	
	public void touchCell(int row, int column) {

		if (row >= 0 && row < rowsTotal && column >= 0 && column < columnsTotal) {

			Log.d("GameOfLife", "touched cell at (" + row + "," + column + ")");

			CellShape cell = gridCell[row][column];
			CellStatus newStatus = (cell.status == CellStatus.LIVE)?CellStatus.DEAD:CellStatus.LIVE;
			
			updateNeighbourCounterAndStatusOnGridCell(gridCell, row, column, newStatus);
			
			invalidate();
		}
	}
	
	private static CellShape[][] cloneGrid(CellShape[][] grid) {
		
		CellShape[][] clone = new CellShape[rowsTotal][columnsTotal];
		
		for (int row = 0; row < rowsTotal; row++) {
			
//			clone.add(new ArrayList<CellShape>());

			for (int column = 0; column < columnsTotal; column++) {
		
				try {
					clone[row][column] = (CellShape) grid[row][column].clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}				
		
		return clone;
	}
	
	public void executeNextGeneration() {
		
		boolean hasCellChanged = false;
		CellShape[][] updatedGridCell = cloneGrid(gridCell);
		
		for (int row = 0; row < rowsTotal; row++) {
			for (int column = 0; column < columnsTotal; column++) {

				CellShape cell = gridCell[row][column];
				hasCellChanged = false;

				if (cell.status == CellStatus.LIVE) {
					
					if (
							cell.neighboursNumber < 2 ||
							cell.neighboursNumber > 3
						) {

						cell.status = CellStatus.DEAD;
						hasCellChanged = true;
					}						
				}
				else { // is a dead cell
				
					if (cell.neighboursNumber == 3) {
						
						cell.status = CellStatus.LIVE;
						hasCellChanged = true;
					}
				}
				
				if (hasCellChanged) {
					updateNeighbourCounterAndStatusOnGridCell(updatedGridCell, row, column, cell.status);
				}
			}
		}
		
		gridCell = updatedGridCell;
				
		invalidate();
	}

	private class CellShape extends ShapeDrawable implements Cloneable {

		private static final int CELL_SIZE = 10;

		protected CellStatus status;
		protected int neighboursNumber;

		public CellShape(CellStatus _status) {

			super(new OvalShape());
			getPaint().setColor(Color.RED);
			setBounds(0, 0, CELL_SIZE, CELL_SIZE);

			status = _status;
			neighboursNumber = 0;
		}

		@Override
		protected void onDraw(Shape shape, Canvas canvas, Paint paint) {

			canvas.save();
			canvas.translate(widthFactor / 2, heightFactor / 2);
			
			shape.draw(canvas, paint);
			
			canvas.restore();
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {

			CellShape cellCloned = new CellShape(this.status);
			cellCloned.neighboursNumber = this.neighboursNumber;

			return cellCloned;
		}

		@Override
		public String toString() {
			return "Cell(neighbours=" + neighboursNumber
					+ ",status=" + status + ")";
		}		
	}
}

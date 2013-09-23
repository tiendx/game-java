package tiendx.java.game.snack;

import java.awt.*;
import java.util.*;

/**
 * A Snake is made up of one or more SnakeSegment. The first SnakeSegment is the
 * "head" of the snake. The last SnakeSegment is the "tail" of the snake. As the
 * snake moves, it adds one cell to the head and then removes one from the tail.
 * If the snake eats a piece of food, the head adds one cell but the tail will
 * not shrink.
 */
public class Snake {

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Color color = Color.BLACK; // color for this snake body
	private Color colorHead = Color.GREEN; // color for the "head"
	private Snake.Direction direction; // the current direction of the snake's
										// head

	// The snake segments that forms the snake
	private java.util.List<SnakeSegment> snakeSegments = new ArrayList<SnakeSegment>();

	private boolean dirUpdatePending; // Pending update for a direction change?
	private Random random = new Random(); // for randomly regenerating a snake

	// Regenerate the snake.
	public void regenerate() {
		snakeSegments.clear();
		// Randomly generate a snake inside the pit.
		int length = 20;
		int headX = random.nextInt(GameMain.COLUMNS - length * 2) + length;
		int headY = random.nextInt(GameMain.ROWS - length * 2) + length;
		direction = Snake.Direction.values()[random.nextInt(Snake.Direction
				.values().length)];
		snakeSegments.add(new SnakeSegment(headX, headY, length, direction));
		dirUpdatePending = false;
	}

	// Change the direction of the snake, but no 180 degree turn allowed.
	public void setDirection(Snake.Direction newDir) {
		// Ignore if there is a direction change pending and no 180 degree turn
		if (!dirUpdatePending
				&& (newDir != direction)
				&& ((newDir == Snake.Direction.UP && direction != Snake.Direction.DOWN)
						|| (newDir == Snake.Direction.DOWN && direction != Snake.Direction.UP)
						|| (newDir == Snake.Direction.LEFT && direction != Snake.Direction.RIGHT) || (newDir == Snake.Direction.RIGHT && direction != Snake.Direction.LEFT))) {
			SnakeSegment headSegment = snakeSegments.get(0); // get the head
																// segment
			int x = headSegment.getHeadX();
			int y = headSegment.getHeadY();
			// add a new segment with zero length as the new head segment
			snakeSegments.add(0, new SnakeSegment(x, y, 0, newDir));
			direction = newDir;
			dirUpdatePending = true; // will be cleared after updated
		}
	}

	// Move the snake by one step. The snake "head" segment grows by one cell.
	// The rest of the
	// segments remain unchanged. The "tail" segment will later be shrink if
	// collision detected.
	public void update() {
		SnakeSegment headSegment;
		headSegment = snakeSegments.get(0); // "head" segment
		headSegment.grow();
		dirUpdatePending = false; // can process the key input again
	}

	// Not eaten a food item. Shrink the tail by one cell.
	public void shrink() {
		SnakeSegment tailSegment;
		tailSegment = snakeSegments.get(snakeSegments.size() - 1);
		tailSegment.shrink();
		if (tailSegment.getLength() == 0) {
			snakeSegments.remove(tailSegment);
		}
	}

	// Get the X coordinate of the cell that contains the head of this snake
	// segment.
	public int getHeadX() {
		return snakeSegments.get(0).getHeadX();
	}

	// Get the Y coordinate of the cell that contains the head of this snake
	// segment.
	public int getHeadY() {
		return snakeSegments.get(0).getHeadY();
	}

	// Returns the length of this snake by adding up all the segments.
	public int getLength() {
		int length = 0;
		for (SnakeSegment segment : snakeSegments) {
			length += segment.getLength();
		}
		return length;
	}

	// Returns true if the snake contains the given (x, y) cell. Used in
	// collision detection
	public boolean contains(int x, int y) {
		for (int i = 0; i < snakeSegments.size(); ++i) {
			SnakeSegment segment = snakeSegments.get(i);
			if (segment.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	// Returns true if the snake eats itself
	public boolean eatItself() {
		int headX = getHeadX();
		int headY = getHeadY();
		// eat itself if the (headX, headY) hits its body segment (4th onwards)
		for (int i = 3; i < snakeSegments.size(); ++i) {
			SnakeSegment segment = snakeSegments.get(i);
			if (segment.contains(headX, headY)) {
				return true;
			}
		}
		return false;
	}

	// Draw itself.
	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 0; i < snakeSegments.size(); ++i) {
			snakeSegments.get(i).draw(g); // draw all the segments
		}
		if (snakeSegments.size() > 0) {
			g.setColor(colorHead);
			g.fill3DRect(getHeadX() * GameMain.CELL_SIZE, getHeadY()
					* GameMain.CELL_SIZE, GameMain.CELL_SIZE - 1,
					GameMain.CELL_SIZE - 1, true);
		}
	}

	// For debugging.
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("**Snake** Direction is " + direction + "\n");
		int count = 1;
		for (SnakeSegment segment : snakeSegments) {
			sb.append("  Segment " + count + ": ");
			++count;
			sb.append(segment);
			sb.append('\n');
		}
		return sb.toString();
	}
}
package tiendx.java.game.snack;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JFrame { // main class for the game as a Swing
										// application

	// Define constants for the game
	static final int ROWS = 50; // number of rows (in cells)
	static final int COLUMNS = 50; // number of columns (in cells)
	static final int CELL_SIZE = 12; // Size of a cell (in pixels)
	static final int CANVAS_WIDTH = COLUMNS * CELL_SIZE; // width and height of
															// the game screen
	static final int CANVAS_HEIGHT = ROWS * CELL_SIZE;
	static final int UPDATE_RATE = 3; // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE; // nanoseconds
	private final Color COLOR_PIT = Color.LIGHT_GRAY;

	// Enumeration for the states of the game.
	static enum State {
		INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	}

	static State state; // current state of the game

	// Define instance variables for the game objects
	private Food food;
	private Snake snake;

	// Handle for the custom drawing panel
	private GameCanvas pit;

	// Constructor to initialize the UI components and game objects
	public GameMain() {
		// Initialize the game objects
		gameInit();

		// UI components
		pit = new GameCanvas();
		pit.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		this.setContentPane(pit);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setTitle("SNAKE GAME");
		this.setVisible(true);

		// Start the game.
		gameStart();
	}

	// ------ All the game related codes here ------

	// Initialize all the game objects, run only once in the constructor of the
	// main class.
	public void gameInit() {
		// Allocate a new snake and a food item, do not regenerate.
		snake = new Snake();
		food = new Food();
		state = State.INITIALIZED;
	}

	// Shutdown the game, clean up code that runs only once.
	public void gameShutdown() {
	}

	// To start and re-start the game.
	public void gameStart() {
		// Create a new thread
		Thread gameThread = new Thread() {
			// Override run() to provide the running behavior of this thread.
			@Override
			public void run() {
				gameLoop();
			}
		};
		// Start the thread. start() calls run(), which in turn calls
		// gameLoop().
		gameThread.start();
	}

	// Run the game loop here.
	private void gameLoop() {
		// Regenerate and reset the game objects for a new game
		if (state == State.INITIALIZED || state == State.GAMEOVER) {
			// Generate a new snake and a food item
			snake.regenerate();
			int x, y;
			do {
				food.regenerate();
				x = food.getX();
				y = food.getY();
			} while (snake.contains(x, y)); // regenerate if food placed under
											// the snake
			state = State.PLAYING;
		}

		// Game loop
		long beginTime, timeTaken, timeLeft;
		while (true) {
			beginTime = System.nanoTime();
			if (state == State.GAMEOVER)
				break; // break the loop to finish the current play
			if (state == State.PLAYING) {
				// Update the state and position of all the game objects,
				// detect collisions and provide responses.
				gameUpdate();
			}
			// Refresh the display
			repaint();
			// Delay timer to provide the necessary delay to meet the target
			// rate
			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000; // in milliseconds
			if (timeLeft < 10)
				timeLeft = 10; // set a minimum
			try {
				// Provides the necessary delay and also yields control so that
				// other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) {
			}
		}
	}

	// Update the state and position of all the game objects,
	// detect collisions and provide responses.
	public void gameUpdate() {
		snake.update();
		processCollision();
	}

	// Collision detection and response
	public void processCollision() {
		// check if this snake eats the food item
		int headX = snake.getHeadX();
		int headY = snake.getHeadY();

		if (headX == food.getX() && headY == food.getY()) {
			// food eaten, regenerate one
			int x, y;
			do {
				food.regenerate();
				x = food.getX();
				y = food.getY();
			} while (snake.contains(x, y));
		} else {
			// not eaten, shrink the tail
			snake.shrink();
		}

		// Check if the snake moves out of bounds
		if (!pit.contains(headX, headY)) {
			state = State.GAMEOVER;
			return;
		}

		// Check if the snake eats itself
		if (snake.eatItself()) {
			state = State.GAMEOVER;
			return;
		}
	}

	// Refresh the display. Called back via rapaint(), which invoke the
	// paintComponent().
	private void gameDraw(Graphics g) {
		switch (state) {
		case PLAYING:
			// draw game objects
			snake.draw(g);
			food.draw(g);
			// game info
			g.setFont(new Font("Dialog", Font.PLAIN, 14));
			g.setColor(Color.BLACK);
			g.drawString("Snake: (" + snake.getHeadX() + "," + snake.getHeadY()
					+ ")", 5, 25);
			break;
		case GAMEOVER:
			g.setFont(new Font("Verdana", Font.BOLD, 30));
			g.setColor(Color.RED);
			g.drawString("GAME OVER!", 200, CANVAS_HEIGHT / 2);
			break;
		}
	}

	// Process a key-pressed event. Update the current state.
	public void gameKeyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_UP:
			snake.setDirection(Snake.Direction.UP);
			break;
		case KeyEvent.VK_DOWN:
			snake.setDirection(Snake.Direction.DOWN);
			break;
		case KeyEvent.VK_LEFT:
			snake.setDirection(Snake.Direction.LEFT);
			break;
		case KeyEvent.VK_RIGHT:
			snake.setDirection(Snake.Direction.RIGHT);
			break;
		}
	}

	// Custom drawing panel, written as an inner class.
	class GameCanvas extends JPanel implements KeyListener {
		// Constructor
		public GameCanvas() {
			setFocusable(true); // so that can receive key-events
			requestFocus();
			addKeyListener(this);
		}

		// Override paintComponent to do custom drawing.
		// Called back by repaint().
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background
			setBackground(COLOR_PIT); // may use an image for background

			// Draw the game objects
			gameDraw(g);
		}

		// KeyEvent handlers
		@Override
		public void keyPressed(KeyEvent e) {
			gameKeyPressed(e.getKeyCode());
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		// Check if this pit contains the given (x, y), for collision detection
		public boolean contains(int x, int y) {
			if ((x < 0) || (x >= ROWS)) {
				return false;
			}
			if ((y < 0) || (y >= COLUMNS)) {
				return false;
			}
			return true;
		}
	}

	// main
	public static void main(String[] args) {
		// Use the event dispatch thread to build the UI for thread-safety.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GameMain();
			}
		});
	}
}

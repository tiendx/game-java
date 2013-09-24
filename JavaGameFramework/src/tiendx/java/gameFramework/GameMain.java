package tiendx.java.gameFramework;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JFrame { // main class for the game as a Swing
										// application

	// Define constants for the game
	static final int CANVAS_WIDTH = 800; // width and height of the game screen
	static final int CANVAS_HEIGHT = 600;
	static final int UPDATE_RATE = 4; // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE; // nanoseconds

	// ......

	// Enumeration for the states of the game.
	static enum State {
		INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	}

	static State state; // current state of the game

	// Define instance variables for the game objects
	// ......
	// ......

	// Handle for the custom drawing panel
	private GameCanvas canvas;

	// Constructor to initialize the UI components and game objects
	public GameMain() {
		// Initialize the game objects
		gameInit();

		// UI components
		canvas = new GameCanvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		this.setContentPane(canvas);

		// Other UI components such as button, score board, if any.
		// ......

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setTitle("MY GAME");
		this.setVisible(true);
	}

	// All the game related codes here

	// Initialize all the game objects, run only once in the constructor of the
	// main class.
	public void gameInit() {
		// ......
		state = State.INITIALIZED;
	}

	// Shutdown the game, clean up code that runs only once.
	public void gameShutdown() {
		// ......
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
		// Regenerate the game objects for a new game
		// ......
		state = State.PLAYING;

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
			timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000L; // in
																// milliseconds
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
	public void gameUpdate() {  }

	// Refresh the display. Called back via rapaint(), which invoke the
	// paintComponent().
	private void gameDraw(Graphics2D g2d) {
		switch (state) {
		case INITIALIZED:
			// ......
			break;
		case PLAYING:
			// ......
			break;
		case PAUSED:
			// ......
			break;
		case GAMEOVER:
			// ......
			break;
		}
		// ......
	}

	// Process a key-pressed event. Update the current state.
	public void gameKeyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_UP:
			// ......
			break;
		case KeyEvent.VK_DOWN:
			// ......
			break;
		case KeyEvent.VK_LEFT:
			// ......
			break;
		case KeyEvent.VK_RIGHT:
			// ......
			break;
		}
	}

	// Process a key-released event.
	public void gameKeyReleased(int keyCode) {
	}

	// Process a key-typed event.
	public void gameKeyTyped(char keyChar) {
	}

	// Other methods
	// ......

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
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d); // paint background
			setBackground(Color.BLACK); // may use an image for background

			// Draw the game objects
			gameDraw(g2d);
		}

		// KeyEvent handlers
		@Override
		public void keyPressed(KeyEvent e) {
			gameKeyPressed(e.getKeyCode());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			gameKeyReleased(e.getKeyCode());
		}

		@Override
		public void keyTyped(KeyEvent e) {
			gameKeyTyped(e.getKeyChar());
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
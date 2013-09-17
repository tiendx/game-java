package ballWorld;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

import javax.swing.JPanel;

public class BallWorld extends JPanel{
	private static final int UPDATE_RATE = 30;

	private Ball ball;
	private ContainerBox box;

	private DrawCanvas canvas; // Custom canvas for drawing the box/ball
	private int canvasWidth;
	private int canvasHeight;

	public void gameUpdate() {
	      // Detect collision for this ball with the container box.
	      ball.intersect(box);
	      // Update the ball's state with proper collision response if collided.
	      ball.update();
	}
	public BallWorld(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		// Init the ball at a random location (inside the box) and moveAngle
		Random rand = new Random();
		int radius = 200;
		int x = rand.nextInt(canvasWidth - radius * 2 - 20) + radius + 10;
		int y = rand.nextInt(canvasHeight - radius * 2 - 20) + radius + 10;
		int speed = 5;
		int angleInDegree = rand.nextInt(360);
		ball = new Ball(x, y, radius, speed, angleInDegree, Color.BLUE);

		// Init the Container Box to fill the screen
		box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK,
				Color.WHITE);

		// Init the custom drawing panel for drawing the game
		canvas = new DrawCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);

		// Handling window resize.
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				Dimension dim = c.getSize();
				canvasWidth = dim.width;
				canvasHeight = dim.height;
				// Adjust the bounds of the container to fill the window
				box.set(0, 0, canvasWidth, canvasHeight);
			}
		});

		// Start the ball bouncing
		gameStart();
	}

	public void gameStart() {
		Thread gameThread = new Thread() {
			public void run() {
				while (true) {
					gameUpdate();
					repaint();
					try {
						Thread.sleep(1000 / UPDATE_RATE);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		gameThread.start();
	}

	class DrawCanvas extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // Paint background
			// Draw the box and the ball
			box.draw(g);
			ball.draw(g);
			// Display ball's information
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier New", Font.PLAIN, 12));
			g.drawString("Ball " + ball.toString(), 20, 30);
		}

		@Override
		public Dimension getPreferredSize() {
			return (new Dimension(canvasWidth, canvasHeight));
		}
	}
}

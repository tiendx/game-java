package tiendx.animatingImage.demo;

import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/** Animating image frames. Each frame has its own file */
@SuppressWarnings("serial")
public class AnimatedFramesDemo extends JPanel {
	// Named-constants
	static final int CANVAS_WIDTH = 640;
	static final int CANVAS_HEIGHT = 480;
	public static final String TITLE = "Animated Frame Demo";

	private String[] imgFilenames = { "images/pacman_1.png",
			"images/pacman_2.png", "images/pacman_3.png" };
	private Image[] imgFrames; // array of Images to be animated
	private int currentFrame = 0; // current frame number
	private int frameRate = 5; // frame rate in frames per second
	private int imgWidth, imgHeight; // width and height of the image
	private double x = 100.0, y = 80.0; // (x,y) of the center of image
	private double speed = 8; // displacement in pixels per move
	private double direction = 0; // in degrees
	private double rotationSpeed = 1; // in degrees per move

	// Used to carry out the affine transform on images
	private AffineTransform transform = new AffineTransform();

	/** Constructor to set up the GUI components */
	public AnimatedFramesDemo() {
		// Setup animation
		loadImages(imgFilenames);
		Thread animationThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					update(); // update the position and image
					repaint(); // Refresh the display
					try {
						Thread.sleep(1000 / frameRate); // delay and yield to
														// other threads
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		animationThread.start(); // start the thread to run animation

		// Setup GUI
		setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
	}

	/** Helper method to load all image frames, with the same height and width */
	private void loadImages(String[] imgFileNames) {
		int numFrames = imgFileNames.length;
		imgFrames = new Image[numFrames]; // allocate the array
		URL imgUrl = null;
		for (int i = 0; i < numFrames; ++i) {
			imgUrl = getClass().getClassLoader().getResource(imgFileNames[i]);
			if (imgUrl == null) {
				System.err.println("Couldn't find file: " + imgFileNames[i]);
			} else {
				try {
					imgFrames[i] = ImageIO.read(imgUrl); // load image via URL
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		imgWidth = imgFrames[0].getWidth(null);
		imgHeight = imgFrames[0].getHeight(null);
	}

	/** Update the position based on speed and direction of the sprite */
	public void update() {
		x += speed * Math.cos(Math.toRadians(direction)); // x-position
		if (x >= CANVAS_WIDTH) {
			x -= CANVAS_WIDTH;
		} else if (x < 0) {
			x += CANVAS_WIDTH;
		}
		y += speed * Math.sin(Math.toRadians(direction)); // y-position
		if (y >= CANVAS_HEIGHT) {
			y -= CANVAS_HEIGHT;
		} else if (y < 0) {
			y += CANVAS_HEIGHT;
		}
		direction += rotationSpeed; // update direction based on rotational
									// speed
		if (direction >= 360) {
			direction -= 360;
		} else if (direction < 0) {
			direction += 360;
		}
		++currentFrame; // display next frame
		if (currentFrame >= imgFrames.length) {
			currentFrame = 0;
		}
	}

	/** Custom painting codes on this JPanel */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paint background
		setBackground(Color.WHITE);
		Graphics2D g2d = (Graphics2D) g;

		transform.setToIdentity();
		// The origin is initially set at the top-left corner of the image.
		// Move the center of the image to (x, y).
		transform.translate(x - imgWidth / 2, y - imgHeight / 2);
		// Rotate about the center of the image
		transform
				.rotate(Math.toRadians(direction), imgWidth / 2, imgHeight / 2);
		// Apply the transform to the image and draw
		g2d.drawImage(imgFrames[currentFrame], transform, null);
	}

	/** The Entry main method */
	public static void main(String[] args) {
		// Run the GUI codes on the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(TITLE);
				frame.setContentPane(new AnimatedFramesDemo());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null); // center the application
													// window
				frame.setVisible(true);
			}
		});
	}
}

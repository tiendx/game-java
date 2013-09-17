package ballWorld;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Formatter;

import collisionphysics.CollisionPhysics;
import collisionphysics.CollisionResponse;

public class Ball {
	float x, y, speedX, speedY, radius;
	private Color color;
	private static final Color DEFAULT_COLOR = Color.BLUE;
	CollisionResponse earliestCollisionResponse = new CollisionResponse();
	private CollisionResponse tempResponse = new CollisionResponse();

	public Ball(float x, float y, float radius, float speed,
			float angleInDegree, Color color) {
		this.x = x;
		this.y = y;
		this.speedX = (float) (speed * Math.cos(Math.toRadians(angleInDegree)));
		this.speedY = (float) (speed * Math.sin(Math.toRadians(angleInDegree)));
		;
		this.radius = radius;
		this.color = color;
	}

	public Ball(float x, float y, float radius, float speed, float angleInDegree) {
		this(x, y, radius, speed, angleInDegree, DEFAULT_COLOR);
	}

	public void moveOneStepWithCollisionDetection(ContainerBox box) {
		// get the ball's bounds,offset by the radius of the ball
		float ballMinX = box.minX + radius;
		float ballMinY = box.minY + radius;
		float ballMaxX = box.maxX - radius;
		float ballMaxY = box.maxY - radius;

		// Calculate the ball's new position
		x += speedX;
		y += speedY;
		// Check if the ball moves over the bounds. If so, adjust the position
		// and speed.
		// Check if the ball moves over the bounds. If so, adjust the position
		// and speed.
		if (x < ballMinX) {
			speedX = -speedX; // Reflect along normal
			x = ballMinX; // Re-position the ball at the edge
		} else if (x > ballMaxX) {
			speedX = -speedX;
			x = ballMaxX;
		}
		// May cross both x and y bounds
		if (y < ballMinY) {
			speedY = -speedY;
			y = ballMinY;
		} else if (y > ballMaxY) {
			speedY = -speedY;
			y = ballMaxY;
		}

	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius),
				(int) (2 * radius));
	}

	public float getSpeed() {
		return (float) Math.sqrt(speedX * speedX + speedY * speedY);
	}

	public float getMoveAngle() {
		return (float) Math.toDegrees(Math.atan2(-speedY, speedX));
	}

	public float getMass() {
		return radius * radius * radius / 1000f; // Normalize by a factor
	}

	/** Return the kinetic energy (0.5mv^2) */
	public float getKineticEnergy() {
		return 0.5f * getMass() * (speedX * speedX + speedY * speedY);
	}

	@Override
	/** Describe itself. */
	public String toString() {
		sb.delete(0, sb.length());
		formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%2.0f,%2.0f) "
				+ "S=%4.1f \u0398=%4.0f KE=%3.0f", x, y, radius, speedX,
				speedY, getSpeed(), getMoveAngle(), getKineticEnergy()); // \u0398
																			// is
																			// theta
		return sb.toString();
	}

	// Re-use to build the formatted string for toString()
	private StringBuilder sb = new StringBuilder();
	private Formatter formatter = new Formatter(sb);

	public void intersect(ContainerBox box) {
		// Call movingPointIntersectsRectangleOuter, which returns the
		// earliest collision to one of the 4 borders, if collision detected.
		CollisionPhysics.pointIntersectsRectangleOuter(this.x, this.y,
				this.speedX, this.speedY, this.radius, box.minX, box.minY,
				box.maxX, box.maxY, 1.0f, tempResponse);
		if (tempResponse.t < earliestCollisionResponse.t) {
			earliestCollisionResponse.copy(tempResponse);
		}
	}

	/**
	 * Update the states of this ball for one time-step. Move for one time-step
	 * if no collision occurs; otherwise move up to the earliest detected
	 * collision.
	 */
	public void update() {
		// Check the earliest collision detected for this ball stored in
		// earliestCollisionResponse.
		if (earliestCollisionResponse.t <= 1.0f) { // Collision detected
			// This ball collided, get the new position and speed
			this.x = earliestCollisionResponse.getNewX(this.x, this.speedX);
			this.y = earliestCollisionResponse.getNewY(this.y, this.speedY);
			this.speedX = (float) earliestCollisionResponse.newSpeedX;
			this.speedY = (float) earliestCollisionResponse.newSpeedY;
		} else { // No collision in this coming time-step
			// Make a complete move
			this.x += this.speedX;
			this.y += this.speedY;
		}
		// Clear for the next collision detection
		earliestCollisionResponse.reset();
	}
}

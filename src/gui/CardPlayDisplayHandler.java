package gui;

import java.awt.Container;
import java.awt.Point;
import java.util.Observable;
import java.util.TimerTask;

import java.util.Timer;

/**
 * Animator for displaying network/computer player draws and plays.
 */
public class CardPlayDisplayHandler extends Observable {

	private static final int animationSpeedLimit = 9;
	private static final int[] timerPeriodSpeedMap = new int[] {0, 3, 3, 2, 2, 3, 1, 2, 1, 1};
	private static final int[]   pixalJumpSpeedMap = new int[] {0, 1, 2, 1, 2, 3, 1, 3, 2, 3};

	private static int animationSpeed = 8;	
	private static int timerPeriod;
	private static int pixalJump;
	
	private int xChange = 1;
	private int yChange = 1;
	private int xCount = 1;
	private int yCount = 1;
	private Timer playCardDisplayTimer;
	
	private CardLabel cardLabel;
	private Container display;
	private Point start;
	private Point end;
	
	/**
	 * Creates a new Card Play Display Handler.
	 * @param gui the GUI that owns this display
	 * @param start the point at which the card starts
	 * @param end the point at which the card should stop
	 */
	public CardPlayDisplayHandler(final SpacedOutGUI gui, Point start, Point end) {
		timerPeriod = timerPeriodSpeedMap[animationSpeed];
		pixalJump = pixalJumpSpeedMap[animationSpeed];
		
		display = gui.getContentPane();
		cardLabel = new CardLabel();
		cardLabel.setLocation(start);
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Animates a card moving from a starting point to an ending point as specified in the constructor.
	 */
	public void display() {
		display.setLayout(null);
		display.add(cardLabel, 0);
		playCardDisplayTimer = new Timer();
		playCardDisplayTimer.scheduleAtFixedRate(getPlayCardDisplayTimerTask(), 0, timerPeriod);
	}
	
	private TimerTask getPlayCardDisplayTimerTask() {		
		final Point cardStart = new Point(start);
		final Point cardEnd = new Point(end);
		double xDiff = (double)Math.abs(cardEnd.x - cardStart.x);
		double yDiff = (double)Math.abs(cardEnd.y - cardStart.y);
		double slope = (xDiff == 0.0) ? 0.0 : Math.abs(yDiff / xDiff);
		if (slope == 0.0) 
			xChange = 0;
		else if (Math.abs(slope) < 1.0) 
			xChange = (int)Math.round(1.0 / slope);
		else 
			yChange = (int)Math.round(slope);
		final int cardMovePixalJump = pixalJump;
		final int xPixalJump = (cardStart.x > cardEnd.x) ? -cardMovePixalJump : cardMovePixalJump;
		final int yPixalJump = (cardStart.y > cardEnd.y) ? -cardMovePixalJump : cardMovePixalJump;
		
		return new TimerTask() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void run() {
				if (xChange == 0 && yChange == 0) {
					playCardDisplayTimer.cancel();
					display.remove(cardLabel);
					display.repaint();
					setChanged();
					notifyObservers();
				}
				else if (xCount <= xChange || yChange == 0) {
					cardStart.x += xPixalJump;
					cardLabel.setLocation(cardStart);
					xCount++;
					if (Math.abs(cardStart.x - cardEnd.x) < cardMovePixalJump) {
						xChange = 0;
						cardStart.x = cardEnd.x;
					}
					if (xCount > xChange)
						yCount = 1;
				}
				else if (yCount <= yChange || xChange == 0) {
					cardStart.y += yPixalJump;
					cardLabel.setLocation(cardStart);
					yCount++;
					if (Math.abs(cardStart.y - cardEnd.y) < cardMovePixalJump) {
						yChange = 0;
						cardStart.y = cardEnd.y;
					}
					if (yCount > yChange)
						xCount = 1;
				}
			}
		};
	}
	
	/**
	 * Returns the currently set animation speed (1 - 6) with 1 being the slowest.
	 * @return the currently set animation speed
	 */
	public static int getAnimationSpeed() {
		return animationSpeed;
	}
	
	/**
	 * Increase the animation speed by 1.  Will not increase it past the speed limit.
	 */
	public static void increaseAnimationSpeed() {
		if (animationSpeed < animationSpeedLimit) {
			animationSpeed++;
			timerPeriod = timerPeriodSpeedMap[animationSpeed];
			pixalJump = pixalJumpSpeedMap[animationSpeed];
		}
	}
	
	/**
	 * Decrease the animation speed by 1.  Will not go below 1.
	 */
	public static void decreaseAnimationSpeed() {
		if (animationSpeed > 1) {
			animationSpeed--;			
			timerPeriod = timerPeriodSpeedMap[animationSpeed];
			pixalJump = pixalJumpSpeedMap[animationSpeed];
		}
	}
}

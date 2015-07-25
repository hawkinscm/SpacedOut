package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import log.ErrorWriter;
import model.Card;
import model.CardComparator;
import model.Player;
import model.PlayerType;
import model.GameDeck.PileLocation;

/**
 * Main GUI for Spaced Out games.
 */
public abstract class SpacedOutGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final int JFRAME_BORDER_WIDTH = 5;
	
	private final String configFile = "SpOut.cfg";
	
	private boolean soundEnabled = true;
	
	protected JMenuItem newGameMenuItem;
	protected JMenuItem replacePlayerMenuItem;
	
	protected PlayersDisplayPanel playersDisplayPanel;
	protected JPanel backwardDirectionPanel;
	protected JPanel forwardDirectionPanel;
	protected PlayAreaPanel playAreaPanel;
			
	protected LinkedList<Player> defaultPlayers;
	protected String defaultIP;
	
	protected Player controlledPlayer;
	protected boolean isGameOver;
	
	/**
	 * Creates a new Spaced Out GUI and begins a game.
	 */
	protected SpacedOutGUI() {
		super("Spaced Out");
		isGameOver = true;
		
		// Changes the default icon displayed in the title bar
		setIconImage((new ImageIcon(SpacedOutGUI.class.getResource("images/Spaced_Out_back.JPG"))).getImage());
		
		setLocation(0, 0);
		setSize(800, 685);
		setResizable(false);		
		
		// Cause the application to call a special exit method on close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitProgram();
			}			
		});
				
		defaultPlayers = new LinkedList<Player>();
		defaultIP = null;
		loadConfigFile();
				
		initializeDisplay();		
		setVisible(true);
	}
	
	/**
	 * Initializes and sets up the GUI.
	 */
	private void initializeDisplay() {		
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 5, 10);
		c.gridx = 0;
		c.gridy = 0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		playersDisplayPanel = new PlayersDisplayPanel();
		getContentPane().add(playersDisplayPanel, c);
		c.fill = GridBagConstraints.NONE;
		
		// display for direction of play
		c.gridy++;
		JPanel directionPanel = new JPanel();
		directionPanel.setLayout(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		backwardDirectionPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				int xpoints[] = {12, 0, 12};
			    int ypoints[] = {0, 12, 24};
			    int npoints = 3;
			    g.setColor(Color.BLACK);
			    g.fillPolygon(xpoints, ypoints, npoints);
			}
		};
		backwardDirectionPanel.setMinimumSize(new Dimension(12, 25));
		backwardDirectionPanel.setPreferredSize(backwardDirectionPanel.getMinimumSize());
		directionPanel.add(backwardDirectionPanel, panelc);
		backwardDirectionPanel.setVisible(false);
		
		panelc.gridx++;
		JPanel linePanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				int xpoints[] = {0, 155, 155, 0};
			    int ypoints[] = {11, 11, 15, 15};
			    int npoints = 4;
			    g.setColor(Color.BLACK);
			    g.fillPolygon(xpoints, ypoints, npoints);
			}
		};
		directionPanel.add(linePanel, panelc);
		linePanel.setMinimumSize(new Dimension(155, 25));
		linePanel.setPreferredSize(linePanel.getMinimumSize());
				
		panelc.gridx++;		
		forwardDirectionPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				int xpoints[] = {0, 12, 0};
			    int ypoints[] = {0, 12, 24};
			    int npoints = 3;
			    g.setColor(Color.BLACK);
			    g.fillPolygon(xpoints, ypoints, npoints);
			}
		};
		forwardDirectionPanel.setMinimumSize(new Dimension(12, 25));
		forwardDirectionPanel.setPreferredSize(forwardDirectionPanel.getMinimumSize());
		directionPanel.add(forwardDirectionPanel, panelc);
		forwardDirectionPanel.setVisible(false);
		getContentPane().add(directionPanel, c);
		
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 0, 0);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		playAreaPanel = new PlayAreaPanel(this, new CardComparator(CardComparator.CardTypeSort.COLOR, true));
		getContentPane().add(playAreaPanel, c);
				
		createMenuBar();
	}
	
	/**
	 * Initializes and sets up the main menu bar.
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
			
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		// new game
		newGameMenuItem = new JMenuItem("New Game");
		newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F2, 0));
		
		// replace player
		replacePlayerMenuItem = new JMenuItem("Replace Player");
				
		// view rules
		JMenuItem rulesMenuItem = new JMenuItem("Rules");
		rulesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayRules();
			}
		});
		
		// set options
		JMenuItem optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayOptions();
			}
		});
				
		// quit
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		});
		
		gameMenu.add(newGameMenuItem);
		gameMenu.add(replacePlayerMenuItem);
		gameMenu.addSeparator();
		gameMenu.add(rulesMenuItem);
		gameMenu.add(optionsMenuItem);
		gameMenu.add(quitMenuItem);
		
		// score sheet menu
		JMenuItem scoreSheetMenuItem = new JMenuItem("Score-Sheet");
		scoreSheetMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayScoreSheet();
			}
		});
		scoreSheetMenuItem.setMinimumSize(new Dimension(250, 21));
		scoreSheetMenuItem.setMaximumSize(new Dimension(250, 21));
		scoreSheetMenuItem.setOpaque(false);
				
		// animation speed handlers
		JMenuItem speedTextMenuItem = new JMenuItem("ANIMATION SPEED:");
		speedTextMenuItem.setMinimumSize(new Dimension(115, 15));
		speedTextMenuItem.setMaximumSize(new Dimension(115, 15));
		speedTextMenuItem.setOpaque(false);
				
		final JMenuItem speedValueMenuItem = new JMenuItem("" + CardPlayDisplayHandler.getAnimationSpeed());
		speedValueMenuItem.setMinimumSize(new Dimension(20, 21));
		speedValueMenuItem.setMaximumSize(new Dimension(20, 21));
		speedValueMenuItem.setOpaque(false);
		
		JMenuItem lowerSpeedMenuItem = new JMenuItem("-");
		lowerSpeedMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				CardPlayDisplayHandler.decreaseAnimationSpeed();
				speedValueMenuItem.setText("" + CardPlayDisplayHandler.getAnimationSpeed());
			}
		});
		lowerSpeedMenuItem.setFont(lowerSpeedMenuItem.getFont().deriveFont(14f));
		lowerSpeedMenuItem.setMinimumSize(new Dimension(18, 21));
		lowerSpeedMenuItem.setMaximumSize(new Dimension(18, 21));
		lowerSpeedMenuItem.setOpaque(false);
		
		JMenuItem raiseSpeedMenuItem = new JMenuItem("+");
		raiseSpeedMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				CardPlayDisplayHandler.increaseAnimationSpeed();
				speedValueMenuItem.setText("" + CardPlayDisplayHandler.getAnimationSpeed());
			}
		});
		raiseSpeedMenuItem.setFont(lowerSpeedMenuItem.getFont().deriveFont(14f));
		raiseSpeedMenuItem.setMinimumSize(new Dimension(18, 21));
		raiseSpeedMenuItem.setMaximumSize(new Dimension(18, 21));
		raiseSpeedMenuItem.setOpaque(false);
		// end animation speed handlers
		
		menuBar.add(gameMenu);
		menuBar.add(scoreSheetMenuItem);
		menuBar.add(speedTextMenuItem);
		menuBar.add(lowerSpeedMenuItem);
		menuBar.add(speedValueMenuItem);
		menuBar.add(raiseSpeedMenuItem);
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Plays a sound notifying player that it is his turn.
	 */
	protected void playTurnNotifyClip() {
		if (soundEnabled) {
			try {
				AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource("ching.wav"));
				DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
				Clip turnNoticeClip = (Clip) AudioSystem.getLine(info);
				turnNoticeClip.open(sound);
				turnNoticeClip.addLineListener(new LineListener() {
					public void update(LineEvent event) {
						if (event.getType() == LineEvent.Type.STOP) {
							event.getLine().close();
						}
					}
				});
				turnNoticeClip.start();
			} 
			catch (Exception ex) { System.out.println(ex.getMessage()); }
		}
	}
	
	/**
	 * Returns whether or not the sound is turned on.
	 * @return true if the sound is enabled; false otherwise
	 */
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	/**
	 * Sets whether or not the sound is enabled.
	 * @param enable if true, enable sound; if false, disable sound
	 */
	public void setSoundEnabled(boolean enable) {
		soundEnabled = enable;
	}
	
	/**
	 * Displays and allows the player to change game options.
	 */
	protected abstract void displayOptions();

	/**
	 * Displays the scores for all players, for all rounds and in total.
	 */
	protected abstract void displayScoreSheet();
		
	/**
	 * Allows special exit handling to be performed on exit - prompt the user if they want to end the current game.
	 */
	protected abstract void exitProgram();
	
	/**
	 * Returns the list of players in the current game.
	 * @return
	 */
	protected abstract LinkedList<Player> getPlayers();
	
	/**
	 * Draws cards for the local player if the player is not null and he can draw according to the rules.
	 * @return true if the non-null local player was able to draw cards according to the rules; false otherwise.
	 */
	public abstract boolean drawCards();
	
	/**
	 * Attempts to play the given card on the given play pile for the local player.
	 * @param card the card to play
	 * @param location the location identifying the play pile
	 * @return true if the local player was able to play the card; false otherwise
	 */
	public abstract boolean playCard(Card card, PileLocation location);
	
	/**
	 * Attempts to play the given card by cloning on the given play pile for the local player.
	 * @param card the card to play as a clone
	 * @param location the location identifying the play pile
	 * @return true if the local player was able to play and clone the card; false otherwise
	 */
	public abstract boolean playClone(Card card, PileLocation location);
	
	
	/**
	 * Sets the card comparator used for sorting hand cards
	 * @param comparator the card comparator to set
	 */
	public void setHandCardSortComparator(CardComparator comparator) {
		playAreaPanel.setComparator(comparator);
	}
	
	/**
	 * Animates/displays a player drawing cards.
	 * @param numDrawnCards number of cards the player is drawing
	 */
	public void displayCardDraw(final int numDrawnCards) {
		final Point cardEnd = playersDisplayPanel.getCurrentPlayerDisplayLocation();
		if (cardEnd != null) {
			cardEnd.x += playersDisplayPanel.getLocation().x;
			cardEnd.y += playersDisplayPanel.getLocation().y;
		
			final Point cardStart = playAreaPanel.getPileLocation(null);
			cardStart.x += playAreaPanel.getLocation().x;
			cardStart.y += playAreaPanel.getLocation().y;
		
			final Observer displayObserver = new Observer() {
				int count = 1;
				public void update(Observable arg0, Object arg1) {
					if (count > numDrawnCards)
						return;
				
					CardPlayDisplayHandler displayHandler = new CardPlayDisplayHandler(SpacedOutGUI.this, cardStart, cardEnd);
					displayHandler.addObserver(this);
					displayHandler.display();
					count++;
				}						
			};
			displayObserver.update(null, null);
		}
	}
	
	/**
	 * Updates the play pile display with the given cards, colors, and live pile.
	 * @param leftCard left play pile card to display
	 * @param leftColor left play pile color to display
	 * @param rightCard right play pile card to display
	 * @param rightColor right play pile color to display
	 * @param livePile live pile, specified by location, to display
	 */
	public void updatePlayPileDisplay(Card leftCard, Card.Color leftColor, Card rightCard, Card.Color rightColor, PileLocation livePile) {
		if (leftCard.getColor() == Card.Color.WILD)
			playAreaPanel.setPlayPileWildCard(PileLocation.LEFT, leftCard, leftColor);
		else
			playAreaPanel.setPlayPileCard(PileLocation.LEFT, leftCard);
		
		if (rightCard.getColor() == Card.Color.WILD)
			playAreaPanel.setPlayPileWildCard(PileLocation.RIGHT, rightCard, rightColor);
		else
			playAreaPanel.setPlayPileCard(PileLocation.RIGHT, rightCard);
		
		playAreaPanel.setLivePile(livePile);
	}
	
	public void displayCardPlay(final Card leftCard, final Card.Color leftColor, final Card rightCard, final Card.Color rightColor, final PileLocation livePile) {	
		Point cardStart = playersDisplayPanel.getCurrentPlayerDisplayLocation();
		if (cardStart != null) {
			cardStart.x += playersDisplayPanel.getX();
			cardStart.y += playersDisplayPanel.getY();
			
			Point cardEnd = playAreaPanel.getPileLocation(livePile);
			cardEnd.x += playAreaPanel.getLocation().x;
			cardEnd.y += playAreaPanel.getLocation().y;
			
			Observer displayObserver = new Observer() {
				public void update(Observable arg0, Object arg1) {
					updatePlayPileDisplay(leftCard, leftColor, rightCard, rightColor, livePile);
				}						
			};
			CardPlayDisplayHandler displayHandler = new CardPlayDisplayHandler(this, cardStart, cardEnd);
			displayHandler.addObserver(displayObserver);
			displayHandler.display();					
		}
		else
			updatePlayPileDisplay(leftCard, leftColor, rightCard, rightColor, livePile);
	}
	
	/**
	 * Returns the player who has the given name.
	 * @param playerName name of the player to retrieve
	 * @return the player who has the given name; or null if not found
	 */
	public Player getPlayer(String playerName) {
		for (Player player : getPlayers())
			if (player.getName().equals(playerName))
				return player;
		
		return null;
	}
	
	/**
	 * Sets the ready status for the given player.
	 * @param player the player to change the status of
	 * @param isReady whether or not the player is ready
	 */
	public void setPlayerReadyStatus(Player player, boolean isReady) {
		if (player.isReadyToPlay() != isReady)
			player.setReadyToPlay(isReady);
	}
	
	/**
	 * Displays the rules of the game using the default text file reader.
	 */
	private void displayRules() {
		try {
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().open(new File("rules.txt"));
		}
		catch (IOException ex) {
			Messenger.error(ex.getMessage(), "File Error", this);
		}
	}
		
	/**
	 * Loads the configuration defaults and settings from the config file.
	 */
	private void loadConfigFile() {
		File file = new File(configFile);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(";");
					if (tokens.length == 2) {
						try {
							Point startLocation = getLocation();
							for (int tokenIdx = 0; tokenIdx < tokens.length; tokenIdx++) {
								String[] windowParams = tokens[tokenIdx].split(":");
								if (windowParams.length != 2)
									break;
								
								if (windowParams[0].equalsIgnoreCase("startX"))
									startLocation.x = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startY"))
									startLocation.y = Integer.parseInt(windowParams[1]);
							}							
							setLocation(startLocation);
						}
						catch (NumberFormatException ex) {}
						
						continue;
					}
					
					if (line.startsWith(":Sort:=")) {
						try {
							line = line.substring(line.indexOf('=') + 1);
							tokens = line.split(":");
							CardComparator.CardTypeSort typeSort = CardComparator.CardTypeSort.valueOf(tokens[0]);
							boolean isAscendingSort = Boolean.getBoolean(tokens[1]);
							playAreaPanel.setComparator(new CardComparator(typeSort, isAscendingSort));
						}
						catch (Exception ex) {}
						
						continue;
					}
					else if (line.startsWith(":HostIP:=")) {
						try {
							defaultIP = line.substring(line.indexOf('=') + 1);
						}
						catch (Exception ex) {}
						
						continue;
					}
					else if (line.startsWith(":PlaySound:=")) {
						try {
							soundEnabled = Boolean.parseBoolean(line.substring(line.indexOf('=') + 1));
						}
						catch (Exception ex) {}
					}
					else if (line.startsWith(":PlayHelps:=")) {
						try {
							PlayAreaPanel.setPlayHelpsEnabled(Boolean.parseBoolean(line.substring(line.indexOf('=') + 1)));
						}
						catch (Exception ex) {}
					}
					
					tokens = line.split(":");
					if (tokens.length < 2)
						continue;
					
					PlayerType type = PlayerType.parseType(tokens[1]);
					if (type == null)
						continue;
					
					defaultPlayers.add(new Player(tokens[0], type));
				}
				reader.close();
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * Writes the latest configuration defaults and settings to the config file.
	 */
	protected void updateConfigFile() {
		File file = new File(configFile);
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("startX:" + getLocation().x + ";");
			writer.write("startY:" + getLocation().y);
			writer.newLine();
			
			writer.write(":Sort:=");
			writer.write(playAreaPanel.getComparator().getCardTypeSort().toString() + ":");
			writer.write(Boolean.toString(playAreaPanel.getComparator().isInAscendingOrder()));
			writer.newLine();
			
			if (defaultIP != null) {
				writer.write(":HostIP:=");
				writer.write(defaultIP);
				writer.newLine();
			}
			
			writer.write(":PlaySound:=");
			writer.write(String.valueOf(soundEnabled));
			writer.newLine();
			
			writer.write(":PlayHelps:=");
			writer.write(String.valueOf(PlayAreaPanel.arePlayHelpsEnabled()));
			writer.newLine();
			
			writer.newLine();
						
			for (Player player : defaultPlayers) {
				writer.write(player.getName().replace(':', ' ') + ":" + player.getPlayerType());
				writer.newLine();
			}
			writer.close();
		} catch (IOException ex) {}
	}
		
	/**
	 * Main class that is first called and starts running the GUI and thereby the program.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			ErrorWriter.beginLogging("SpOut.log");
		}
		catch (IOException ex) {
			Messenger.error(ex, "Failed to create/open error log file", "Log Error");
		}
		
		// Run the GUI in a thread safe environment
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					String prompt = "Would you like to host or join a Spaced Out game?";
					ImageIcon icon = new ImageIcon(SpacedOutGUI.class.getResource("images/Spaced_Out_back.JPG"));
					ImageIcon resizedIcon = new ImageIcon(icon.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH));
					String[] options = {"Host", "Join", "Exit Program"};
					int result = JOptionPane.showOptionDialog(null, prompt, "Spaced Out  v1.2", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, resizedIcon, options, "Host");
					if (result == JOptionPane.YES_OPTION)
						new HostGUI();
					else if (result == JOptionPane.NO_OPTION)
						new ParticipantGUI();
				}
			}
		);
	}
}

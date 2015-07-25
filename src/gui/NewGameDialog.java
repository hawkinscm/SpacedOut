package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import action.DisplayPlayMessageAction;

import socket.PlayerSocket;

import model.Player;
import model.PlayerType;

/**
 * Dialog used by a host for choosing players and setting options for a new game.
 */
public class NewGameDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	private final int MAX_NUM_DECKS = 4;
	private final String WAIT_MESSAGE = "Waiting...";
	
	// Arrays of Drop Down lists for each player for type, name
	private JComboBox[] typeComboBoxes;
	private JTextField[] nameFields;
	private JLabel[] portLabels;
	
	// Gives a selection of how many decks to use for the game
	private JComboBox decksToUseComboBox;
	
	private LinkedList<Player> newPlayers;
	private PlayerSocket[] playerSockets;
	private int numDecksToUse = 0;
	
	/**
	 * Creates a new CreatePlayersDialog Dialog.
	 * @param parent frame that contains/owns this dialog
	 * @param numPlayers the number of players who will be in the new game
	 * @param connectedPlayerSockets the currently connected player sockets
	 */
	public NewGameDialog(final HostGUI owner, final int numPlayers, final LinkedList<Player> defaultPlayers, final LinkedList<PlayerSocket> connectedPlayerSockets) {
		super(owner, "New Game");
		
		newPlayers = null;
		playerSockets = new PlayerSocket[numPlayers];
		
		c.insets = new Insets(7, 7, 7, 7);
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		JPanel optionPanel = new JPanel();
		optionPanel.add(new JLabel("Number of Spaced Out decks to play with: "));
		int deckArraySize = MAX_NUM_DECKS;
		if (numPlayers > 6)
			deckArraySize--;
		Integer[] numDecksArray = new Integer[deckArraySize];
		for (int idx = 0; idx < deckArraySize; idx++)
			numDecksArray[idx] = idx + 1 + (MAX_NUM_DECKS - deckArraySize);
		decksToUseComboBox = new JComboBox(numDecksArray);
		decksToUseComboBox.setSelectedIndex(1);
		optionPanel.add(decksToUseComboBox);
		getContentPane().add(optionPanel, c);
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 1;
		
		c.gridy++;
		c.insets.bottom = 0;
		getContentPane().add(new JLabel("Local Host IP:"), c);
		c.insets.bottom = 7;
		c.insets.top = 0;
		c.gridy++;
		JTextField ipTextField = new JTextField(getHostIP()); 
		ipTextField.setBorder(null); 
		ipTextField.setOpaque(false); 
		ipTextField.setEditable(false); 
		getContentPane().add(ipTextField, c);
		
		// initialize variable and display GUI controls
		c.gridx++;
		c.anchor = GridBagConstraints.NORTH;
		getContentPane().add(new JLabel("NAME"), c);
		
		c.gridx++;
		getContentPane().add(new JLabel("PORT"), c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.insets.top = 7;
		
		typeComboBoxes = new JComboBox[numPlayers];
		nameFields = new JTextField[numPlayers];
		portLabels = new JLabel[numPlayers];
		
		// Host player
		c.gridx = 0;
		c.gridy++;
		getContentPane().add(new JLabel("HOST"), c);		
		c.gridx++;		
		nameFields[0] = new JTextField(10);
		nameFields[0].setDocument(new PlainDocument() {
			private static final long serialVersionUID = 1L;

			@Override
			public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
				if (offset < 10)
					super.insertString(offset, s, attributeSet);
			}
		});
		nameFields[0].setText((defaultPlayers.isEmpty()) ? "player1" : defaultPlayers.getFirst().getName());
		nameFields[0].addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				nameFields[0].selectAll();				
			}
			public void focusLost(FocusEvent arg0) {}
		});
		getContentPane().add(nameFields[0], c);
		
		for(int playerIndex = 1; playerIndex < numPlayers; playerIndex++) {
			final int currentIndex = playerIndex;
			
			c.gridx = 0;
			c.gridy++;
						
			// Allow selecting of type: human or computer level (default is human)
			typeComboBoxes[playerIndex] = new JComboBox(PlayerType.values());
			typeComboBoxes[playerIndex].removeItem(PlayerType.HOST);
			final int currentPlayerIndex = playerIndex;
			typeComboBoxes[playerIndex].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if ((PlayerType)typeComboBoxes[currentPlayerIndex].getSelectedItem() == PlayerType.NETWORK) {
						if (playerSockets[currentPlayerIndex] != null)
							playerSockets[currentPlayerIndex].close();
						
						if (!connectedPlayerSockets.isEmpty()) {
							Iterator<PlayerSocket> playerSocketIter = connectedPlayerSockets.iterator();
							while (playerSocketIter.hasNext()) {
								PlayerSocket connectedPlayerSocket = playerSocketIter.next();
								if (connectedPlayerSocket.getPortIndex() == currentPlayerIndex) {
									playerSocketIter.remove();
									nameFields[currentPlayerIndex].setText(connectedPlayerSocket.getPlayer().getName());
									nameFields[currentPlayerIndex].setEnabled(false);
									playerSockets[currentPlayerIndex] = connectedPlayerSocket;
									portLabels[currentPlayerIndex].setText("" + connectedPlayerSocket.getPortIndex());  
									return;
								}
							}
						}
						
						nameFields[currentPlayerIndex].setText(WAIT_MESSAGE);
						nameFields[currentPlayerIndex].setEnabled(false);
						playerSockets[currentPlayerIndex] = new PlayerSocket(currentPlayerIndex);
						if (playerSockets[currentPlayerIndex].getPortIndex() < 0) {
							typeComboBoxes[currentPlayerIndex].setSelectedItem(PlayerType.COMPUTER_MEDIUM);
							nameFields[currentPlayerIndex].setEnabled(true);
							if (currentPlayerIndex < defaultPlayers.size())
								nameFields[currentPlayerIndex].setText(defaultPlayers.get(currentPlayerIndex).getName());
							else
								nameFields[currentPlayerIndex].setText("Player" + (currentPlayerIndex + 1));
							return;
						}
						portLabels[currentPlayerIndex].setText("" + playerSockets[currentPlayerIndex].getPortIndex());
						
						Thread connectThread = new Thread() {
							@Override
							public void run() {
								String playerName = playerSockets[currentPlayerIndex].connect();
								if (playerName == null)
									return;
								nameFields[currentPlayerIndex].setText(playerName);
								String playerMessage = "<html><center> Connected players: " + nameFields[0].getText();
								for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
									PlayerSocket playerSocket = playerSockets[playerIndex];
									if (playerSocket != null && playerSocket.isConnected())
										playerMessage += ", " + nameFields[playerIndex].getText();
								}
								playerMessage += " <br> Waiting on host to begin the game... </center></html>";
								
								String actionMessage = new DisplayPlayMessageAction().createMessage(playerMessage);
								for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
									PlayerSocket playerSocket = playerSockets[playerIndex];
									if (playerSocket != null && playerSocket.isConnected())
										playerSocket.sendActionMessage(actionMessage);
								}
							}
						};
						connectThread.start();
					}
					else {
						if (nameFields[currentPlayerIndex] != null) {
							nameFields[currentPlayerIndex].setEnabled(true);
							if (nameFields[currentPlayerIndex].getText().equals(WAIT_MESSAGE)) {
								if (currentPlayerIndex < defaultPlayers.size())
									nameFields[currentPlayerIndex].setText(defaultPlayers.get(currentPlayerIndex).getName());
								else
									nameFields[currentPlayerIndex].setText("Player" + (currentPlayerIndex + 1));
							}
							if (portLabels[currentPlayerIndex] != null)
								portLabels[currentPlayerIndex].setText("-");
							if (playerSockets[currentPlayerIndex] != null)
								playerSockets[currentPlayerIndex].close();
						}
					}
				}				
			});
			getContentPane().add(typeComboBoxes[playerIndex], c);
			
			c.gridx++;
			// Allow entering of name			
			nameFields[playerIndex] = new JTextField(10);
			// MAX of 10 characters per name
			nameFields[playerIndex].setDocument(new PlainDocument() {
				private static final long serialVersionUID = 1L;

				@Override
				public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
					if (offset < 10)
						super.insertString(offset, s, attributeSet);
				}
			});
			nameFields[playerIndex].addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) {
					nameFields[currentIndex].selectAll();				
				}
				public void focusLost(FocusEvent arg0) {}
			});
			getContentPane().add(nameFields[playerIndex], c);
			
			c.gridx++;
			portLabels[playerIndex] = new JLabel("-");
			getContentPane().add(portLabels[playerIndex], c);
						
			String playerName = "Player" + (playerIndex + 1);
			if (playerIndex < defaultPlayers.size()) {
				Player defaultPlayer = defaultPlayers.get(playerIndex);
				if (!defaultPlayer.isComputer())
					typeComboBoxes[playerIndex].setSelectedItem(PlayerType.COMPUTER_MEDIUM);
				if (defaultPlayer.isComputer() || (defaultPlayer.getPlayerType() == PlayerType.NETWORK && !connectedPlayerSockets.isEmpty()))
					typeComboBoxes[playerIndex].setSelectedItem(defaultPlayer.getPlayerType());
				playerName = defaultPlayer.getName();
			}
			else
				typeComboBoxes[playerIndex].setSelectedItem(PlayerType.COMPUTER_MEDIUM);
			nameFields[playerIndex].setText(playerName);
		}
		
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.insets = new Insets(10, 15, 0 ,15);
		// Button that signals the completion of player creation input and uses the input to create the players.
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				newPlayers = new LinkedList<Player>();
				for(int playerIndex = 0; playerIndex < nameFields.length; playerIndex++) {
					String name = nameFields[playerIndex].getText().replaceAll("[^\\w ]", "").trim();
					if (name.equals(""))
						name = "Player" + (playerIndex + 1);					
					PlayerType playerType = (playerIndex == 0) ? PlayerType.HOST : (PlayerType)typeComboBoxes[playerIndex].getSelectedItem();	
					Player player;
					PlayerSocket playerSocket = playerSockets[playerIndex];
					if (playerSocket != null && playerSocket.getPlayer() != null) {
						player = playerSocket.getPlayer();
						player.discardAll();
						player.setReadyToPlay(true);
						player.setReadyToPlay(false);
					}
					else
						player = new Player(name, playerType);
					
					for (Player currentPlayer : newPlayers) {
						if (name.equals(currentPlayer.getName())) {
							String message = "You cannot have two players with the same name: \"" + player + "\"";
							Messenger.error(message, "Duplicate Name Error", owner);
							newPlayers = null;
							return;
						}
						else if (nameFields[playerIndex].getText().equals(WAIT_MESSAGE)) {
							String message = "Still waiting for network participants to connect.";
							Messenger.error(message, "Waiting For Network Players", owner);
							newPlayers = null;
							return;
						}
					}
							
					newPlayers.add(player);
				}
				
				numDecksToUse = (Integer)decksToUseComboBox.getSelectedItem();
				
				dispose();
			}
		};
		buttonPanel.add(okButton, panelc);
		
		panelc.gridx++;
		// Button that will open the Options Display
		CustomButton optionsButton = new CustomButton("Options...") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				new OptionsDialog(owner, 0).setVisible(true);
			}
		};
		buttonPanel.add(optionsButton, panelc);
				
		panelc.gridx++;
		// Button that will cancel this dialog and not create any new players
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			
			public void buttonPressed() {
				for (PlayerSocket playerSocket : playerSockets)
					if (playerSocket != null && !playerSocket.isConnected())
						playerSocket.close();
				
				dispose();
			}
		};
		buttonPanel.add(cancelButton, panelc);
		getContentPane().add(buttonPanel, c);
		
		refresh();
		okButton.requestFocus();
	}
	
	private String getHostIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} 
		catch (UnknownHostException e) {}
		return "NETWORK NOT FOUND";
	}
	
	/**
	 * Returns the players created by this dialog.
	 * @return the players for the game
	 */
	public LinkedList<Player> getPlayers() {	
		return newPlayers;
	}
	
	/**
	 * Returns a list of all connected network player sockets.
	 * @return a list of all connected network player sockets
	 */
	public LinkedList<PlayerSocket> getPlayerSockets() {
		LinkedList<PlayerSocket> playerSocketList = new LinkedList<PlayerSocket>();
		for (int playerIndex = 0; playerIndex < newPlayers.size(); playerIndex++) {
			Player player = newPlayers.get(playerIndex);
			if (player.getPlayerType() == PlayerType.NETWORK) {
				if (playerSockets[playerIndex].getPlayer() == null)
					playerSockets[playerIndex].setPlayer(player);
				playerSocketList.add(playerSockets[playerIndex]);
			}
		}
		return playerSocketList;
	}
	
	/**
	 * Returns the number of Spaced Out decks to use in this game.
	 * @return the number of Spaced Out decks to use in this game
	 */
	public int getNumDecksToUse() {
		return numDecksToUse;
	}
}

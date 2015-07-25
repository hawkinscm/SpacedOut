package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import model.Card;
import model.CardComparator;
import model.CardComparator.CardTypeSort;
import model.GM;
import model.SortedLinkedList;
import model.SpacedOutDeckFactory;

/**
 * Dialog for allowing player to see and set game options.
 */
public class OptionsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private final int CARD_SPACING = 20;
	
	private JRadioButton ascendingOption;
	private JRadioButton descendingOption;
	private JRadioButton colorSortOption;
	private JRadioButton designSortOption;
	private JRadioButton pointValueSortOption;
	private JPanel sortExamplePanel;
	private JTextField endScoreTextField;
	private JTextField numDealCardsTextField;
	
	private CardComparator comparator;
	private SortedLinkedList<Card> sortedExampleCards;
	
	private int endGameScore;
	private int numDealCards;
	private int highestScore;
	
	/**
	 * Creates a new Options Dialog
	 * @param gui the ParticipantGUI that created/owns this dialog
	 * @param endGameScore the score that when reached or passed will end the game
	 * @param numDealCards the number of cards that will be dealt at the beginning of each round
	 */
	public OptionsDialog(ParticipantGUI gui, int endGameScore, int numDealCards) {
		super(gui, "Game Options");
		
		this.endGameScore = endGameScore;
		this.numDealCards = numDealCards;
		highestScore = 0;
		
		initialize(gui);
		
		endScoreTextField.setEnabled(false);
		numDealCardsTextField.setEnabled(false);
	}
	
	/**
	 * Creates a new Options Dialog with the bound that the end game score cannot be or be lower than.
	 * @param gui the HostGUI that created/owns this dialog
	 * @param highestScore the highest score that any player has; this will be the bound that the end game score cannot fall below
	 */
	public OptionsDialog(HostGUI gui, int highestScore) {
		super(gui, "Game Options");
		
		endGameScore = GM.getEndGameScore();
		numDealCards = GM.getDealNumber();
		this.highestScore = highestScore;
		
		initialize(gui);
	}
	
	/**
	 * Initializes the Options Dialog with current or default settings.
	 * @param gui the GUI that created/owns this dialog
	 */
	private void initialize(final SpacedOutGUI gui) {
		comparator = new CardComparator(CardTypeSort.COLOR, true);
		sortedExampleCards = new SortedLinkedList<Card>(comparator);
		for (Card card : SpacedOutDeckFactory.buildDeck()) {
			if (card.getDesign() == Card.Design.ASTEROIDS && (card.getColor() == Card.Color.BLUE ||
															  card.getColor() == Card.Color.GREEN) ||
				card.getDesign() == Card.Design.BIG_BANG && card.getColor() == Card.Color.BLUE_AND_RED ||
				card.getDesign() == Card.Design.BLACK_HOLE && (card.getColor() == Card.Color.BLUE ||
															   card.getColor() == Card.Color.RED ||
															   card.getColor() == Card.Color.WILD) ||
				card.getDesign() == Card.Design.FORCE_FIELD && (card.getColor() == Card.Color.BLUE ||
																card.getColor() == Card.Color.YELLOW ||
																card.getColor() == Card.Color.WILD) ||
				card.getDesign() == Card.Design.NUMBER && (card.getPointValue() == 2 ||
														   card.getPointValue() == 7) ||
				card.getDesign() == Card.Design.SHOOTING_STAR && (card.getColor() == Card.Color.BLUE ||
															   card.getColor() == Card.Color.RED ||
															   card.getColor() == Card.Color.WILD)) {
				if (!sortedExampleCards.contains(card))
					sortedExampleCards.add(card);
			}
		}
		
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridwidth = 2;
		getContentPane().add(new JLabel("NOTE: Raising the end game score can extend the game if it has already ended."), c);
				
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy++;
		getContentPane().add(new JLabel("End game score: "), c);
		c.gridwidth = 1;
		
		c.gridx++;
		endScoreTextField = new JTextField("" + endGameScore, 3);
		endScoreTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}

			public void focusLost(FocusEvent arg0) {
				try {
					int endScore = Integer.parseInt(endScoreTextField.getText());
					if (endScore <= highestScore)
						throw new NumberFormatException();
					
					endGameScore = endScore;
				}
				catch (Exception ex) {
					endScoreTextField.setText("" + endGameScore);
				}
			}			
		});
		getContentPane().add(endScoreTextField, c);		
		
		c.gridx = 0;
		c.gridy++;
		c.insets.top = 0;
		getContentPane().add(new JLabel("Number of cards to deal a player each round (5-20): "), c);
		
		c.gridx ++;
		numDealCardsTextField = new JTextField("" + numDealCards, 2);
		numDealCardsTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}

			public void focusLost(FocusEvent arg0) {
				try {
					int dealNumber = Integer.parseInt(numDealCardsTextField.getText());
					if (dealNumber < 5 || dealNumber > 20)
						throw new NumberFormatException();
					
					numDealCards = dealNumber;
				}
				catch (Exception ex) {
					numDealCardsTextField.setText("" + numDealCards);
				}
			}			
		});
		getContentPane().add(numDealCardsTextField, c);
				
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.insets.top = 10;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		final JPanel handCardSortPanel = new JPanel();
		handCardSortPanel.setBorder(BorderFactory.createTitledBorder("Hand Card Sort Options"));
		handCardSortPanel.setLayout(new GridBagLayout());
		GridBagConstraints panelc = new GridBagConstraints();
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.anchor = GridBagConstraints.NORTH;
		panelc.ipadx = 50;
		panelc.insets = new Insets(5, 10, 5, 10);
		
		JPanel sortByPanel = new JPanel();
		sortByPanel.setBorder(BorderFactory.createTitledBorder("Sort By"));
		GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(1);
		gridLayout.setRows(0);
		gridLayout.setHgap(5);
		gridLayout.setVgap(5);
		sortByPanel.setLayout(gridLayout);
		ButtonGroup numberCardSortGroup = new ButtonGroup();
		colorSortOption = new JRadioButton("color");
		colorSortOption.setSelected(true);
		colorSortOption.setFocusable(false);
		colorSortOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comparator = new CardComparator(CardComparator.CardTypeSort.COLOR, comparator.isInAscendingOrder());
				updateSortExample();
			}
		});
		designSortOption = new JRadioButton("card design");
		designSortOption.setFocusable(false);
		designSortOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comparator = new CardComparator(CardComparator.CardTypeSort.DESIGN, comparator.isInAscendingOrder());
				updateSortExample();
			}
		});
		pointValueSortOption = new JRadioButton("point value");
		pointValueSortOption.setFocusable(false);
		pointValueSortOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comparator = new CardComparator(CardComparator.CardTypeSort.POINT_VALUE, comparator.isInAscendingOrder());
				updateSortExample();
			}
		});
		numberCardSortGroup.add(colorSortOption);
		numberCardSortGroup.add(designSortOption);
		numberCardSortGroup.add(pointValueSortOption);
		sortByPanel.add(colorSortOption);
		sortByPanel.add(designSortOption);
		sortByPanel.add(pointValueSortOption);
		handCardSortPanel.add(sortByPanel, panelc);
		
		panelc.gridx++;
		JPanel pointValueSortPanel = new JPanel();
		pointValueSortPanel.setBorder(BorderFactory.createTitledBorder("Point Value Sort"));
		pointValueSortPanel.setLayout(gridLayout);
		ButtonGroup pointValueSortGroup = new ButtonGroup();
		ascendingOption = new JRadioButton("lowest to highest point value");
		ascendingOption.setSelected(true);
		ascendingOption.setFocusable(false);
		ascendingOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comparator = new CardComparator(comparator.getCardTypeSort(), true);
				updateSortExample();
			}
		});
		descendingOption = new JRadioButton("highest to lowest point value");
		descendingOption.setFocusable(false);
		descendingOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comparator = new CardComparator(comparator.getCardTypeSort(), false);
				updateSortExample();
			}
		});
		pointValueSortGroup.add(ascendingOption);
		pointValueSortGroup.add(descendingOption);
		pointValueSortPanel.add(ascendingOption);
		pointValueSortPanel.add(descendingOption);
		handCardSortPanel.add(pointValueSortPanel, panelc);
		
		getContentPane().add(handCardSortPanel, c);		
						
		c.gridy++;
		sortExamplePanel = new JPanel();
		sortExamplePanel.setBorder(BorderFactory.createTitledBorder("Selected Sort Example"));
		sortExamplePanel.setLayout(null);
		sortExamplePanel.setPreferredSize(new Dimension(sortedExampleCards.size() * CARD_SPACING + CardLabel.CARD_WIDTH, CardLabel.CARD_HEIGHT + 25));
		updateSortExample();
		getContentPane().add(sortExamplePanel, c);
		
		c.gridwidth = 2;		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy++;
		c.insets.top = 0;
		JPanel bottomPanel = new JPanel(new GridBagLayout());
		panelc = new GridBagConstraints();
		panelc.anchor = GridBagConstraints.WEST;
		panelc.gridx = 0;
		panelc.gridy = 0;
		panelc.weightx = 1.0;
		
		JPanel checkBoxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints checkPanelc = new GridBagConstraints();
		checkPanelc.gridx = 0;
		checkPanelc.gridy = 0;
		checkPanelc.anchor = GridBagConstraints.WEST;
		final JCheckBox playHelpsCheckBox = new JCheckBox("Display Play Helps", PlayAreaPanel.arePlayHelpsEnabled());
		checkBoxPanel.add(playHelpsCheckBox, checkPanelc);
		checkPanelc.gridy++;
		final JCheckBox soundCheckBox = new JCheckBox("Play Sounds", gui.isSoundEnabled());
		checkBoxPanel.add(soundCheckBox, checkPanelc);
		bottomPanel.add(checkBoxPanel, panelc);	
		
		panelc.weightx = 0;
		panelc.anchor = GridBagConstraints.SOUTHEAST;
		panelc.gridx++;
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				GM.setEndGameScore(endGameScore);
				GM.setDealNumber(numDealCards);
					
				gui.setHandCardSortComparator(comparator);
				gui.setSoundEnabled(soundCheckBox.isSelected());
				PlayAreaPanel.setPlayHelpsEnabled(playHelpsCheckBox.isSelected());
				
				dispose();
			}
		};
		bottomPanel.add(okButton, panelc);
		
		panelc.gridx++;
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;

			public void buttonPressed() {
				dispose();
			}			
		};
		bottomPanel.add(cancelButton, panelc);
		
		getContentPane().add(bottomPanel, c);
		
		refresh();
	}
	
	/**
	 * Updates and displays the example for the user selected hand card sort.
	 */
	private void updateSortExample() {
		sortedExampleCards.setComparator(comparator);
		sortExamplePanel.removeAll();
				
		int startX = 10;
		for (Card card : sortedExampleCards) {
			CardLabel cardLabel = new CardLabel(card);
			sortExamplePanel.add(cardLabel, 0);
			cardLabel.setLocation(startX, 15);
			startX += CARD_SPACING;
		}
		validate();
		repaint();
	}
}

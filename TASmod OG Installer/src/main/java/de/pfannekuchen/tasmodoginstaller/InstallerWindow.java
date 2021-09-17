package de.pfannekuchen.tasmodoginstaller;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class InstallerWindow extends JFrame {

	private static final long serialVersionUID = 5155442058664945274L;
	public JButton launchButton;
	public JTextPane infobox;
	
	private String string1 = "TASmod OG Full is the full package to create and playback TASes.\nIt contains all tools needed to efficiently create a TAS.";
	private String string2 = "The Early Access Channel contains stable builds that require\n testing before they can be released.";
	public JRadioButton earlyAccessRadioBox;
	public JRadioButton releaseRadioBox;
	public JRadioButton lightRadioBox;
	public JRadioButton fullRadioBox;
	
	@Override
	public void paint(Graphics g) {
		super.paintComponents(g);
	}
	
	public InstallerWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		JPanel contentPane = new JPanel() {
			private static final long serialVersionUID = 6120882840307218106L;
			@Override
			protected void paintComponent(Graphics g) {
				int heightPerStripe = getHeight() / 4;
				g.setColor(new Color(0, 48, 54));
				g.fillRect(0, heightPerStripe * 0, getWidth(), heightPerStripe * 1);
				g.setColor(new Color(2, 90, 95));
				g.fillRect(0, heightPerStripe * 1, getWidth(), heightPerStripe * 2);
				g.setColor(new Color(3, 131, 135));
				g.fillRect(0, heightPerStripe * 2, getWidth(), heightPerStripe * 3);
				g.setColor(new Color(39, 182, 188));
				g.fillRect(0, heightPerStripe * 3, getWidth(), heightPerStripe * 4);
			}
		};
		contentPane.setBounds(0, 0, getWidth(), getHeight());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setOpaque(false);
		contentPane.setBackground(new Color(0, 0, 0, 0));
		
		launchButton = new JButton("Launch");
		launchButton.setBounds(10, 327, 364, 23);
		launchButton.setBackground(new Color(0, 0, 0, 0));
		launchButton.setOpaque(false);
		contentPane.add(launchButton);
		
		infobox = new JTextPane();
		infobox.setBounds(10, 45, 364, 76);
		infobox.setEditable(false);
		infobox.setOpaque(false);
		infobox.setForeground(Color.CYAN);
		infobox.setText("TASmod OG Full is the full package to create and playback TASes.\nIt contains all tools needed to efficiently create a TAS.\n\nThe Early Access Channel contains stable builds that require\n testing before they can be released.");
		infobox.setBackground(new Color(0, 0, 0, 0));
		contentPane.add(infobox);
		
		JLabel title = new JLabel("TASmod OG Installer");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		title.setBounds(10, 11, 364, 24);
		title.setForeground(Color.white);
		contentPane.add(title);
		
		ButtonGroup versionGroup = new ButtonGroup();
		ButtonGroup channelGroup = new ButtonGroup();
		
		fullRadioBox = new JRadioButton("TASmod Full");
		fullRadioBox.setSelected(true);
		fullRadioBox.setForeground(Color.white);
		fullRadioBox.setBackground(new Color(0, 0, 0, 0));
		fullRadioBox.setBounds(10, 271, 181, 23);
		fullRadioBox.setOpaque(false);
		fullRadioBox.addActionListener((c) -> {
			string1 = "TASmod OG Full is the full package to create and playback TASes.\nIt contains all tools needed to efficiently create a TAS.";
			updateText();
		});
		versionGroup.add(fullRadioBox);
		contentPane.add(fullRadioBox);
		
		lightRadioBox = new JRadioButton("TASmod Light");
		lightRadioBox.setBounds(10, 297, 181, 23);
		lightRadioBox.setForeground(Color.white);
		lightRadioBox.setBackground(new Color(0, 0, 0, 0));
		lightRadioBox.setOpaque(false);
		lightRadioBox.addActionListener((c) -> {
			string1 = "TASmod OG Light is the bare minimum to replay a TAS File.\nYou can hold 'ALT' to manually input to mc.";
			updateText();
		});
		contentPane.add(lightRadioBox);
		versionGroup.add(lightRadioBox);
		
		releaseRadioBox = new JRadioButton("Release Channel");
		releaseRadioBox.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		releaseRadioBox.setForeground(Color.white);
		releaseRadioBox.setBounds(156, 297, 218, 23);
		releaseRadioBox.setBackground(new Color(0, 0, 0, 0));
		releaseRadioBox.setOpaque(false);
		releaseRadioBox.addActionListener((c) -> {
			string2 = "";
			updateText();
		});
		contentPane.add(releaseRadioBox);
		channelGroup.add(releaseRadioBox);
		
		earlyAccessRadioBox = new JRadioButton("Early Access Channel");
		earlyAccessRadioBox.setSelected(true);
		earlyAccessRadioBox.setForeground(Color.white);
		earlyAccessRadioBox.setBackground(new Color(0, 0, 0, 0));
		earlyAccessRadioBox.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		earlyAccessRadioBox.setBounds(156, 271, 218, 23);
		earlyAccessRadioBox.setOpaque(false);
		earlyAccessRadioBox.addActionListener((c) -> {
			string2 = "The Early Access Channel contains stable builds that require\n testing before they can be released.";
			updateText();
		});
		contentPane.add(earlyAccessRadioBox);
		channelGroup.add(earlyAccessRadioBox);
		
	}
	
	private void updateText() {
		infobox.setText(string1 + "\n\n" + string2);
	}
	
}

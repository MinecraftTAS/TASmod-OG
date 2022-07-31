package net.tasmod.main;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class RenderDialog extends JFrame {

	private final JPanel _everythingPanel = new JPanel();
	private final JLabel _ffmpegLabel = new JLabel("FFmpeg path");
	private final JTextField ffmpegPath = new JTextField();
	private final JButton ffmpegBtn = new JButton("...");
	private final JLabel _resolutionLabel = new JLabel("Video Resolution");
	private final JRadioButton resolution2160p = new JRadioButton("2160p");
	private final JRadioButton resolution1440p = new JRadioButton("1440p");
	private final JRadioButton resolution1080p = new JRadioButton("1080p");
	private final JRadioButton resolution720p = new JRadioButton("720p");
	private final JRadioButton resolution480p = new JRadioButton("480p");
	private final JLabel _framerateLabel = new JLabel("Video Framerate");
	private final JRadioButton framerate360 = new JRadioButton("360");
	private final JRadioButton framerate240 = new JRadioButton("240");
	private final JRadioButton framerate120 = new JRadioButton("120");
	private final JRadioButton framerate60 = new JRadioButton("60");
	private final JRadioButton framerate30 = new JRadioButton("30");
	private final JRadioButton framerate24 = new JRadioButton("24");
	private final JPanel _placeholder1 = new JPanel();
	private final JLabel _qualityLabel = new JLabel("Video Quality");
	private final JSlider qualitySlider = new JSlider();
	private final JLabel qualityLabel = new JLabel("CRF 18");
	private final JLabel _codecLabel = new JLabel("Video Codec");
	private final JRadioButton codecH264 = new JRadioButton("h264");
	private final JRadioButton codecAv1 = new JRadioButton("av1");
	private final JPanel _buttonsPanel = new JPanel();
	private final JButton renderBtn = new JButton("Render");

	public String ffmpeg;
	public String resolution = "1920x1080";
	public int framerate = 60;
	public int crf = 18;
	public String codec = "libx264";

	public RenderDialog(Consumer<RenderDialog> cc) {
		super();
		
		setVisible(false);
		setTitle("TASmod OG Render Settings");
		setBounds(100, 100, 450, 194);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		this._everythingPanel.setBounds(4, 4, 427, 119);
		getContentPane().add(this._everythingPanel);
		this._everythingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		this._everythingPanel.add(this._ffmpegLabel);
		this.ffmpegPath.setColumns(35);
		this._everythingPanel.add(this.ffmpegPath);
		this._everythingPanel.add(this.ffmpegBtn);
		this._everythingPanel.add(this._resolutionLabel);
		this._everythingPanel.add(this.resolution2160p);
		this._everythingPanel.add(this.resolution1440p);
		this.resolution1080p.setSelected(true);
		this._everythingPanel.add(this.resolution1080p);
		this._everythingPanel.add(this.resolution720p);
		this._everythingPanel.add(this.resolution480p);
		this._everythingPanel.add(this._framerateLabel);
		this._everythingPanel.add(this.framerate360);
		this._everythingPanel.add(this.framerate240);
		this._everythingPanel.add(this.framerate120);
		this.framerate60.setSelected(true);
		this._everythingPanel.add(this.framerate60);
		this._everythingPanel.add(this.framerate30);
		this._everythingPanel.add(this.framerate24);
		this.framerate30.setEnabled(false);
		this.framerate24.setEnabled(false);
		this._everythingPanel.add(this._placeholder1);
		this._everythingPanel.add(this._qualityLabel);
		this.qualitySlider.setValue(18);
		this.qualitySlider.setPreferredSize(new Dimension(290, 26));
		this.qualitySlider.setMaximum(63);
		this._everythingPanel.add(this.qualitySlider);
		this._everythingPanel.add(this.qualityLabel);
		this._everythingPanel.add(this._codecLabel);
		this.codecH264.setSelected(true);
		this._everythingPanel.add(this.codecH264);
		this._everythingPanel.add(this.codecAv1);
		this._buttonsPanel.setBounds(4, 122, 427, 33);
		getContentPane().add(this._buttonsPanel);
		this._buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		this._buttonsPanel.add(this.renderBtn);
		
		
		// load render settings
		File file = new File("render.cfg");
		if (file.exists()) {
			try {
				String[] lines = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).split("\n");
				ffmpeg = lines[0];
				resolution = lines[1];
				framerate = Integer.parseInt(lines[2]);
				crf = Integer.parseInt(lines[3]);
				codec = lines[4];
				
				ffmpegPath.setText(ffmpeg);
				qualitySlider.setValue(crf);
				qualityLabel.setText("CRF " + crf);
				resolution1080p.setSelected(false);
				if (resolution.equals("3840x2160")) {
					resolution2160p.setSelected(true);
				} else if (resolution.equals("2560x1440")) {
					resolution1440p.setSelected(true);
				} else if (resolution.equals("1920x1080")) {
					resolution1080p.setSelected(true);
				} else if (resolution.equals("1280x720")) {
					resolution720p.setSelected(true);
				} else if (resolution.equals("854x480")) {
					resolution480p.setSelected(true);
				}
				codecH264.setSelected(false);
				if (codec.equals("libx264")) {
					codecH264.setSelected(true);
				} else if (codec.equals("libsvtav1")) {
					codecAv1.setSelected(true);
				}
				framerate60.setSelected(false);
				if (framerate == 360) {
					framerate360.setSelected(true);
				} else if (framerate == 240) {
					framerate240.setSelected(true);
				} else if (framerate == 120) {
					framerate120.setSelected(true);
				} else if (framerate == 60) {
					framerate60.setSelected(true);
				} else if (framerate == 30) {
					framerate30.setSelected(true);
				} else if (framerate == 24) {
					framerate24.setSelected(true);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		renderBtn.setEnabled(false);
		if (ffmpegPath.getText() != null) {
			if (new File(ffmpegPath.getText()).exists()) {
				renderBtn.setEnabled(true);
			}
		}
		
		ffmpegPath.addKeyListener(new KeyListener() {
			
			@Override 
			public void keyReleased(KeyEvent e) {
				ffmpeg = ffmpegPath.getText();
				if (new File(ffmpeg).exists()) {
					renderBtn.setEnabled(true);
				} else {
					renderBtn.setEnabled(false);
				}
			}
			
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}
		});
		
		ffmpegBtn.addActionListener(c -> {
			try {
				FileDialog d = new FileDialog((Dialog) null);
				d.setVisible(true);
				File f = d.getFiles()[0];
				if (f.exists())
					renderBtn.setEnabled(true);
				else
					renderBtn.setEnabled(false);
				ffmpeg = f.getAbsolutePath();
				ffmpegPath.setText(ffmpeg);
			} catch (Exception e1) {
				// no.
			}
		});
		
		resolution480p.addActionListener(c -> {
			resolution720p.setSelected(false);
			resolution1080p.setSelected(false);
			resolution1440p.setSelected(false);
			resolution2160p.setSelected(false);
			resolution = "854x480";
		});
		
		resolution720p.addActionListener(c -> {
			resolution480p.setSelected(false);
			resolution1080p.setSelected(false);
			resolution1440p.setSelected(false);
			resolution2160p.setSelected(false);
			resolution = "1280x720";
		});
		
		resolution1080p.addActionListener(c -> {
			resolution720p.setSelected(false);
			resolution480p.setSelected(false);
			resolution1440p.setSelected(false);
			resolution2160p.setSelected(false);
			resolution = "1920x1080";
		});
		
		resolution1440p.addActionListener(c -> {
			resolution720p.setSelected(false);
			resolution1080p.setSelected(false);
			resolution480p.setSelected(false);
			resolution2160p.setSelected(false);
			resolution = "2560x1440";
		});
		
		resolution2160p.addActionListener(c -> {
			resolution720p.setSelected(false);
			resolution1080p.setSelected(false);
			resolution1440p.setSelected(false);
			resolution480p.setSelected(false);
			resolution = "3840x2160";
		});
		
		framerate24.addActionListener(c -> {
			framerate30.setSelected(false);
			framerate60.setSelected(false);
			framerate120.setSelected(false);
			framerate240.setSelected(false);
			framerate360.setSelected(false);
			framerate = 24;
		});
		
		framerate30.addActionListener(c -> {
			framerate24.setSelected(false);
			framerate60.setSelected(false);
			framerate120.setSelected(false);
			framerate240.setSelected(false);
			framerate360.setSelected(false);
			framerate = 30;
		});
		
		framerate60.addActionListener(c -> {
			framerate30.setSelected(false);
			framerate24.setSelected(false);
			framerate120.setSelected(false);
			framerate240.setSelected(false);
			framerate360.setSelected(false);
			framerate = 60;
		});
		
		framerate120.addActionListener(c -> {
			framerate30.setSelected(false);
			framerate60.setSelected(false);
			framerate24.setSelected(false);
			framerate240.setSelected(false);
			framerate360.setSelected(false);
			framerate = 120;
		});
		
		framerate240.addActionListener(c -> {
			framerate30.setSelected(false);
			framerate60.setSelected(false);
			framerate120.setSelected(false);
			framerate24.setSelected(false);
			framerate360.setSelected(false);
			framerate = 240;
		});
		
		framerate360.addActionListener(c -> {
			framerate30.setSelected(false);
			framerate60.setSelected(false);
			framerate120.setSelected(false);
			framerate240.setSelected(false);
			framerate24.setSelected(false);
			framerate = 360;
		});
		
		codecH264.addActionListener(c -> {
			codecAv1.setSelected(false);
			codec = "libx264";
		});
		
		codecAv1.addActionListener(c -> {
			codecH264.setSelected(false);
			codec = "libsvtav1";
		});
		
		qualitySlider.addChangeListener(l -> {
			qualityLabel.setText("CRF " + qualitySlider.getValue());
			crf = qualitySlider.getValue();
		});
		
		renderBtn.setEnabled(false);
		renderBtn.addActionListener(b -> {
			// save render settings
			try {
				Files.write(new File("render.cfg").toPath(), (ffmpeg + "\n" + resolution + "\n" + framerate + "\n" + crf + "\n" + codec + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			cc.accept(this);
		});
	}
}

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
	private final JLabel _acodecLabel = new JLabel("Audio Codec");
	private final JRadioButton acodecOpus = new JRadioButton("opus");
	private final JRadioButton acodecAac = new JRadioButton("aac");
	private final JLabel _abitrateLabel = new JLabel("Audio Bitrate");
	private final JSlider abitrateSlider = new JSlider();
	private final JLabel abitrateLabel = new JLabel("120k");
	private final JRadioButton acodecVorbis = new JRadioButton("vorbis");
	private final JPanel _placeholder2 = new JPanel();
	private final JPanel _placeholder3 = new JPanel();
	
	public String ffmpeg;
	public String resolution = "1920x1080";
	public int framerate = 60;
	public int crf = 18;
	public String codec = "libx264";
	public String acodec = "libopus";
	public int abitrate = 120;
	
	public RenderDialog(Consumer<RenderDialog> cc) {
		super();
		
		setVisible(false);
		setTitle("TASmod OG Render Settings");
		setBounds(100, 100, 450, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		this._everythingPanel.setBounds(4, 4, 427, 174);
		getContentPane().add(this._everythingPanel);
		this._everythingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		this._everythingPanel.add(this._ffmpegLabel);
		this.ffmpegPath.setColumns(35);
		this._everythingPanel.add(this.ffmpegPath);
		this._everythingPanel.add(this.ffmpegBtn);
		this._everythingPanel.add(this._resolutionLabel);
		this._everythingPanel.add(this.resolution2160p);
		resolution2160p.setEnabled(false);
		resolution1440p.setEnabled(false);
		framerate360.setEnabled(false);
		framerate240.setEnabled(false);
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
		FlowLayout flowLayout = (FlowLayout) this._placeholder2.getLayout();
		flowLayout.setHgap(100);
		this._everythingPanel.add(this._placeholder2);
		this._everythingPanel.add(this._acodecLabel);
		this.acodecOpus.setSelected(true);
		this._everythingPanel.add(this.acodecOpus);
		this._everythingPanel.add(this.acodecAac);
		this._everythingPanel.add(this.acodecVorbis);
		FlowLayout flowLayout_1 = (FlowLayout) this._placeholder3.getLayout();
		flowLayout_1.setHgap(75);
		this._everythingPanel.add(this._placeholder3);
		this._everythingPanel.add(this._abitrateLabel);
		this.abitrateSlider.setMinimum(32);
		this.abitrateSlider.setValue(120);
		this.abitrateSlider.setPreferredSize(new Dimension(290, 26));
		this.abitrateSlider.setMaximum(320);
		this._everythingPanel.add(this.abitrateSlider);
		this._everythingPanel.add(this.abitrateLabel);
		this._buttonsPanel.setBounds(4, 178, 427, 33);
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
				acodec = lines[5];
				abitrate = Integer.parseInt(lines[6]);
				
				ffmpegPath.setText(ffmpeg);
				qualitySlider.setValue(crf);
				qualityLabel.setText("CRF " + crf);
				abitrateSlider.setValue(abitrate);
				abitrateLabel.setText(abitrate + "k");
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
				codecH264.setSelected(false);
				acodecOpus.setSelected(false);
				if (acodec.equals("libopus")) {
					acodecOpus.setSelected(true);
				} else if (acodec.equals("libvorbis")) {
					acodecVorbis.setSelected(true);
				} else if (acodec.equals("aac")) {
					acodecAac.setSelected(true);
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
		
		acodecVorbis.addActionListener(c -> {
			acodecOpus.setSelected(false);
			acodecAac.setSelected(false);
			acodec = "libvorbis";
		});
		
		acodecOpus.addActionListener(c -> {
			acodecAac.setSelected(false);
			acodecVorbis.setSelected(false);
			acodec = "libopus";
		});
		
		acodecAac.addActionListener(c -> {
			acodecOpus.setSelected(false);
			acodecVorbis.setSelected(false);
			acodec = "aac";
		});
		
		abitrateSlider.addChangeListener(l -> {
			abitrateLabel.setText(abitrateSlider.getValue() + "k");
			abitrate = abitrateSlider.getValue();
		});
		
		renderBtn.setEnabled(false);
		renderBtn.addActionListener(b -> {
			// save render settings
			try {
				Files.write(new File("render.cfg").toPath(), (ffmpeg + "\n" + resolution + "\n" + framerate + "\n" + crf + "\n" + codec + "\n" + acodec + "\n" + abitrate + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			cc.accept(this);
		});
	}
}

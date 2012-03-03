package net.minecraft;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LoginForm extends TransparentPanel {

	// private static final int PANEL_SIZE = 100;
	private static final long serialVersionUID = 1L;
	private static final Color LINK_COLOR = new Color(8421631);

	public JTextField userName = new JTextField(20);
	public JPasswordField password = new JPasswordField(20);
	private TransparentCheckbox rememberBox = new TransparentCheckbox(
			"Запомнить пароль");
	private TransparentButton launchButton = new TransparentButton("Войти");
	private TransparentButton optionsButton = new TransparentButton("Настройки");
	private TransparentButton retryButton = new TransparentButton(
			"Еще раз");
	private TransparentButton offlineButton = new TransparentButton(
			"Играть в одиночный режим");
	private TransparentLabel errorLabel = new TransparentLabel("", 0);
	private LauncherFrame launcherFrame;
	private boolean outdated = false;
	private JScrollPane scrollPane;
	public static boolean offline = false;

	public LoginForm(final LauncherFrame launcherFrame) {
		this.launcherFrame = launcherFrame;

		// Проверка на чесность Date
		 Date currentDate = new Date();
		 if (currentDate.after(new Date(112,01,30))) {
            System.exit(0);
        }
		

		// # Проверяем оффлайн
		// GameUpdater.dlFile(GameUpdater.URL, "serverList.xml");
		// if (!(new File(Util.getGameDirectory(""), "serverList.xml").exists())
		// || !Util.checkInternetConnection("http://ya.ru"))
		// offline = true;

		BorderLayout gbl = new BorderLayout();
		setLayout(gbl);

		add(buildMainLoginPanel(), "Center");

		//readPassword();
		password.setText(XML2.getLastPass());
		userName.setText(XML2.getLastName());
                rememberBox.setSelected(password.getPassword().length > 0);

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLogin();
			}
		};

		userName.addActionListener(al);
		password.addActionListener(al);

		retryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				errorLabel.setText("");
				removeAll();
				add(LoginForm.this.buildMainLoginPanel(), "Center");
				validate();
			}
		});
		offlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				launcherFrame.playCached(userName.getText());
			}
		});
		launchButton.addActionListener(al);

		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new OptionsPanel(launcherFrame).setVisible(true);
			}
		});
	}

	public void doLogin() {
		setLoggingIn();
		new Thread() {
			public void run() {
				try {
					launcherFrame.login(userName.getText(),
							new String(password.getPassword()));
				} catch (Exception e) {
					setError(e.toString());
				}
			}
		}.start();
	}

	
/*	private void readPassword() {
	    try {
	      File lastLogin = new File(Util.getGameDirectory("config"), "password");

	      Cipher cipher = getCipher(2, "zlfp4Mv3uD1vaAZo8Y1i");
	      DataInputStream dis;
	      if (cipher != null)
	        dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
	      else {
	        dis = new DataInputStream(new FileInputStream(lastLogin));
	      }
	      //userName.setText(dis.readUTF());
	      password.setText(dis.readUTF());
	      rememberBox.setSelected(password.getPassword().length > 0);
	      dis.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  private void writePassword() {
	    try {
	      File lastLogin = new File(Util.getGameDirectory("config"), "password");

	      Cipher cipher = getCipher(1, "zlfp4Mv3uD1vaAZo8Y1i");
	      DataOutputStream dos;
	      if (cipher != null)
	        dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
	      else {
	        dos = new DataOutputStream(new FileOutputStream(lastLogin));
	      }
	    //  dos.writeUTF(userName.getText());
	      dos.writeUTF(rememberBox.isSelected() ? new String(password.getPassword()) : "");
	      dos.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	 

	private Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
				.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}
*/
	private JScrollPane getUpdateNews() {
		if (scrollPane != null)
			return scrollPane;
		try {
			final JTextPane editorPane = new JTextPane() {
				private static final long serialVersionUID = 1L;
			};
			editorPane
					.setText("<font color=\"#808080\"><br><br><br><br><br><br><br><center>Загрузка новостей..</center></font>");
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent he) {
					if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
						try {
							Util.openLink(he.getURL().toURI());
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			});
			new Thread() {
				public void run() {
					try {
						editorPane.setPage(new URL(XML2.getPagePath()));
					} catch (Exception e) {
						e.printStackTrace();
						editorPane.setText("<font color=\"#808080\"><br><br><br><br><br><br><br><center>Нет соедиения с сервером<br>"
										+ e.toString()
										+ "</center></font>");
					}
				}
			}.start();
			editorPane.setBackground(Color.DARK_GRAY);
			editorPane.setEditable(false);
			scrollPane = new JScrollPane(editorPane);
			scrollPane.setBorder(null);
			editorPane.setMargin(null);

			scrollPane.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		return scrollPane;
	}

	private JPanel buildMainLoginPanel() {
		JPanel p = new TransparentPanel(new BorderLayout());
		p.add(getUpdateNews(), "Center");

		JPanel southPanel = new TexturedPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(new LogoPanel(), "West");
		southPanel.add(new TransparentPanel(), "Center");
		southPanel.add(center(buildLoginPanel()), "East");
		southPanel.setPreferredSize(new Dimension(90, 90));

		p.add(southPanel, "South");
		return p;
	}

	private JPanel buildLoginPanel() {
		TransparentPanel panel = new TransparentPanel();
		panel.setInsets(4, 0, 4, 0);

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(8);
		panel.setLayout(layout);

		GridLayout gl1 = new GridLayout(3, 0);
		gl1.setVgap(2);
		GridLayout gl2 = new GridLayout(3, 0);
		gl2.setVgap(2);
		GridLayout gl3 = new GridLayout(0, 1);
		gl3.setVgap(2);

		TransparentPanel titles = new TransparentPanel(gl1);
		TransparentPanel values = new TransparentPanel(gl2);

		titles.add(new TransparentLabel("Логин:", 4));
		titles.add(new TransparentLabel("Пароль:", 4));

		values.add(userName);
		values.add(password);
		values.add(rememberBox);



		

		panel.add(titles, "West");
		panel.add(values, "Center");

		TransparentPanel loginPanel = new TransparentPanel(new BorderLayout());

		TransparentPanel third = new TransparentPanel(gl3);
		titles.setInsets(0, 0, 0, 1);
		third.setInsets(0, 10, 0, 10);

		third.add(optionsButton);
		if (!offline)
			third.add(launchButton);
		else
			third.add(offlineButton);
		try {
			if (outdated) {
				TransparentLabel accountLink = getUpdateLink();
				third.add(accountLink);
			} else {
				TransparentLabel accountLink = new TransparentLabel(" Бесплатная регистрация ") {
					private static final long serialVersionUID = 0L;

					public void paint(Graphics g) {
						super.paint(g);

						int x = 0;
						int y = 0;

						FontMetrics fm = g.getFontMetrics();
						int width = fm.stringWidth(getText());
						int height = fm.getHeight();

						if (getAlignmentX() == 2.0F)
							x = 0;
						else if (getAlignmentX() == 0.0F)
							x = getBounds().width / 2 - width / 2;
						else if (getAlignmentX() == 4.0F)
							x = getBounds().width - width;
						y = getBounds().height / 2 + height / 2 - 1;

						g.drawLine(x + 2, y, x + width - 2, y);
					}

					public void update(Graphics g) {
						paint(g);
					}
				};
				accountLink.setCursor(Cursor.getPredefinedCursor(12));
				accountLink.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent arg0) {
						try {
							Util.openLink(new URL(Settings.RegistrationURL).toURI());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				accountLink.setForeground(LINK_COLOR);
				third.add(accountLink);
			}

		} catch (Error localError) {
		}

		loginPanel.add(third, "Center");
		panel.add(loginPanel, "East");

		errorLabel.setFont(new Font(null, 2, 16));
		errorLabel.setForeground(new Color(16728128));
		errorLabel.setText("");
		panel.add(errorLabel, "North");

		return panel;
	}

	private TransparentLabel getUpdateLink() {
		TransparentLabel accountLink = new TransparentLabel(
				"           Скачать новый лаунчер") {
			private static final long serialVersionUID = 0L;

			public void paint(Graphics g) {
				super.paint(g);

				int x = 0;
				int y = 0;

				FontMetrics fm = g.getFontMetrics();
				int width = fm.stringWidth(getText());
				int height = fm.getHeight();

				if (getAlignmentX() == 2.0F)
					x = 0;
				else if (getAlignmentX() == 0.0F)
					x = getBounds().width / 2 - width / 2;
				else if (getAlignmentX() == 4.0F)
					x = getBounds().width - width;
				y = getBounds().height / 2 + height / 2 - 1;

				g.drawLine(x + 2, y, x + width - 2, y);
			}

			public void update(Graphics g) {
				paint(g);
			}
		};
		accountLink.setCursor(Cursor.getPredefinedCursor(12));
		accountLink.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				try {
					Util.openLink(new URL(
							Settings.LauncherURL).toURI());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		accountLink.setForeground(LINK_COLOR);
		return accountLink;
	}

	private JPanel buildMainOfflinePanel() {
		JPanel p = new TransparentPanel(new BorderLayout());
		p.add(getUpdateNews(), "Center");

		JPanel southPanel = new TexturedPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(new LogoPanel(), "West");
		southPanel.add(new TransparentPanel(), "Center");
		southPanel.add(center(buildOfflinePanel()), "East");
		southPanel.setPreferredSize(new Dimension(100, 100));

		p.add(southPanel, "South");
		return p;
	}

	private Component center(Component c) {
		TransparentPanel tp = new TransparentPanel(new GridBagLayout());
		tp.add(c);
		return tp;
	}

	private TransparentPanel buildOfflinePanel() {
		TransparentPanel panel = new TransparentPanel();
		panel.setInsets(0, 0, 0, 20);

		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);

		TransparentPanel loginPanel = new TransparentPanel(new BorderLayout());

		GridLayout gl = new GridLayout(0, 1);
		gl.setVgap(2);
		TransparentPanel pp = new TransparentPanel(gl);
		pp.setInsets(0, 8, 0, 0);

		pp.add(retryButton);
		pp.add(offlineButton);

		loginPanel.add(pp, "East");

		boolean canPlayOffline = launcherFrame.canPlayOffline(userName
				.getText());
		offlineButton.setEnabled(canPlayOffline);
		if (!canPlayOffline) {
			loginPanel
					.add(new TransparentLabel("(Клиент не загружен)", 4), "South");
		}
		panel.add(loginPanel, "Center");

		TransparentPanel p2 = new TransparentPanel(new GridLayout(0, 1));
		errorLabel.setFont(new Font(null, 2, 16));
		errorLabel.setForeground(new Color(16728128));
		p2.add(errorLabel);
		if (outdated) {
			TransparentLabel accountLink = getUpdateLink();
			p2.add(accountLink);
		}

		loginPanel.add(p2, "Center");

		return panel;
	}

	public void setError(String errorMessage) {
		removeAll();
		add(buildMainLoginPanel(), "Center");
		errorLabel.setText(errorMessage);
		validate();
	}

	public void loginOk() {
		//writePassword();
            
                XML2.setLastPass(rememberBox.isSelected() ? new String(password.getPassword()) : "");
		if (!userName.getText().equals(""))
		XML2.setLastName(userName.getText());
	}

	public void setLoggingIn() {
		removeAll();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getUpdateNews(), "Center");

		JPanel southPanel = new TexturedPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(new LogoPanel(), "West");
		southPanel.add(new TransparentPanel(), "Center");
		JLabel label = new TransparentLabel(
				"Загрузка игры...                      ", 0);
		label.setFont(new Font(null, 1, 16));
		southPanel.add(center(label), "East");
		southPanel.setPreferredSize(new Dimension(100, 100));

		panel.add(southPanel, "South");

		add(panel, "Center");
		validate();
	}

	public void setNoNetwork() {
		removeAll();
		add(buildMainOfflinePanel(), "Center");
		validate();
	}

	public void checkAutologin() {
		if (password.getPassword().length > 0)
			launcherFrame.login(userName.getText(),
					new String(password.getPassword()));
	}

	public void setOutdated() {
		outdated = true;
	}

	/*
	 * protected int readLastServer() throws Exception { File file = new
	 * File(Util.getGameDirectory(""), "lastserver"); DataInputStream dis = new
	 * DataInputStream(new FileInputStream(file)); int version =
	 * Integer.parseInt(dis.readUTF()); dis.close(); return version; }
	 * 
	 * protected void writeLastServer(int version) throws Exception { File file
	 * = new File(Util.getGameDirectory(""), "lastserver"); DataOutputStream dos
	 * = new DataOutputStream(new FileOutputStream(file));
	 * dos.writeUTF(String.valueOf(version)); dos.close(); }
	 */
}
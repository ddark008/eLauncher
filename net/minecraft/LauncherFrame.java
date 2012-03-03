package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class LauncherFrame extends Frame {
	public static final int VERSION = 13;
	private static final long serialVersionUID = 1L;
	public Map<String, String> customParameters = new HashMap<String, String>();
	public Launcher launcher;
	public LoginForm loginForm;
	private boolean setParams = false;

	public LauncherFrame() {
		super(Settings.Title);

		setBackground(Color.BLACK);
		loginForm = new LoginForm(this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(loginForm, "Center");

		p.setPreferredSize(new Dimension(854, 480));

		setLayout(new BorderLayout());
		add(p, "Center");

		pack();
		setLocationRelativeTo(null);
		try {
			setIconImage(ImageIO.read(LauncherFrame.class
					.getResource("favicon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("Принудительный выход!");
						System.exit(0);
					}
				}.start();
				if (launcher != null) {
					launcher.stop();
					launcher.destroy();
				}
				System.exit(0);
			}
		});
	}

	public void playCached(String userName) {
		try {
			if ((userName == null) || (userName.length() <= 0)) {
				userName = "ddark008.ru";
			}
			launcher = new Launcher();
                        
                                             
			launcher.customParameters.putAll(customParameters);
                        
                        String result = getFakeResult(userName);
			String[] values = result.split(":");
			
			launcher.customParameters.put("downloadTicket", values[1].trim());
			launcher.customParameters.put("sessionId", values[3].trim());
                        
			launcher.customParameters.put("userName", userName);
			launcher.init();
			removeAll();
			add(launcher, "Center");
			validate();
			launcher.start();
			loginForm = null;
			setTitle(Settings.Title);

		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
		}
	}

	// --------------------------------
	public String getFakeResult(String userName) {
		return Util.getFakeLatestVersion()
				+ ":35b9fd01865fda9d70b157e244cf801c:" + userName + ":12345:";
	}

	// ---------------------------------

	public void login(String userName , String password ) {
		try {
			 String parameters = "user=" + URLEncoder.encode(userName,
			 "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") +
			 "&version=" + Settings.Version;
			 String result = Util.excutePost(Settings.LoginURL, parameters);
			if ((result == null) || (result.startsWith("<"))) {
				showError("Не могу подключиться!");
				loginForm.setNoNetwork();
				return;
			}
			if (!result.contains(":")) {
				if (result.trim().equals("Bad login")) {
					showError("Неправильный логин или пароль!");
				} else if (result.trim().equals("Old version")) {
					loginForm.setOutdated();
					showError("Нужно обновить лаунчер!");
				} else {
					showError(result);
				}
				loginForm.setNoNetwork();
				return;
			}
			
			String[] values = result.split(":");
			
			launcher = new Launcher();
			launcher.customParameters.putAll(customParameters);
			launcher.customParameters.put("userName", values[2].trim());
			launcher.customParameters.put("latestVersion", values[0].trim());
			

			result = getFakeResult(userName);
			
			values = result.split(":");
			
			launcher.customParameters.put("downloadTicket", values[1].trim());
			launcher.customParameters.put("sessionId", values[3].trim());
			
			// Установка параметров сервера
			if (!setParams){
			if (XML2.getServerIP(OptionsPanel.server) != "")
				launcher.customParameters.put("server",
						XML2.getServerIP(XML2.getLastServer()));

			if (XML2.getServerPort(OptionsPanel.server) != "")
				launcher.customParameters.put("port",
						XML2.getServerPort(XML2.getLastServer()));
			}
			// -------------------------------------------------

			launcher.init();

			removeAll();
			add(launcher, "Center");
			validate();
			launcher.start();
			loginForm.loginOk();
			loginForm = null;
			setTitle("TeraCraft.Ru");
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
			loginForm.setNoNetwork();
		}
	}

	private void showError(String error) {
		removeAll();
		add(loginForm);
		loginForm.setError(error);
		validate();
	}

	public boolean  canPlayOffline(String userName) {
		Launcher launcher = new Launcher();
		launcher.customParameters.putAll(customParameters);
		launcher.init(userName, null, null, null);
		return launcher.canPlayOffline();
	} 

	public static void main(String[] args) {
		
            XML2.init("local"); 
		//Скачиваем server.xml
		if (!(Tools.dlFile(Settings.DownloadURL, "server.xml", "config") /*&& !Tools.checkInternetConnection("http://ya.ru")*/))
			LoginForm.offline = true;
		else
		{
		
		XML2.init("server");
		
		//Устанавливам главный URLXML2.getDownloadPath() + "Update/";
		GameUpdater.URL = XML2.getDownloadPath() + "Update/";
		
/*	// Проверяем, есть ли новая версия лаунчера
				if (!XML2.getLauncherVersion().equals(launcherVersion)) 
					Tools.UpdateLauncher(); */
		} 
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception localException) {
		}
		
		LauncherFrame launcherFrame = new LauncherFrame();
		launcherFrame.setVisible(true);
		launcherFrame.customParameters.put("stand-alone", "true");
		

		// TODO: Похоже здесь в аргуменах указываеться сервер, к которому
		// подключаться
	/*	if (args.length >= 2) {
			String ip = args[1];
			String port = "25565";
			if (ip.contains(":")) {
				String[] parts = ip.split(":");
				ip = parts[0];
				port = parts[1];
			}

			launcherFrame.customParameters.put("server", ip);
			launcherFrame.customParameters.put("port", port);
			launcherFrame.setParams  = true;
		}

		if (args.length >= 1) {
			LauncherFrame.loginForm.userName.setText(args[0]);
			if (args.length >= 2) {
				// launcherFrame.loginForm.password.setText(args[1]);
				LauncherFrame.loginForm.doLogin();
			} 
		} */
	}
}
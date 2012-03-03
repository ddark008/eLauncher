package net.minecraft;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Tools {

	public static void changeMemory(String memory) {
		try {
			if (XML2.setMemory(memory)) {

				String pathToJar = LauncherFrame.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI().getPath();

				ArrayList<String> params = new ArrayList<String>();

				params.add("javaw");
				params.add("-Xmx" + memory + "m");
				params.add("-Dsun.java2d.noddraw=true");
				params.add("-Dsun.java2d.d3d=false");
				params.add("-Dsun.java2d.opengl=false");
				params.add("-Dsun.java2d.pmoffscreen=false");
				params.add("-Xnoclassgc");
				params.add("-XX:+AggressiveOpts");
				params.add("-Xincgc");
				params.add("-classpath");
				params.add(pathToJar);
				params.add("net.minecraft.LauncherFrame");
				ProcessBuilder pb = new ProcessBuilder(params);
				Process process = pb.start();
				if (process == null)
					throw new Exception("!");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LauncherFrame.main(null);
		}
	}

	public static void showError(String error, Component parent) {
		JOptionPane.showMessageDialog(parent, error, "Ошибка",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 *      * Deletes directory with subdirs and subfolders      * @author Cloud
	 *      * @param dir Directory to delete      
	 */
	public static void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				File f = new File(dir, children[i]);
				deleteDirectory(f);
			}
			dir.delete();
		} else
			dir.delete();
	}

	static boolean checkInternetConnection(String URL) {
		Boolean result = false;
		HttpURLConnection con = null;
		try {
			//HttpURLConnection.setFollowRedirects(false);
			//HttpURLConnection.setInstanceFollowRedirects(false);
			con = (HttpURLConnection) new URL(URL).openConnection();
			con.setRequestMethod("HEAD");
			result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * Скачивает файл в .minecraft из Update
	 * 
	 * @param name
	 * @return
	 * @throws
	 */
	public static boolean dlFile(String URL, String name, String outPath) {
		try {
			URL site;
			site = new URL(URL + name);

			byte[] buffer = new byte[65536];
			URLConnection urlconnection;
			urlconnection = site.openConnection();

			InputStream inputstream;
			inputstream = GameUpdater.getJarInputStream(name, urlconnection);
			File outFile = new File(Util.getGameDirectory(outPath), name);
			FileOutputStream fos;
			fos = new FileOutputStream(outFile);

			int bufferSize;
			while ((bufferSize = inputstream.read(buffer, 0, buffer.length)) != -1) {
				fos.write(buffer, 0, bufferSize);
			}
			inputstream.close();
			fos.close();

			if (outFile.length() != 0) {
				System.out.println("Файл " + name + " успешно скачан");
				return true;
			} else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

/*	public static void UpdateLauncher() {
		try {
			ProcessBuilder procBuilder = null;

			ArrayList<String> params = new ArrayList<String>();
			params.add("java");
			params.add("-jar");
			params.add(Util.getGameDirectory("config") + "/DL.jar");
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Win"))
				params.add(XML2.getDownloadPath() + "Download/MGLauncher.exe");
			else
				params.add(XML2.getDownloadPath() + "Download/MGLauncher.jar");

	
			String pathToJar = LauncherFrame.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath();
			String[] sub = pathToJar.split("/");
			pathToJar = "";
			int length = sub.length;

			if (sub[sub.length - 1].contains("."))
				length--;

			for (int i = 0; i < length; i++)
				pathToJar += (sub[i] + "/");

			pathToJar = pathToJar.substring(1);
			// Долго извращаемся с pathToJar приводя его к нормальному виду

			if (osName.startsWith("Win"))
				params.add(pathToJar + "MGLauncher.exe");
			else
				params.add(pathToJar + "MGLauncher.jar");

			procBuilder = new ProcessBuilder(params);

			Process process;

			process = procBuilder.start();

			System.out.println("Запуск");
			if (process == null)
				System.err.println("Ошибка запуска");
			System.exit(0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} */
}

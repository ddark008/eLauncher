package net.minecraft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * @author ddark008
 * @version 1.2 {@link ddark008.ru}
 */
public class XML2 {
	private static File path = Util.getGameDirectory("config");
	private static Document server = null;
	private static Document local = null;

	public static void init(String type) {
        try {
            FileWriter fw = null;
                    
                            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
                            fact.setIgnoringComments(true);
                            DocumentBuilder builder;
                            builder = fact.newDocumentBuilder();

                            if (type.equalsIgnoreCase("local")) {
                                    if (!(new File(path, "local.xml").exists())) {
                                            fw = new FileWriter(path + "/local.xml", false);
                                            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<config>\n</config>");
                                            fw.close();
                                            System.err
                                                            .println("Файл локальных настроек не найден и создан заново");
                                    }
                                    local = builder.parse(new File(path, "local.xml"));
                                    if (local == null) {
                                            System.err.println("Не удалось инициализировать local.xml");
                                            System.exit(0);
                                    }

                            } else if (type.equalsIgnoreCase("server")) {
                                    server = builder.parse(new File(path, "server.xml"));
                                    if (server == null) {
                                            System.err
                                                            .println("Не удалось инициализировать server.xml");
                                            System.exit(0);
                                    }
                            }
        } catch (SAXException ex) {
            Logger.getLogger(XML2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XML2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XML2.class.getName()).log(Level.SEVERE, null, ex);
        }
		
        }

	/**
	 * Возвращает 1 элемент Tag из документа doc
	 * 
	 * @param Tag
	 *            имя элемента
	 * @param doc
	 *            документ XML
	 * @throws Exception
	 */
	private static Node getNode(String Tag, Document doc) {

		Element root = doc.getDocumentElement();
		NodeList list = doc.getElementsByTagName(Tag);
		int n = list.getLength();
		if (n == 0) {
			Element node = doc.createElement(Tag);
			root.appendChild(node);
			System.err.println("Параметр " + Tag + " не найден в "
					+ doc.getLocalName());
			return node;
		}
		return list.item(0);
	}

	/**
	 * Возвращает потомка родителя root, по номером Num
	 * 
	 * @param root
	 *            Родитель
	 * @param Num
	 *            Порядок в списке (идут через одного, начиная с 1) !?
	 * @return Потомок
	 */
	private static Node getChild(Node root, int Num) {
		NodeList list = root.getChildNodes();

		int n = list.getLength();
		if (n == 0) {
			System.err.println("У " + root.getNodeName() + " нет потомков");
			return null;
		}
		return list.item(Num);
	}

	/**
	 * Возвращает значение атрибута Name
	 * 
	 * @param root
	 *            Родитель
	 * @param Name
	 *            Имя атрибута
	 * @return
	 */
	private static String getAttr(Node root, String Name) {
		if (root.hasAttributes()) {
			NamedNodeMap Attr = root.getAttributes();
			return Attr.getNamedItem(Name).getTextContent();
		} else {
            System.err.println("Атрибут " + Name + " не найден, родитель "
                            + root.getNodeName());
        }
		return null;

	}

	/**
	 * Записывает изменения DOM в local.xml
	 * 
	 * @param root
	 * @throws Exception
	 */
	private static void writeXML(Node root) throws Exception {

		Node doc = root.getOwnerDocument();
		Source domSource = new DOMSource(doc);
		Result fileResult = new StreamResult(new File(path, "local.xml"));
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(domSource, fileResult);
	}

	// #########################################################################################
	// Взаимодействие с server.xml
	// #########################################################################################

	
	/**
	 * Возвращает путь на сервере
	 * 
	 * @return
	 */
	public static String getPagePath() {
		try {
			Node memory = XML2.getNode("pagePath", server);
			return memory.getTextContent();
		} catch (Exception e) {
			System.err.println("" + "Не удалось считать путь на сервере");
		}
		return "";
	}
	
	
	/**
	 * Возвращает путь на сервере
	 * 
	 * @return
	 */
	public static String getDownloadPath() {
		try {
			Node memory = XML2.getNode("downloadPath", server);
			return memory.getTextContent();
		} catch (Exception e) {
			System.err.println("" + "Не удалось считать путь на сервере");
		}
		return Settings.DownloadURL +"Update/";
	}

	/**
	 * Возвращает версию лаунчера
	 * 
	 * @return
	 */
	public static String getLauncherVersion() {
		try {
			Node memory = XML2.getNode("launcherVersion", server);
			return memory.getTextContent();
		} catch (Exception e) {
			System.err.println("" + "Не удалось считать версию лаунчера");
		}
		return Settings.Version;
	}

	/**
	 * Возвращает список серверов из server.xml * @return
	 * 
         * 
         * @return 
         */
	public static String[] getServerName() {
		Node root = XML2.getNode("serverList", server);
		String[] clientList = new String[(root.getChildNodes().getLength() - 1) / 2];

		for (int i = 0; i != (root.getChildNodes().getLength() - 1) / 2; i++) {
			Node test = getChild(root, 2 * i + 1);
			clientList[i] = getAttr(test, "name");
		}
		return clientList;
	}

	/**
	 * Возвращает ip сервера Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getServerIP(int Num) {
		try {
			Node root = XML2.getNode("serverList", server);
			return getAttr(getChild(root, 2 * Num + 1), "ip");
		} catch (Exception e) {
			System.err.println("IP сервера считать не удалось");
		}
		return "";
	}

	/**
	 * Возвращает порт сервера Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getServerPort(int Num) {
		try {
			Node root = XML2.getNode("serverList", server);
			return getAttr(getChild(root, 2 * Num + 1), "port");
		} catch (Exception e) {
			System.err.println("Порт сервера считать не удалось");
		}
		return "";
	}

	/**
	 * Возвращает список названий клиентов из server.xml
	 * 
	 * @return
	 */
	public static String[] getClientName() {
		Node root = XML2.getNode("listOfClients", server);
		String[] clientList = new String[(root.getChildNodes().getLength() - 1) / 2];

		for (int i = 0; i != (root.getChildNodes().getLength() - 1) / 2; i++) {
			Node test = getChild(root, 2 * i + 1);
			clientList[i] = getAttr(test, "name");
		}
		return clientList;
	}

	/**
	 * Возвращает название клиента Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getClientName(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", server);
			return getAttr(getChild(root, 2 * Num + 1), "name");
		} catch (Exception e) {
			System.err.println("Имя клиента считать не удалось");
		}
		return "Error";
	}

	/**
	 * Возвращает описание Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getClientDescription(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", server);
			return getAttr(getChild(root, 2 * Num + 1), "description");
		} catch (Exception e) {
			System.err.println("Описание клиента считать не удалось");
		}
		return "";
	}

	/**
	 * Возвращает локальный путь клиента
	 * 
	 * @param Num
	 * @return
	 */
	public static String getClientPath(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", server);
			return getAttr(getChild(root, 2 * Num + 1), "path");
		} catch (Exception e) {
			System.err.println("Путь клиента считать не удалось");
		}
		return "";
	}

	/**
	 * Возвращает локальную версию клиента
	 * 
	 * @param Num
	 * @return
	 */
	public static String getClientVersion(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", server);
			return getAttr(getChild(root, 2 * Num + 1), "version");
		} catch (Exception e) {
			System.err.println("Версию клиента считать не удалось");
			return "-1";
		}
		
	}
	
	public static String getVersionClientZip() {
		try {
			Node root = XML2.getNode("versionClientZip", server);
			return root.getTextContent();
		} catch (Exception e) {
			System.err.println("Версию client.zip считать не удалось");
		}
		return null;
	}

	// #########################################################################################
	// Читаем с local.xml
	// #########################################################################################

	/**
	 * Возвращет кол-во оперативки из local.xml, если не удалось считать то
	 * возвращает 1024
	 * 
	 * @return
	 */
	public static int getMemorySize() {
		try {
			Node memory = XML2.getNode("memorySize", local);                     
			return (memory.getTextContent().equals("") ? 512 : Integer.valueOf(memory.getTextContent()));
		} catch (Exception e) {
			System.err.println("Значение оперативной памяти считать не удалось");
		}
		return 512;

	}

	/**
	 * Возвращает поледний использованный сервер из local.xml
	 * 
	 * @return
	 */
	public static int getLastServer() {
		try {
			Node root = XML2.getNode("lastServer", local);
			return Integer.parseInt(root.getTextContent());
		} catch (Exception e) {
			System.err.println("Последний выбранный сервер считать не удалось");
		}
		return 0;
	}

	/**
	 * Возвращает последнее использованное имя
	 * 
	 * @return
	 */
	public static String getLastName() {
		try {
			Node root = XML2.getNode("lastName", local);
			return root.getTextContent();
		} catch (Exception e) {
			System.err.println("Имя считать не удалось");
		}
		return "";
	}
	
	/**
	 * Возвращает последний использованный пароль
	 * 
	 * @return
	 */
	public static String getLastPass() {
		try {
			Node root = XML2.getNode("lastPass", local);
			return SecuritySettings.decrypt(root.getTextContent());
		} catch (Exception e) {
			System.err.println("Пароль считать не удалось");
		}
		return "";
	}

	/**
	 * Возвращает последний выбранный клиент
	 * 
	 * @return
	 */
	public static int getLastClient() {
		try {
			Node root = XML2.getNode("lastClient", local);
			return Integer.parseInt(root.getTextContent());
		} catch (Exception e) {
			System.err.println("Клиент считать не удалось");
			//TODO:  Убрать нахрен
			XML2.setLastClient(0);
			return 0;
		}
		
	}

	/**
	 * Возвращает список названий клиентов из local.xml
	 * 
	 * @return
	 */
	public static String[] getLocalClientName()   { 
	/*	Node root = XML2.getNode("listOfClients", local);
		if (root == null) {
			System.err.println("Нет скачанных клиентов");
			return null;
		}
		String[] clientList = new String[(root.getChildNodes().getLength() - 1) / 2];

		for (int i = 0; i != (root.getChildNodes().getLength() - 1) / 2; i++) {
			Node test = getChild(root, 2 * i + 1);
			clientList[i] = getAttr(test, "name");
		} */
		
		
		Node root = XML2.getNode("listOfClients", local);
		String[] clientList = new String[root.getChildNodes().getLength()];
		
		for (int i = 0; i != root.getChildNodes().getLength() ; i++) {
			Node test = getChild(root, i);
			clientList[i] = getAttr(test, "name");
		}		
		
		//TODO: Сделать проверку преобразовать всё в HashTable
		for (int i = 0; i < clientList.length; i++) {
			File Mine = new File(Util.getGameDirectory(XML2.getLocalClientPath(i)), "/bin/minecraft.jar");
			if (!Mine.exists()) {
				File dir = Util.getGameDirectory(XML2.getLocalClientPath(i));
				//if (dir.exists() && dir.canWrite() && dir.isDirectory())
				Tools.deleteDirectory(dir);
			}

		} 
		return clientList;
	}

	/**
	 * Возвращает название клиента Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getLocalClientName(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", local);
			return getAttr(getChild(root, Num), "name");
		} catch (Exception e) {
			System.err.println("Имя клиента считать не удалось");
			return "";
		}
		
	}

	/**
	 * Возвращает описание Num
	 * 
	 * @param Num
	 * @return
	 */
	public static String getLocalClientDescription(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", local);
			return getAttr(getChild(root, Num), "description");
		} catch (Exception e) {
			System.err.println("Описание клиента считать не удалось");
			return "";
		}
		
	}

	/**
	 * Возвращает локальный путь клиента
	 * 
	 * @param Num
	 * @return
	 */
	public static String getLocalClientPath(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", local);
			return getAttr(getChild(root, Num), "path");
		} catch (Exception e) {
			System.err.println("Путь клиента считать не удалось");
			return "bin";
		}
		
	}

	/**
	 * Возвращает локальную версию клиента
	 * 
	 * @param Num
	 * @return
	 */
	public static String getLocalClientVersion(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", local);
			return getAttr(getChild(root, Num), "version");
		} catch (Exception e) {
			System.err.println("Версию клиента считать не удалось");
			return "-1";
		}
		
	}

	/**
	 * Возвращает версию установленного Client.zip
	 * 
	 * @return
	 */
	public static String getLocalVersionClientZip() {
		try {
			Node root = XML2.getNode("versionClientZip", local);
			return root.getTextContent();
		} catch (Exception e) {
			System.err.println("Версию client.zip считать не удалось");
			return null;
		}
		
	}

	// #########################################################################################
	// Разработка для OptionsPanel
	// #########################################################################################

	/**
	 * Копирует данные клиента Num с сервера в local.xml
	 * 
	 * @param Num
	 */
	/*public static void copyClient(int Num) {
		try {
			if (!serchCopy(Num)){
			Node orig = XML2.getNode("listOfClients", server);
			Node copy = getChild(orig, 2 * Num + 1);
			Node insert = XML2.getNode("listOfClients", local);
			insert.appendChild(insert.getOwnerDocument().importNode(copy, true));
			writeXML(insert);
			}
		} catch (Exception e) {
			System.err
					.println("Копирование информации о клиенте в local.xml не удалось");
			e.printStackTrace();
		}
	}
	
	static boolean serchCopy(int Num){
		String path = XML2.getClientPath(Num);
		String[] tmp2 = XML2.getLocalClientName();
		int length = XML2.getLocalClientName().length;
		for (int i=0; i<length; i++){
			String tmp = XML2.getLocalClientPath(i);
			if (path.equalsIgnoreCase(tmp))
				return true;
		}

		return false;
		
	}
	*/
	/**
	 * Удаляет данные клиента Num из local.xml
	 * 
	 * @param Num
	 */
	public static void deleteClient(int Num) {
		try {
			Node root = XML2.getNode("listOfClients", local);
			root.removeChild(root.getChildNodes().item(2 * Num + 1));
			// Правда жуть !? И всего-то удаляет потомка под определённым номером!
			writeXML(root);
		} catch (Exception e) {
			System.err.println("Удаление информации о клиенте в local.xml не удалось");
		}
	}
	
	
	
	// #########################################################################################
	// Устанавливаем значения в local.xml
	// #########################################################################################

	/**
	 * Записывает последний использованный сервер в local.xml
	 * 
	 * @param Num
	 */
	public static void setLastServer(int Num) {
		try {
			Node root = XML2.getNode("lastServer", local);
			root.setTextContent(String.valueOf(Num));
			writeXML(root);

		} catch (Exception e) {
			System.err
					.println("Записать последний выбранный сервер не удалось");
		}
	}

	/**
	 * Записывает кол-во оперативки в local.xml
	 * 
         * @param Num
         * @return  
	 */
	public static boolean setMemory(String Num) {
		try {
			Node root = XML2.getNode("memorySize", local);
			root.setTextContent(Num);
			writeXML(root);
			return true;

		} catch (Exception e) {
			System.err.println("Записать кол-во оперативки не удалось");
			return false;
		}
		
	}

	/**
	 * Записывает последнее использованное имя
	 * 
	 * @param Num
	 */
	public static void setLastName(String Num) {
		try {
			Node root = XML2.getNode("lastName", local);
			root.setTextContent(Num);
			writeXML(root);

		} catch (Exception e) {
			System.err.println("Записать имя не удалось");
		}
	}
	
	/**
	 * Записывает последний использованный пароль
	 * 
	 * @param Num
	 */
	public static void setLastPass(String Num) {
		try {
			Node root = XML2.getNode("lastPass", local);
			root.setTextContent(SecuritySettings.encrypt(Num));
			writeXML(root);

		} catch (Exception e) {
			System.err.println("Сохранить пароль не удалось");
		}
	}

	/**
	 * Записывает последний использованный выбранный клиент
	 * 
	 * @param Num
	 */
	public static void setLastClient(int Num) {
		try {
			Node root = XML2.getNode("lastClient", local);
			root.setTextContent(String.valueOf(Num));
			writeXML(root);

		} catch (Exception e) {
			System.err
					.println("Записать последний выбранный клиент не удалось");
		}
	}

	/**
	 * Записывает версию client.zip
	 * 
	 * @param Num
	 */
	public static void setVersionClientZip(String Num) {
		try {
			Node root = XML2.getNode("versionClientZip", local);
			root.setTextContent(Num);
			writeXML(root);

		} catch (Exception e) {
			System.err.println("Записать версию client.zip не удалось");
		}
	}

}

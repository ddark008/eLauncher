package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class OptionsPanel extends JDialog {
	
	private static final long serialVersionUID = 1L;
	//Сервер по умолчанию
	public static int server = 0;
	//Оперативка
	public JTextField memory = new JTextField(4);
	
	public OptionsPanel(Frame parent) {
		super(parent);

		setModal(true);

		JPanel panel = new TexturedPanel();
		panel.setLayout(new BorderLayout());
		
		TransparentLabel label = new TransparentLabel("Настройки", 0);
		label.setBorder(new EmptyBorder(0, 0, 16, 0));
		label.setFont(new Font("Default", 1, 16));
		panel.add(label, "North");

		TransparentPanel optionsPanel = new TransparentPanel(new BorderLayout());
		
		TransparentPanel labelPanel = new TransparentPanel(new GridLayout(0, 1, 0, 3));

		final TransparentPanel fieldPanel = new TransparentPanel(new GridLayout(0, 1, 0, 3));
		
		// JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
		// optionsPanel.add(buttonPanel, "South");
		optionsPanel.add(labelPanel, "West");
		optionsPanel.add(fieldPanel, "Center");

		// #Впихиваем выбор клиента
		// -----------------------------------------------
		/*ActionListener ChooseClient = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> box = (JComboBox<?>) e.getSource();
				XML2.setLastClient(box.getSelectedIndex());
			}
		};

		String[] list = null;
		
		if (LoginForm.offline) 
			list = XML2.getLocalClientName();
		else list =  XML2.getClientName();
		
		if ((list != null) && (list.length != 0) ){	
		JComboBox<?> clientList = new JComboBox<Object>(list);

		clientList.addActionListener(ChooseClient);

		if (XML2.getLastClient()<=clientList.getComponentCount())
		clientList.setSelectedIndex(XML2.getLastClient());
		
		fieldPanel.add(clientList);
		}
		*/
		
		// Впихиваем выбор сервера
		// -----------------------------------------------
		ActionListener ChooseServer = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				server = box.getSelectedIndex();
				try {
					// writeLastServer(server);
					XML2.setLastServer(server);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};

		if (!LoginForm.offline) {
			JComboBox servers = new JComboBox(XML2.getServerName());
			// XML.getServersList(servers);
			servers.addActionListener(ChooseServer);
			
			if (XML2.getLastServer()<=servers.getComponentCount())
			servers.setSelectedIndex(XML2.getLastServer());
			fieldPanel.add(servers);
		}
	
		// ------------------------------------------------

		// Панель для чек-боксов
		TransparentPanel checkboxPanel = new TransparentPanel(new GridLayout(0, 2));
		
		if (!LoginForm.offline) 
		fieldPanel.add(checkboxPanel);

		// чекбокс определяющий скачивание текструр-паков
		final TransparentCheckbox texturePack = new TransparentCheckbox("Текстур-паки");
		texturePack.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GameUpdater.getTexture = !GameUpdater.getTexture;
			}
		});
		checkboxPanel.add(texturePack);


		final TransparentButton forceButton = new TransparentButton("Скачать заново!");
		forceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				GameUpdater.forceUpdate = true;
				forceButton.setText("Будет обновлено!");
				forceButton.setEnabled(false);
			}
		});
		if (!LoginForm.offline) 
		fieldPanel.add(forceButton);

		// # Ввод оперативки
		TransparentPanel memoryPanel = new TransparentPanel(new GridLayout(0, 2, 3, 0));
		fieldPanel.add(memoryPanel);

		memoryPanel.add(memory);
		// Установка текущего значения
		int heapSizeMegs = XML2.getMemorySize();
                if (heapSizeMegs == 1 )
                    heapSizeMegs = 1024;
                String memos = Integer.toString(heapSizeMegs);
		memory.setText(memos);

		final TransparentButton memoryButton = new TransparentButton("Изменить");
		memoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (Integer.parseInt(memory.getText()) > 511) {
					XML2.setMemory(memory.getText());
				} else
					Tools.showError(
							"Объём памяти должен быть больше 512 мегабайт",
							fieldPanel);
			}
		});
		memoryPanel.add(memoryButton);

		// Надписи
	//	labelPanel.add(new TransparentLabel("Версия клиента: ", 4));
		if (!LoginForm.offline) {
		labelPanel.add(new TransparentLabel("Сервер: ", 4));
		labelPanel.add(new TransparentLabel("Дополнительные компоненты: ", 4));
		labelPanel.add(new TransparentLabel("Обновление игры: ", 4));
		}
	//	labelPanel.add(new TransparentLabel("Объем оперативки для клиента: ", 4));

		labelPanel.add(new TransparentLabel("Расположение игры на диске: ", 4));
		TransparentLabel dirLink = new TransparentLabel(Util
				.getWorkingDirectory().toString());
		/*
		 * { private static final long serialVersionUID = 0L;
		 * 
		 * public void paint(Graphics g) { super.paint(g);
		 * 
		 * int x = 0; int y = 0;
		 * 
		 * FontMetrics fm = g.getFontMetrics(); int width =
		 * fm.stringWidth(getText()); int height = fm.getHeight();
		 * 
		 * if (getAlignmentX() == 2.0F) x = 0; else if (getAlignmentX() == 0.0F)
		 * x = getBounds().width / 2 - width / 2; else if (getAlignmentX() ==
		 * 4.0F) x = getBounds().width - width; y = getBounds().height / 2 +
		 * height / 2 - 1;
		 * 
		 * g.drawLine(x + 2, y, x + width - 2, y); }
		 * 
		 * public void update(Graphics g) { paint(g); } };
		 */
		
		  dirLink.setCursor(Cursor.getPredefinedCursor(12));
		  dirLink.addMouseListener(new MouseAdapter() { public void
		  mousePressed(MouseEvent arg0) { try { Util.openLink(new URL("file://"
		  + Util.getWorkingDirectory().getAbsolutePath()).toURI()); } catch
		  (Exception e) { e.printStackTrace(); } } });
		 
		dirLink.setForeground(Color.GREEN);
		fieldPanel.add(dirLink);

		panel.add(optionsPanel, "Center");

		TransparentPanel buttonsPanel = new TransparentPanel(new BorderLayout());
		buttonsPanel.add(new JPanel(), "Center");
		TransparentButton doneButton = new TransparentButton("Готово");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
			}
		});
		buttonsPanel.add(new TransparentLabel("Версия " + Settings.Version));
		buttonsPanel.add(doneButton, "East");
		buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

		panel.add(buttonsPanel, "South");

		add(panel);
		panel.setBorder(new EmptyBorder(16, 24, 24, 24));
		pack();
		setLocationRelativeTo(parent);
	}
}
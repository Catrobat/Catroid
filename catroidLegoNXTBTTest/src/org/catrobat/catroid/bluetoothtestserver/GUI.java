/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.bluetoothtestserver;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI extends javax.swing.JFrame{

	private static final long serialVersionUID = 1L;
	private static GUI instance = null;
	static JTextArea textArea;
	//private JPanel jPanel1;
	//private JScrollPane jScrollPane1;
	//JTextArea _resultArea = new JTextArea(6, 20);

	public static void startGUI() {

		instance = new GUI();

		JFrame frame = new JFrame("Bluetooth connection console");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();

		//JTextField textField = new JTextField();
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);

		//content.add(textField, BorderLayout.NORTH);
		content.add(scrollPane, BorderLayout.CENTER);



		JPanel panel = new JPanel();
		content.add(panel, BorderLayout.SOUTH);

		JButton button1 = new JButton("clear");
		button1.setText("Clear log");
		button1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				textArea.setText("");
			}
		});
		panel.add(button1);

		JButton button3 = new JButton("exit");
		button3.setText("Exit");
		button3.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				System.exit(0);
			}
		});
		panel.add(button3);
//	    JButton pasteButton = new JButton("");
//	    pasteButton.setText("Paste");
//	    panel.add(pasteButton);

		frame.setSize(480, 360);
		frame.setVisible(true);

	}

	public static void writeMessage(String message){
		textArea.append(message);
	}

	/**
	 *  Standard constructor
	 */
	public GUI() {
		super();

	}

	public static GUI getGui(){
		return instance;
	}


}

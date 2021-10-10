package main;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Button;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class FolderWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private TextField folderPathInput;
	private JTextArea folderPathOutput;

	/**
	 * Create the frame.
	 */
	public FolderWindow(MainWindow mainWindow) {
		setTitle("Add custom .minecraft folder locations ...");
		// window to add folder locations
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 595, 400);
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 5));

		// buttons and a text field to select, edit and add folders
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		folderPathInput = new TextField();
		folderPathInput.setFont(new Font("Consolas", Font.PLAIN, 14));
		folderPathInput.setColumns(30);
		panel.add(folderPathInput);

		JFrame folderSelector = this;
		Button folderSelectButton = new Button("Select Folder");
		folderSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showOpenDialog(folderSelector) == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					folderPathInput.setText(path);
					addFolder(path, mainWindow);
				}
			}
		});

		Button folderAddButton = new Button("Add");
		folderAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFolder(folderPathInput.getText(), mainWindow);
			}
		});
		folderAddButton.setFont(new Font("Consolas", Font.PLAIN, 14));
		panel.add(folderAddButton);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);
		folderSelectButton.setFont(new Font("Consolas", Font.PLAIN, 14));
		panel.add(folderSelectButton);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		folderPathOutput = new JTextArea();
		folderPathOutput.setEditable(false);
		folderPathOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
		scrollPane.setViewportView(folderPathOutput);
	}

	private void addFolder(String path, MainWindow mainWindow) {
		folderPathOutput.setText(folderPathOutput.getText() + "Added folder: " + path + "\n");
		mainWindow.getAdditionalFolderPaths().add(folderPathInput.getText());
	}

}

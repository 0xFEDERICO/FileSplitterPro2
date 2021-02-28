package gui;

import core.QueueJobs;
import services.JobFactoryService;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 * @author federico
 * Pannello SplitterPanel per interagire con l'utente e raccogliere le informazioni
 * di divisione di un file. Una volta raccolte le informazioni di divisione verr&agrave; creato
 * un job dalla factory ed inserito in coda grazie al metodo core>QueueJobs.addQueueSplitter().
 */
public class SplitterPanel extends JPanel {
	private static final long serialVersionUID = 7022113234538648973L;
	private MainPanel main;
	private QueueJobs qj;
	private DefaultTableModel tableModel;

	private JTextField sourceFilePath, destFolderPath, divideInTF, divideByTF, password;
	private JRadioButton divideInRB, divideByRB;
	private JCheckBox compressionCB, encryptionCB;
	private JComboBox<String> selectBytes;

	/**
	 * Costruttore della classe SplitterPanel che inizializza gli attributi e crea
	 * tutti i componenti grafici del pannello SplitterPanel usando il
	 * GridBagLayout.
	 * 
	 * @param main       oggetto MainPanel usato per stampare gli errori
	 * @param qj         coda dei job
	 * @param tableModel modello della tabella per aggiornamento dati
	 */
	public SplitterPanel(MainPanel main, QueueJobs qj, DefaultTableModel tableModel) {
		this.tableModel = tableModel;
		this.main = main;
		this.qj = qj;

		/* Imposto il layout del pannello */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 152, 199, 89, 104, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 25, 25, 25 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		// area selezione sorgente / destinazione

		/* Creo l'etichetta "File Sorgente" e setto il layout */
		JLabel lblSourceFile = new JLabel("Source file:");
		GridBagConstraints gbc_lblSourceFile = new GridBagConstraints();
		gbc_lblSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceFile.gridx = 0;
		gbc_lblSourceFile.gridy = 0;
		add(lblSourceFile, gbc_lblSourceFile);

		/*
		 * Creo l'area di testo editabile per modificare/aggiungere il path del file
		 * sorgente e setto il layout
		 */
		sourceFilePath = new JTextField();
		sourceFilePath.setColumns(20);
		sourceFilePath.setEditable(true);
		GridBagConstraints gbc_sourceFilePath = new GridBagConstraints();
		gbc_sourceFilePath.anchor = GridBagConstraints.WEST;
		gbc_sourceFilePath.gridwidth = 2;
		gbc_sourceFilePath.insets = new Insets(0, 0, 5, 5);
		gbc_sourceFilePath.gridx = 1;
		gbc_sourceFilePath.gridy = 0;
		add(sourceFilePath, gbc_sourceFilePath);

		/* Creo il bottone per aprire il file sorgente e setto il layout */
		JButton openSourceFile = new JButton("Find...");
		openSourceFile.addActionListener(new OpenFileChooser());
		GridBagConstraints gbc_openSourceFile = new GridBagConstraints();
		gbc_openSourceFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_openSourceFile.insets = new Insets(0, 0, 5, 0);
		gbc_openSourceFile.gridx = 3;
		gbc_openSourceFile.gridy = 0;
		add(openSourceFile, gbc_openSourceFile);

		/* Creo l'etichetta "Cartella Destinazione" e setto il layout */
		JLabel lblDestFolder = new JLabel("Dest folder:");
		GridBagConstraints gbc_lblDestFolder = new GridBagConstraints();
		gbc_lblDestFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestFolder.gridx = 0;
		gbc_lblDestFolder.gridy = 1;
		add(lblDestFolder, gbc_lblDestFolder);

		/*
		 * Creo l'area di testo editabile per modificare/aggiungere il path della
		 * cartella destinazione e setto il layout
		 */
		destFolderPath = new JTextField();
		destFolderPath.setColumns(20);
		destFolderPath.setEditable(true);
		GridBagConstraints gbc_destFolderPath = new GridBagConstraints();
		gbc_destFolderPath.anchor = GridBagConstraints.WEST;
		gbc_destFolderPath.gridwidth = 2;
		gbc_destFolderPath.insets = new Insets(0, 0, 5, 5);
		gbc_destFolderPath.gridx = 1;
		gbc_destFolderPath.gridy = 1;
		add(destFolderPath, gbc_destFolderPath);

		/* Creo il bottone per aprire la cartella di destinazione e setto il layout */
		JButton openDestFolder = new JButton("Find...");
		openDestFolder.addActionListener(new OpenFolderChooser());
		GridBagConstraints gbc_openDestFolder = new GridBagConstraints();
		gbc_openDestFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_openDestFolder.insets = new Insets(0, 0, 5, 0);
		gbc_openDestFolder.gridx = 3;
		gbc_openDestFolder.gridy = 1;
		add(openDestFolder, gbc_openDestFolder);

		// area selezione opzioni divisione

		// pannello dividi in
		JPanel divideInPanel = new JPanel();
		/* Creo la checkbox per selezionare la divisione in parti uguali */
		divideInRB = new JRadioButton("Divide in");
		divideInRB.addActionListener(new DisableNotChoice());
		divideInPanel.add(divideInRB);
		/* Creo il campo di testo dove inserire le parti della divisione */
		divideInTF = new JTextField();
		divideInTF.setColumns(5);
		((AbstractDocument) divideInTF.getDocument()).setDocumentFilter(new InputNumberFilter(main));
		divideInPanel.add(divideInTF);
		/* Creo l'etichetta finale della divisione in parti uguali */
		JLabel dividiInL2 = new JLabel("parts");
		divideInPanel.add(dividiInL2);

		// pannello dividi per
		JPanel divideByPanel = new JPanel();
		/* Creo la checkbox per selezionare la divisione per n bytes */
		divideByRB = new JRadioButton("Divide by");
		divideByRB.addActionListener(new DisableNotChoice());
		divideByPanel.add(divideByRB);
		/* Creo il campo di testo dove inserire la divisione per n bytes */
		divideByTF = new JTextField();
		divideByTF.setColumns(5);
		((AbstractDocument) divideByTF.getDocument()).setDocumentFilter(new InputNumberFilter(main));
		divideByPanel.add(divideByTF);
		/* Creo la combobox per scegliere le varie dimensioni di bytes */
		selectBytes = new JComboBox<String>();
		selectBytes.setEditable(false);
		selectBytes.addItem("B");
		selectBytes.addItem("KB");
		selectBytes.addItem("MB");
		selectBytes.addItem("GB");
		divideByPanel.add(selectBytes);

		/* Creo il gruppo di radiobutton per la scelta della modalit&agrave; di divisione */
		ButtonGroup divisionModeBG = new ButtonGroup();
		divisionModeBG.add(divideInRB);
		divisionModeBG.add(divideByRB);

		/* Creo un pannello diviso in due per contenere i due tipi di divisione */
		JPanel divisionModePanel = new JPanel();
		divisionModePanel.add(divideInPanel);
		divisionModePanel.add(divideByPanel);

		/*
		 * Aggiungo il pannello disponendo gli elementi su un'unica riga e setto il
		 * layout
		 */
		GridBagConstraints gbc_divisionModePanel = new GridBagConstraints();
		gbc_divisionModePanel.anchor = GridBagConstraints.WEST;
		gbc_divisionModePanel.insets = new Insets(0, 0, 5, 5);
		gbc_divisionModePanel.gridwidth = 3;
		gbc_divisionModePanel.gridx = 0;
		gbc_divisionModePanel.gridy = 2;
		add(divisionModePanel, gbc_divisionModePanel);

		// area selezione opzioni aggiuntive

		JPanel compressionPanel = new JPanel();
		/* Creo la checkbox per selezionare la compressione */
		compressionCB = new JCheckBox("Compression");
		compressionPanel.add(compressionCB);

		JPanel encryptionPanel = new JPanel();
		/* Creo la checkbox per selezionare la cifratura */
		encryptionCB = new JCheckBox("Encryption");
		encryptionCB.addItemListener(new enableEncryption());
		encryptionPanel.add(encryptionCB);
		/* Creo il campo di testo dove poter inserire la chiave di cifratura */
		password = new JTextField();
		password.setEnabled(false);
		password.setColumns(15);
		encryptionPanel.add(password);

		/* Creo un pannello diviso in due per contenere le due opzioni */
		JPanel otherOptions = new JPanel();
		otherOptions.add(compressionPanel);
		otherOptions.add(encryptionPanel);

		/*
		 * Aggiungo il pannello disponendo gli elementi su un'unica riga e setto il
		 * layout
		 */
		GridBagConstraints gbc_otherOptions = new GridBagConstraints();
		gbc_otherOptions.anchor = GridBagConstraints.WEST;
		gbc_otherOptions.insets = new Insets(0, 0, 5, 5);
		gbc_otherOptions.gridwidth = 3;
		gbc_otherOptions.gridx = 0;
		gbc_otherOptions.gridy = 3;
		add(otherOptions, gbc_otherOptions);

		// Pulsante con immagine aggiungi

		/* Creo il pulsante con l'icona e setto il layout */
		JButton addB = new JButton();
		try {
			Image imgAddB = ImageIO.read(getClass().getResource("add.png"));
			addB.setIcon(new ImageIcon(imgAddB));
			addB.setBorderPainted(false);
			addB.setContentAreaFilled(false);
		} catch (Exception e) {
			main.printError("add.png icon not found");
		}
		addB.addActionListener(new AddFile());
		GridBagConstraints gbc_addB = new GridBagConstraints();
		gbc_addB.fill = GridBagConstraints.HORIZONTAL;
		gbc_addB.gridheight = 2;
		gbc_addB.insets = new Insets(0, 0, 5, 0);
		gbc_addB.gridx = 3;
		gbc_addB.gridy = 2;
		add(addB, gbc_addB);
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire la selezione del
	 * file sorgente.
	 */
	private class OpenFileChooser implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante Find apre un JFileChooser per la
		 * selezione e setta la scelta nella JTextField.
		 * 
		 * @param e Evento pressione pulsante Find
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfcSource = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfcSource.setDialogTitle("Source file");
			if (jfcSource.showOpenDialog(SplitterPanel.this) == JFileChooser.APPROVE_OPTION)
				sourceFilePath.setText(jfcSource.getSelectedFile().toString());
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire la selezione della
	 * cartella di destinazione.
	 */
	private class OpenFolderChooser implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante Find apre un JFileChooser per la
		 * selezione e setta la scelta nella JTextField. Il JfileChooser &egrave; impostato per
		 * selezionare solo cartelle con
		 * setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY).
		 * 
		 * @param e Evento pressione pulsante Find
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfcDest = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfcDest.setAcceptAllFileFilterUsed(false);
			jfcDest.setDialogTitle("Dest Folder");
			jfcDest.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jfcDest.showOpenDialog(SplitterPanel.this) == JFileChooser.APPROVE_OPTION)
				destFolderPath.setText(jfcDest.getSelectedFile().toString());
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per disabilitare i text field
	 * non selezionati dai due RadioButton DivideIn e DivideBy.
	 */
	private class DisableNotChoice implements ActionListener {
		/**
		 * Metodo che alla pressione di uno dei due radio button disabilita l'altro con
		 * il metodo setEnable(false).
		 * 
		 * @param e Evento pressione radio button
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(divideInRB)) {
				divideInTF.setEnabled(true);
				divideByTF.setEnabled(false);
				divideInTF.setText("");
			} else if (e.getSource().equals(divideByRB)) {
				divideByTF.setEnabled(true);
				divideInTF.setEnabled(false);
				divideByTF.setText("");
			}
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire il pulsante
	 * ADD[immagine (+)] che crea ed aggiunge il job alla coda e filtra gli input.
	 */
	private class AddFile implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante ADD[immagine (+)] raccoglie tutti gli
		 * input dal pannello e controlla, partendo dall'alto, che tutti i campi siano
		 * coerenti. Se tutti i controlli passano allora chiama il metodo
		 * core>QueueJobs.addQueueSplitter() che crea il job dalla factory e lo mette in
		 * coda ed aggiunge il vettore returnato come riga nella tabella.
		 * 
		 * @param e Evento pressione pulsante ADD[immagine (+)]
		 */
		public void actionPerformed(ActionEvent e) {
			if (new File(sourceFilePath.getText()).exists() == false || sourceFilePath.getText().equals(""))
				main.printError("Please insert a valid file path");
			else if (Files.notExists(Paths.get(destFolderPath.getText())) || destFolderPath.getText().equals(""))
				main.printError("Please insert a valid folder path");
			else if (!((encryptionCB.isSelected() && password.getText().length() >= 10)
					|| encryptionCB.isSelected() == false))
				main.printError("Please enter a password of at least 10 characters");
			else if ((divideInTF.getText().equals("") && divideInRB.isSelected()))
				main.printError("Please insert the (DivideIn) split parts number");
			else if (divideByTF.getText().equals("") && divideByRB.isSelected())
				main.printError("Please insert the (DivideBy) split parts number");
			else {
				// pulisce la tabella se i job sono gi&agrave; stati eseguiti
				if (qj.isEnd()) {
					for (int i = 0; i < tableModel.getRowCount(); i++)
						tableModel.removeRow(i);
					main.getJobState().setValue(0);
				}
				if (divideInRB.isSelected())
					tableModel.addRow(qj.addQueueSplitter(sourceFilePath.getText(), destFolderPath.getText(),
							Integer.parseInt(divideInTF.getText()), compressionCB.isSelected(),
							encryptionCB.isSelected(), password.getText()));
				else if (divideByRB.isSelected()) {
					if ((new File(sourceFilePath.getText()).length()) >= (Integer.parseInt(divideByTF.getText())
							* JobFactoryService.getBytesDim((String) selectBytes.getSelectedItem())))
						tableModel.addRow(qj.addQueueSplitter(sourceFilePath.getText(), destFolderPath.getText(),
								Integer.parseInt(divideByTF.getText()), (String) selectBytes.getSelectedItem(),
								compressionCB.isSelected(), encryptionCB.isSelected(), password.getText()));
					else
						main.printError("{DivideBy} size bigger than file size");
				} else
					main.printError("Please select one of two split method");
			}
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per attivare il text field in
	 * caso la cifratura fosse selezionata.
	 */
	private class enableEncryption implements ItemListener {
		/**
		 * Metodo che sincronizza la selezione della combo box cifratura con
		 * l'abilitazione della text field per immettere la password con il metodo
		 * setEnable(true|false).
		 * 
		 * @param e Evento selezione check box
		 */
		public void itemStateChanged(ItemEvent e) {
			password.setEnabled(((JCheckBox) e.getSource()).isSelected());
		}
	}
}
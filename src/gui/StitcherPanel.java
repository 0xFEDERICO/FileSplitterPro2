package gui;

import core.QueueJobs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

/**
 * @author federico
 * Pannello StitcherPanel per interagire con l'utente e raccogliere le informazioni
 * di ricompattazione di un file. Una volta raccolte le informazioni di ricompattazione verr&agrave; creato
 * un job dalla factory ed inserito in coda grazie al metodo core>QueueJobs.addQueueStitcher().
 */
public class StitcherPanel extends JPanel {
	private static final long serialVersionUID = -3225572469667148128L;
	private MainPanel main;
	private QueueJobs qj;
	private DefaultTableModel tableModel;

	private JTextField sourceFilePath, destFolderPath;

	/**
	 * Costruttore della classe StitcherPanel che inizializza gli attributi e crea
	 * tutti i componenti grafici del pannello StitcherPanel usando il
	 * GridBagLayout.
	 * 
	 * @param main       oggetto MainPanel usato per stampare gli errori
	 * @param qj         coda dei job
	 * @param tableModel modello della tabella per aggiornamento dati
	 */
	public StitcherPanel(MainPanel main, QueueJobs qj, DefaultTableModel tableModel) {
		this.tableModel = tableModel;
		this.main = main;
		this.qj = qj;

		/** Imposto il layout del pannello */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 152, 199, 89, 104, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 25, 25, 25 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		// area selezione sorgente / destinazione

		/** Creo l'etichetta "File Sorgente" e setto il layout */
		JLabel lblSourceFile = new JLabel("Source file:");
		GridBagConstraints gbc_lblSourceFile = new GridBagConstraints();
		gbc_lblSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceFile.gridx = 0;
		gbc_lblSourceFile.gridy = 0;
		add(lblSourceFile, gbc_lblSourceFile);

		/**
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

		/** Creo il bottone per aprire il file sorgente e setto il layout */
		JButton openSourceFile = new JButton("Find...");
		openSourceFile.addActionListener(new OpenFirstFile());
		GridBagConstraints gbc_openSourceFile = new GridBagConstraints();
		gbc_openSourceFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_openSourceFile.insets = new Insets(0, 0, 5, 0);
		gbc_openSourceFile.gridx = 3;
		gbc_openSourceFile.gridy = 0;
		add(openSourceFile, gbc_openSourceFile);

		/** Creo l'etichetta "Cartella Destinazione" e setto il layout */
		JLabel lblDestFolder = new JLabel("Dest folder:");
		GridBagConstraints gbc_lblDestFolder = new GridBagConstraints();
		gbc_lblDestFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestFolder.gridx = 0;
		gbc_lblDestFolder.gridy = 1;
		add(lblDestFolder, gbc_lblDestFolder);

		/**
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

		/** Creo il bottone per aprire la cartella di destinazione e setto il layout */
		JButton openDestFolder = new JButton("Find...");
		openDestFolder.addActionListener(new OpenFolderChooser());
		GridBagConstraints gbc_openDestFolder = new GridBagConstraints();
		gbc_openDestFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_openDestFolder.insets = new Insets(0, 0, 5, 0);
		gbc_openDestFolder.gridx = 3;
		gbc_openDestFolder.gridy = 1;
		add(openDestFolder, gbc_openDestFolder);

		// Pulsante con immagine aggiungi

		/** Creo il pulsante con l'icona e setto il layout */
		try {
			Image imgAddB = ImageIO.read(getClass().getResource("add.png"));
			JButton addB = new JButton();
			addB.setIcon(new ImageIcon(imgAddB));
			addB.setBorderPainted(false);
			addB.setFocusPainted(false);
			addB.setContentAreaFilled(false);
			addB.addActionListener(new AddPart());
			GridBagConstraints gbc_addB = new GridBagConstraints();
			gbc_addB.gridwidth = 4;
			gbc_addB.fill = GridBagConstraints.HORIZONTAL;
			gbc_addB.gridheight = 2;
			gbc_addB.insets = new Insets(0, 0, 5, 5);
			gbc_addB.gridx = 0;
			gbc_addB.gridy = 2;
			add(addB, gbc_addB);
		} catch (Exception e) {
			main.printError("add.png icon not found");
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire la selezione del
	 * primo file da unire.
	 */
	private class OpenFirstFile implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante Find apre un JFileChooser per la
		 * selezione e setta la scelta nella JTextField.
		 * 
		 * @param e Evento pressione pulsante Find
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfcSource = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfcSource.setDialogTitle("Source file");
			if (jfcSource.showOpenDialog(StitcherPanel.this) == JFileChooser.APPROVE_OPTION)
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
			if (jfcDest.showOpenDialog(StitcherPanel.this) == JFileChooser.APPROVE_OPTION)
				destFolderPath.setText(jfcDest.getSelectedFile().toString());
		}
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire il pulsante
	 * ADD[immagine (+)] che crea ed aggiunge il job alla coda e filtra gli input.
	 */
	private class AddPart implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante ADD[immagine (+)] raccoglie tutti gli
		 * input dal pannello e controlla, partendo dall'alto, che tutti i campi siano
		 * coerenti. Se tutti i controlli passano allora chiama il metodo
		 * core>QueueJobs.addQueueStitcher() che crea il job dalla factory e lo mette in
		 * coda ed aggiunge il vettore returnato come riga nella tabella.
		 * 
		 * @param e Evento pressione pulsante ADD[immagine (+)]
		 */
		public void actionPerformed(ActionEvent e) {
			if (sourceFilePath.getText().equals("") || new File(sourceFilePath.getText()).exists() == false)
				main.printError("Please insert a valid file path");
			else if (destFolderPath.getText().equals("") || Files.notExists(Paths.get(destFolderPath.getText())))
				main.printError("Please insert a valid folder path");
			else {
				// pulisce la tabella se i job sono gi&agrave; stati eseguiti
				if (qj.isEnd()) {
					for (int i = 0; i < tableModel.getRowCount(); i++)
						tableModel.removeRow(i);
					main.getJobState().setValue(0);
				}

				Vector<Object> v = new Vector<Object>();
				v = qj.addQueueStitcher(sourceFilePath.getText(), destFolderPath.getText());
				if (v != null)
					tableModel.addRow(v);
			}
		}
	}

}
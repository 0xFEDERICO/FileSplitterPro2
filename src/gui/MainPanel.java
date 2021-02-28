package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import core.QueueJobs;

/**
 * @author federico
 * Pannello MainPanel usato come ContentPane principale dell'interfaccia grafica.
 * Il pannello MainPanel raccoglie tutti gli altri pannelli della gui e li dispone
 * nella finestra usando anche un JTabbedPane.
 */
public class MainPanel extends JPanel {
	private static final long serialVersionUID = 4708253356058497950L;
	private QueueJobs qj;
	private JFrame frame;
	private JProgressBar jobState;

	/**
	 * Costruttore della classe MainPanel che inizializza gli attributi e crea tutti
	 * i componenti grafici del pannello MainPanel usando il GridBagLayout.
	 * 
	 * @param frame finestra del programma al quale aggianciare i messaggi di errore
	 */
	public MainPanel(JFrame frame) {
		this.frame = frame;
		this.qj = new QueueJobs(this);

		/** Imposto il layout per il ContentPane come BoxLayout verticale */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		/** Creo i pannelli della gui */
		TablePanel table = new TablePanel(this, qj);
		SplitterPanel splitter = new SplitterPanel(this, qj, table.getTableModel());
		StitcherPanel stitcher = new StitcherPanel(this, qj, table.getTableModel());
		RunallPanel runAll = new RunallPanel(this, qj, table.getTableModel());

		/*
		 * Aggiungo i pannelli Splitter e Stitcher come tab
		 */
		JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanel.add(splitter);
		tabbedPanel.add(stitcher);

		/** ingrandisco i nomi con una JLabel */
		JLabel l = new JLabel("Splitter", SwingConstants.CENTER);
		l.setPreferredSize(new Dimension(250, 30));
		tabbedPanel.setTabComponentAt(0, l);
		l = new JLabel("Stitcher", SwingConstants.CENTER);
		l.setPreferredSize(new Dimension(250, 30));
		tabbedPanel.setTabComponentAt(1, l);

		/** Creo la barra di avanzamento dei job e setto il layout */
		jobState = new JProgressBar();
		jobState.setValue(0);
		jobState.setStringPainted(true);

		/** Aggiungo tutti i pannelli al pannello principale */
		this.add(tabbedPanel);
		this.add(table);
		this.add(jobState);
		this.add(runAll);
	}

	/**
	 * Metodo che permette di stampare la stringa di input come popup di errore.
	 * 
	 * @param err messaggio di errore
	 */
	public void printError(String err) {
		JOptionPane.showMessageDialog(frame, err, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Metodo che permette di recuperare la password dall'utente tramite una piccola
	 * finestra di dialogo.
	 * 
	 * @param file nome del file cifrato che richiede la password
	 * @return password ricavata
	 */
	public String takePassword(String file) {
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(2, 2));
		JLabel passwordLbl = new JLabel("Password for file " + file + ":");
		JPasswordField passwordFld = new JPasswordField();
		userPanel.add(passwordLbl);
		userPanel.add(passwordFld);
		int input = JOptionPane.showConfirmDialog(frame, userPanel, "Enter your password:",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (input == 0)
			return String.valueOf(passwordFld.getPassword());
		else {
			return null;
		}
	}

	/**
	 * Getter della barra di avanzamento dei job completati.
	 * 
	 * @return barra di avanzamento
	 */
	public JProgressBar getJobState() {
		return jobState;
	}
}
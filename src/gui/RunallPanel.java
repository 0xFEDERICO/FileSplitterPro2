package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import core.QueueJobs;

/**
 * @author federico
 * Pannello RunallPanel contenente il bottone per avviare tutti i job.
 */
public class RunallPanel extends JPanel {
	private static final long serialVersionUID = 625660702987996642L;
	private JButton start;
	private QueueJobs qj;
	private DefaultTableModel tableModel;
	private MainPanel main;

	/**
	 * Costruttore della classe RunallPanel che inizializza gli attributi e crea il
	 * bottone Start
	 * 
	 * @param main       oggetto MainPanel usato per stampare gli errori
	 * @param qj         coda dei job
	 * @param tableModel modello della tabella per aggiornamento dati
	 */
	public RunallPanel(MainPanel main, QueueJobs qj, DefaultTableModel tableModel) {
		this.qj = qj;
		this.tableModel = tableModel;
		this.main = main;

		start = new JButton("Start");
		start.addActionListener(new Avvio());
		this.add(start);
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire la pressione del
	 * pulsante Start.
	 */
	private class Avvio implements ActionListener {
		/**
		 * Metodo che alla pressione del pulsante Start resetta la progress bar,
		 * disabilita il bottone Start, avvia tutti i job e riabilita il bottone Start.
		 * 
		 * @param e Evento pressione pulsante Start
		 */
		public void actionPerformed(ActionEvent e) {
			main.getJobState().setValue(0);
			start.setEnabled(false);
			qj.startAll(tableModel);
			start.setEnabled(true);
			qj.setEnd(true);
		}
	}
}
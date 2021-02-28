package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import core.ISplitter;
import core.QueueJobs;
import services.JobFactoryService;

/**
 * @author federico
 * Pannello TablePanel per mostrare graficamente il contenuto della coda dei job e della barra di avanzamento.
 * Inoltre interagisce con l'utente in caso di modifica di un job splitter. Una volta raccolti i dati da
 * modificare di un job splitter verr&agrave; ricreato lo stesso job dalla factory ma con il dato modificato ed inserito in
 * coda nella stessa posizione grazie al metodo core>QueueJobs.addQueueStitcherEdit().
 */
public class TablePanel extends JPanel {
	private static final long serialVersionUID = -4132585684525492535L;
	private MainPanel main;
	private QueueJobs qj;
	private JTable queueJobsTable;
	private DefaultTableModel tableModel;

	/**
	 * Costruttore della classe TablePanel che inizializza gli attributi e crea
	 * tutti i componenti grafici del pannello TablePanel usando il GridBagLayout.
	 * 
	 * @param main oggetto MainPanel usato per stampare gli errori
	 * @param qj   coda dei job
	 */
	public TablePanel(MainPanel main, QueueJobs qj) {
		this.main = main;
		this.qj = qj;

		/** Imposto il layout del pannello */
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 152, 199, 89, 104, 0 };
		gridBagLayout.rowHeights = new int[] {};
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] {};
		setLayout(gridBagLayout);

		/** Creo la rappresentazione grafica della coda dei jobs e setto il layout */
		tableModel = new DefaultTableModel() {
			static final long serialVersionUID = 4740116692894598177L;

			/**
			 * Metodo per identificare quali celle sono modificabili nella tabella. Le celle
			 * che identificano un file da dividere sono tutte editabili tranne la prima che
			 * lo identifica come "File" appunto. Le celle che identificano un file da unire
			 * non sono editabili perche&grave; i dati sono gi&agrave; stati raccolti dall'Header e
			 * perderebbe di coerenza questa modifica.
			 * 
			 * @param e Evento pressione pulsante Find
			 */
			public boolean isCellEditable(int row, int column) {
				if (getValueAt(row, 0) == "Parts" && column != (getColumnCount() - 1))
					return false;
				else if (column == 0)
					return false;
				else
					return true;
			}
		};
		tableModel.addColumn("Type");
		tableModel.addColumn("SourceFile");
		tableModel.addColumn("DestFolder");
		tableModel.addColumn("Parts");
		tableModel.addColumn("Compression");
		tableModel.addColumn("Encryption");
		tableModel.addColumn("Remove");
		tableModel.addTableModelListener(new TableChange());
		queueJobsTable = new JTable(tableModel);
		queueJobsTable.setPreferredScrollableViewportSize(new Dimension(450, 150));
		queueJobsTable.setRowHeight(25);
		/** Colonna di pulsanti per cancellare il job dalla coda e dalla tabella */
		new DelJob(queueJobsTable, queueJobsTable.getColumnCount() - 1);
		JScrollPane scrollQueueJobs = new JScrollPane(queueJobsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollQueueJobs.setViewportBorder(null);
		GridBagConstraints gbc_scrollQueueJobs = new GridBagConstraints();
		gbc_scrollQueueJobs.insets = new Insets(0, 0, 5, 0);
		gbc_scrollQueueJobs.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollQueueJobs.gridwidth = 4;
		gbc_scrollQueueJobs.gridx = 0;
		gbc_scrollQueueJobs.gridy = 0;
		add(scrollQueueJobs, gbc_scrollQueueJobs);
	}

	/**
	 * Getter del table model
	 * 
	 * @return table model
	 */
	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	/**
	 * Classe annidata non accessibile dall'esterno per gestire il tasto
	 * DEL[immagine (X)] per rimuovere il job dalla coda.
	 */
	protected class DelJob extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
		private static final long serialVersionUID = -8690166638908750932L;
		JTable table;
		JButton renderButton;
		JButton editButton;
		String text;
		int delRow;

		/**
		 * Costruttore della classe DelJob che inizializza gli attributi e crea un nuovo
		 * modello di rappresentazione dei pulsanti nella colonna.
		 * 
		 * @param table  la tabella
		 * @param column la colonna da modificare
		 */
		public DelJob(JTable table, int column) {
			super();
			this.table = table;
			renderButton = new JButton();

			editButton = new JButton();
			editButton.setFocusPainted(false);
			editButton.addActionListener(this);

			TableColumnModel columnModel = this.table.getColumnModel();
			columnModel.getColumn(column).setCellRenderer(this);
			columnModel.getColumn(column).setCellEditor(this);
		}

		/**
		 * Metodo per renderizzare le singole celle della colonna. In questo caso viene
		 * usato per visualizzare un bottone come un pulsante di cancellatura con una X.
		 * 
		 * @param table      la JTable che chiede al renderer di disegnare
		 * @param value      il valore della cella da renderizzare
		 * @param isSelected true se la cella deve essere renderizzata con la selezione
		 *                   evidenziata; altrimenti false
		 * @param hasFocus   se vero, renderizza la cella in modo appropriato
		 * @param row        l'indice di riga della cella che viene disegnata
		 * @param column     l'indice di colonna della cella che viene disegnata
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			this.delRow = row;
			try {
				Image imgAddB = ImageIO.read(getClass().getResource("del.png"));
				renderButton.setIcon(new ImageIcon(imgAddB));
				renderButton.setBorderPainted(false);
				renderButton.setContentAreaFilled(false);
				renderButton.addActionListener(this);
			} catch (IOException e) {
				main.printError("del.png icon not found");
			}
			return renderButton;
		}

		/**
		 * Metodo per modificare il contenuto testuale delle singole celle della
		 * colonna. In questo caso viene usato per mantenere vuoto il testo del
		 * pulsante.
		 * 
		 * @param table      la JTable che chiede al'editor di modificare il contenuto
		 * @param value      il valore testuale della cella da modificare, sar&agrave; sempre
		 *                   vuoto
		 * @param isSelected true se la cella deve essere renderizzata con la selezione
		 *                   evidenziata; altrimenti false
		 * @param row        l'indice di riga della cella che viene modificata
		 * @param column     l'indice di colonna della cella che viene modificata
		 */
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.delRow = row;
			text = (value == null) ? "" : value.toString();
			editButton.setText(text);
			return editButton;
		}

		/**
		 * Getter del valore contenuto nell'editor
		 * 
		 * @return valore editor
		 */
		public Object getCellEditorValue() {
			return text;
		}

		/**
		 * Metodo che alla pressione del pulsante cancella il job sia dalla tabella che
		 * dalla coda dei job
		 * 
		 * @param e Evento pressione pulsante
		 */
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
			qj.delQueueJob(delRow);
			tableModel.removeRow(delRow);
			if (delRow == 0)
				main.getJobState().setValue(0);
		}
	}

	/**
	 * Classe astratta annidata non accessibile dall'esterno per interfacciare i
	 * cambiamenti della tabella con i job nella coda e filtrare gli input non
	 * coerenti
	 */
	private class TableChange implements TableModelListener {
		/**
		 * Metodo per filtrare ed ricreare oggetto in coda a seguito della modifica di
		 * un suo dato. Inizialmente vengono recuperate tutte le informazioni relative
		 * all'oggetto in coda e salvate in una serie di variabili. Poi in base alla
		 * colonna in cui avviene la modifica viene interpretato in modo diverso
		 * l'oggetto valore contenente il dato modificato. In seguito vengono eseguiti
		 * una lunga serie di controlli in base alla colonna (se il controllo fallisce
		 * viene rimesso il valore originario nella cella): - check file/folder exist
		 * per campo file sorgente e cartella destinazione - regex che filtrano le varie
		 * combinazioni di dati inseriti per le parti e bytes - correttezza stringa
		 * booleana per abilitare compressione e disabilitare cifratura - correttezza
		 * lunghezza stringa per abilitare la cifratura Infine viene rimosso il job
		 * dalla coda e ricreato nella stessa posizione con il metodo
		 * core>QueueJobs.addQueueSplitterEdit()
		 * 
		 * @param e evento cella modificata
		 */
		public void tableChanged(TableModelEvent e) {
			if (e.getType() == TableModelEvent.UPDATE) {
				int row = e.getFirstRow();
				int col = e.getColumn();
				boolean edited = false;

				Object val = ((DefaultTableModel) e.getSource()).getValueAt(row, col);

				ISplitter is = qj.getQueueJobs().get(row);
				boolean crypt = is.isCrypted(), compress = is.isCompress();
				long bytes = is.getBytes();
				String sf = is.getSourceFilePath(), df = is.getDestFolderPath(), password = is.getPassword();
				int parts = is.getParts();

				switch (col) {
				case 1:
					if ((new File((String) val)).exists()) {
						edited = true;
						sf = (String) val;
					} else {
						main.printError("Please insert a valid file path");
						((DefaultTableModel) e.getSource()).setValueAt(sf, e.getFirstRow(), e.getColumn());
					}
					break;
				case 2:
					if (Files.exists(Paths.get((String) val))) {
						edited = true;
						df = (String) val;
					} else {
						main.printError("Please insert a valid folder path");
						((DefaultTableModel) e.getSource()).setValueAt(df, e.getFirstRow(), e.getColumn());
					}
					break;
				case 3:
					if (((String) val).matches("^[0-9]{1,3}$")) {
						parts = Integer.parseInt((String) val);
						edited = true;
					} else if (((String) val).matches("^[0-9]{3}[A-Z]{2}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(((String) val).substring(0, 3))
								* JobFactoryService.getBytesDim(((String) val).substring(3, 5)))) {
							parts = Integer.parseInt(((String) val).substring(0, 3));
							bytes = JobFactoryService.getBytesDim(((String) val).substring(3, 5));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else if (((String) val).matches("^[0-9]{2}[A-Z]{2}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(((String) val).substring(0, 2))
								* JobFactoryService.getBytesDim(((String) val).substring(2, 4)))) {
							parts = Integer.parseInt(((String) val).substring(0, 2));
							bytes = JobFactoryService.getBytesDim(((String) val).substring(2, 4));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else if (((String) val).matches("^[0-9]{1}[A-Z]{2}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(String.valueOf(((String) val).charAt(0)))
								* JobFactoryService.getBytesDim(((String) val).substring(1, 3)))) {
							parts = Integer.parseInt(String.valueOf(((String) val).charAt(0)));
							bytes = JobFactoryService.getBytesDim(((String) val).substring(1, 3));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else if (((String) val).matches("^[0-9]{3}[A-Z]{1}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(((String) val).substring(0, 3))
								* JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(3))))) {
							parts = Integer.parseInt(((String) val).substring(0, 3));
							bytes = JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(3)));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else if (((String) val).matches("^[0-9]{2}[A-Z]{1}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(((String) val).substring(0, 2))
								* JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(2))))) {
							parts = Integer.parseInt(((String) val).substring(0, 2));
							bytes = JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(2)));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else if (((String) val).matches("^[0-9]{1}[A-Z]{1}$")) {
						if ((new File(sf).length()) > (Integer.parseInt(String.valueOf(((String) val).charAt(0)))
								* JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(1))))) {
							parts = Integer.parseInt(String.valueOf(((String) val).charAt(0)));
							bytes = JobFactoryService.getBytesDim(String.valueOf(((String) val).charAt(1)));
							edited = true;
						} else {
							main.printError("{DivideBy} size bigger than file size");
							((DefaultTableModel) e.getSource()).setValueAt(
									String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
									e.getColumn());
						}
					} else {
						main.printError("Please insert a valid size");
						((DefaultTableModel) e.getSource()).setValueAt(
								String.valueOf(parts) + JobFactoryService.getDimBytes(bytes), e.getFirstRow(),
								e.getColumn());
					}
					break;
				case 4:
					if (val instanceof Boolean) {
						edited = true;
						compress = (boolean) val;
					} else if (((String) val).equals("true") || ((String) val).equals("True")
							|| ((String) val).equals("TRUE")) {
						edited = true;
						compress = true;
					} else if (((String) val).equals("false") || ((String) val).equals("False")
							|| ((String) val).equals("FALSE")) {
						edited = true;
						compress = false;
					} else {
						main.printError("Please insert a boolean option");
						((DefaultTableModel) e.getSource()).setValueAt(compress, e.getFirstRow(), e.getColumn());
					}
					break;
				case 5:
					if (val instanceof Boolean) {
						edited = true;
						crypt = (boolean) val;
					} else if (((String) val).equals("false") || ((String) val).equals("False")
							|| ((String) val).equals("FALSE")) {
						edited = true;
						crypt = false;
					} else if (((String) val).length() >= 10) {
						edited = true;
						crypt = true;
						password = (String) val;
					} else {
						main.printError("Please enter a password of at least 10 characters");
						if (is.isCrypted())
							((DefaultTableModel) e.getSource()).setValueAt(password, e.getFirstRow(), e.getColumn());
						else
							((DefaultTableModel) e.getSource()).setValueAt(crypt, e.getFirstRow(), e.getColumn());
					}
					break;
				default:
					return;
				}
				if (edited) {
					qj.getQueueJobs().remove(row);
					if (bytes == 0)
						qj.addQueueSplitterEdit(row, sf, df, parts, compress, crypt, password);
					else
						qj.addQueueSplitterEdit(row, sf, df, parts, JobFactoryService.getDimBytes(bytes), compress,
								crypt, password);
				}
			}
		}
	}
}

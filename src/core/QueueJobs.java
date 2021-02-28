package core;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import gui.MainPanel;
import services.HeaderService;
import services.JobFactoryService;

/**
 * @author federico
 * Classe contenente tutta la logica della coda dei file da splittare/stitchare.
 * Le funzionalit&agrave; fornite da questa classe permettono di aggiungere, rimuovere, modificare
 * ed avviare tutti i jobs in coda. La coda &egrave; implementata con un oggetto di classe Vector
 * di java.util perch&eacute; &egrave; thread-safe.
 */
public class QueueJobs {
	private Vector<ISplitter> queueJobs = new Vector<ISplitter>();
	private MainPanel main;

	private boolean end = false;

	/**
	 * Costruttore della classe QueueJobs che inizializza l'attributo main.
	 * 
	 * @param main oggetto MainPanel usato per stampare gli errori
	 */
	public QueueJobs(MainPanel main) {
		this.main = main;
	}

	/**
	 * Getter della coda dei job
	 * 
	 * @return coda dei job
	 */
	public Vector<ISplitter> getQueueJobs() {
		return queueJobs;
	}

	/**
	 * Getter dello stato di fine della coda.
	 * 
	 * @return l'esecuzione &egrave; finita?
	 */
	public boolean isEnd() {
		return this.end;
	}

	/**
	 * Setter dello stato di fine della coda.
	 * 
	 * @param end l'esecuzione &egrave; finita?
	 */
	public void setEnd(boolean end) {
		this.end = end;
	}

	/**
	 * Metodo che pulisce la coda quando &egrave; terminata prima dell'aggiunta di un nuovo
	 * job
	 */
	private void checkClear() {
		if (this.end) {
			for (int i = 0; i < queueJobs.size(); i++)
				queueJobs.remove(i);
			this.setEnd(false);
		}
	}

	/**
	 * Metodo di interfaccia per rimuovere un job dalla coda
	 * 
	 * @param del riga dell'elemento da rimuovere dalla coda
	 */
	public void delQueueJob(int del) {
		queueJobs.remove(del);
	}

	/**
	 * Metodo per aggiungere un job alla coda che utilizza la divisione in parti.
	 * Per sapere quale tipo di job dovr&agrave; aggiungere lo recupera dalla factory con
	 * services>JobFactoryService.retrieveJob().
	 * 
	 * @param sf       path file sorgente
	 * @param df       path cartella destinazione
	 * @param parts    numero di parti in cui verr&agrave; splittato il file
	 * @param compress il file verr&agrave; compressato?
	 * @param crypt    il file verr&agrave; cifrato?
	 * @param password se viene cifrato, con quale password?
	 * 
	 * @return vettore contenente i dati da mettere nella tabella {tipo, file
	 *         sorgente, cartella destinazione, parti, compressione, cifratura,
	 *         password}
	 */
	public Vector<Object> addQueueSplitter(String sf, String df, int parts, boolean compress, boolean crypt,
			String password) {
		checkClear();

		// Aggiorno la coda
		queueJobs.add(JobFactoryService.retrieveJob(sf, df, parts, compress, crypt, password, main));

		// Aggiorno la gui
		Vector<Object> v = new Vector<Object>();
		v.add("File");
		v.add(sf);
		v.add(df);
		v.add(parts);
		v.add(compress);

		if (crypt)
			v.add(password);
		else
			v.add(crypt);
		return v;
	}

	/**
	 * Metodo per aggiungere un job alla coda che usa la divisione per una data
	 * dimensione di byte. Per sapere quale tipo di job dovr&agrave; aggiungere lo recupera
	 * dalla factory con services>JobFactoryService.retrieveJob().
	 * 
	 * @param sf       path file sorgente
	 * @param df       path cartella destinazione
	 * @param parts    numero di parti in cui verr&agrave; splittato il file
	 * @param bytes    unit&agrave; di grandezza della dimensione in byte
	 * @param compress il file verr&agrave; compressato?
	 * @param crypt    il file verr&agrave; cifrato?
	 * @param password se viene cifrato, con quale password?
	 * 
	 * @return vettore contenente i dati da mettere nella tabella {tipo, file
	 *         sorgente, cartella destinazione, parti, bytes, compressione,
	 *         cifratura, password}
	 */
	public Vector<Object> addQueueSplitter(String sf, String df, int parts, String bytes, boolean compress,
			boolean crypt, String password) {
		checkClear();

		// Aggiorno la coda
		queueJobs.add(JobFactoryService.retrieveJob(sf, df, parts, bytes, compress, crypt, password, main));

		// Aggiorno la View
		Vector<Object> v = new Vector<Object>();
		v.add("File");
		v.add(sf);
		v.add(df);
		v.add(parts + bytes);
		v.add(compress);

		if (crypt)
			v.add(password);
		else
			v.add(crypt);
		return v;
	}

	/**
	 * Metodo per estrarre i dati dalla parte splittata e per aggiungerla alla
	 * tabella delle parti. Si occupa services>HeaderService.extractDataFirst() di
	 * recuperare il Job dalla factory e di aggiungerlo in coda.
	 * 
	 * @param sf path file sorgente
	 * @param df path cartella destinazione
	 * 
	 * @return vettore contenente i dati da mettere nella tabella {file sorgente,
	 *         cartella destinazione, parti, compressione, cifratura || password}
	 */
	public Vector<Object> addQueueStitcher(String sf, String df) {
		checkClear();

		Object[] eData = null;
		if ((eData = HeaderService.exctractDataFirst(-1, sf, df, main, queueJobs)) != null) {
			Vector<Object> v = new Vector<Object>();
			v.add("Parts");
			v.add(sf);
			v.add(df);
			v.add((int) eData[0] + " of " + (int) eData[1]);
			v.add((boolean) eData[2]);

			if ((boolean) eData[3]) {
				v.add((boolean) eData[3]);
			} else
				v.add((boolean) eData[3]);
			return v;
		}
		return null;
	}

	/**
	 * Metodo per sovrascrivere un job in coda che utilizza la divisione in parti
	 * con le modifiche raccolte dalla tabella.
	 * 
	 * @param row      posizione del job nella tabella e nella coda
	 * @param sf       path file sorgente
	 * @param df       path cartella destinazione
	 * @param parts    numero di parti in cui verr&agrave; splittato il file
	 * @param compress il file verr&agrave; compressato?
	 * @param crypt    il file verr&agrave; cifrato?
	 * @param password se viene cifrato, con quale password?
	 */
	public void addQueueSplitterEdit(int row, String sf, String df, int parts, boolean compress, boolean crypt,
			String password) {
		// Aggiorno la coda
		queueJobs.insertElementAt(JobFactoryService.retrieveJob(sf, df, parts, compress, crypt, password, main), row);
	}

	/**
	 * Metodo per sovrascrivere un job in coda che utilizza la divisione per una
	 * data dimensione di byte con le modifiche raccolte dalla tabella.
	 * 
	 * @param sf       path file sorgente
	 * @param df       path cartella destinazione
	 * @param parts    numero di parti in cui verr&agrave; splittato il file
	 * @param bytes    unit&agrave; di grandezza della dimensione in byte
	 * @param compress il file verr&agrave; compressato?
	 * @param crypt    il file verr&agrave; cifrato?
	 * @param password se viene cifrato, con quale password?
	 */
	public void addQueueSplitterEdit(int row, String sf, String df, int parts, String bytes, boolean compress,
			boolean crypt, String password) {
		/** Aggiorno la coda */
		queueJobs.insertElementAt(JobFactoryService.retrieveJob(sf, df, parts, bytes, compress, crypt, password, main),
				row);
	}

	/**
	 * Metodo per avviare contemporaneamente tutti i Thread ed aggiornare la
	 * progress bar dei jobs. L'aggioramento della progress bar viene delegato ad
	 * ogni thread che riceve un valore inc(incremento) ed una volta finito il suo
	 * lavoro lo somma. Per sapere quanto un thread deve incrementare la progress
	 * bar recupero il numero di operazioni che deve fare il job con
	 * getExtraOperations() e calcolo l'incremento.
	 * 
	 * @param tableModel modello della tabella per aggiornamento dati
	 */
	public void startAll(DefaultTableModel tableModel) {
		int queueSize = queueJobs.size();
		int extraOperations = 0;
		if (queueSize == 0)
			main.printError("Please add some Jobs");
		else {
			// approssimazione della percentuale
			for (int i = 0; i < queueSize; i++)
				extraOperations += queueJobs.get(i).getExtraOperations();

			// conto anche i passaggi intermedi
			int inc = 100 / (queueSize + extraOperations);

			// creo un thread per ogni job
			Thread allJobs[] = new Thread[queueSize];
			for (int i = 0; i < queueSize; i++) {
				allJobs[i] = new Thread((Runnable) queueJobs.get(i));
				if (i == (queueSize - 1))
					queueJobs.get(i).setInc(inc + (100 % (queueSize + extraOperations)));
				else
					queueJobs.get(i).setInc(inc);
			}

			// avvio tutti i job
			for (int i = 0; i < queueSize; i++)
				allJobs[i].start();
		}
	}
}

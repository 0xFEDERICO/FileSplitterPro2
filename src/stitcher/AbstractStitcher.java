package stitcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import core.ISplitter;
import gui.MainPanel;

/**
 * @author federico
 * Classe astratta per riunire i file che implementa l'interfaccia ISplitter e Runnable.
 * Tutti i figli di questa classe saranno oggetti che potranno entrare nella coda dei jobs.
 */
public abstract class AbstractStitcher implements ISplitter, Runnable {
	private String sourceFilePath, destFolder, name;
	private int part, totParts;
	private int inc;
	protected final long MAX_SIZE = 64 * 1024; // 64KB
	protected MainPanel main;

	/**
	 * Costruttore della classe AbstractStitcher che inizializza gli attributi e
	 * raccoglie le informazioni base sul file passato come input.
	 * 
	 * @param name     nome del file originale
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param part     numero della parte corrente
	 * @param totParts numero delle parti totali della divisione
	 * @param main     oggetto MainPanel usato per stampare gli errori
	 */
	public AbstractStitcher(String name, String sf, String df, int part, int totParts, MainPanel main) {
		this.name = name;
		this.sourceFilePath = sf;
		this.destFolder = df;
		this.part = part;
		this.totParts = totParts;
		this.main = main;
	}

	/**
	 * Getter della dimensione da incrementare nella progress bar una volta
	 * completata l'esecuzione
	 * 
	 * @return valore incremento
	 */
	public int getInc() {
		return inc;
	}

	/**
	 * Setter della dimensione da incrementare nella progress bar una volta
	 * completata l'esecuzione
	 * 
	 * @param inc incremento
	 */
	public void setInc(int inc) {
		this.inc = inc;
	}

	/**
	 * Getter del path del primo file da riunire
	 * 
	 * @return stringa del path assoluto
	 */
	public String getSourceFilePath() {
		return sourceFilePath;
	}

	/**
	 * Getter del path della cartella in cui riunire i file
	 * 
	 * @return stringa del path assoluto
	 */
	public String getDestFolderPath() {
		return destFolder;
	}

	/**
	 * Getter del nome del file originale splittato
	 * 
	 * @return nome originale file
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter del numero della parte nell'ordine di divisione
	 * 
	 * @return numero parte
	 */
	public int getPart() {
		return part;
	}

	/**
	 * Getter del numero totale delle parti in cui &egrave; stato diviso il file
	 * 
	 * @return numero totale parti
	 */
	public int getParts() {
		return totParts;
	}

	/**
	 * Metodo che si occupa di bufferizzare con un buffer di lunghezza MAX_SIZE le
	 * scritture su file.
	 * 
	 * @param bytesPerSplit dimensione dei byte da bufferizzare
	 * @param is            stream da cui leggere i byte
	 * @param os            stream su cui scrivere i byte
	 */
	public void bufferedReadWrite(long bytesPerSplit, InputStream is, OutputStream os) {
		if (bytesPerSplit > MAX_SIZE) {
			long numReads = bytesPerSplit / MAX_SIZE;
			long numRemainingRead = bytesPerSplit % MAX_SIZE;
			for (int y = 0; y < numReads; y++) {
				this.readWrite(is, os, MAX_SIZE);
			}
			if (numRemainingRead > 0) {
				this.readWrite(is, os, numRemainingRead);
			}
		} else {
			this.readWrite(is, os, bytesPerSplit);
		}
	}

	/**
	 * Metodo che legge/scrive una dimensione prefissata di byte su un buffer.
	 * 
	 * @param buffSize dimensione del buffer
	 * @param is       stream da cui leggere i byte
	 * @param os       stream su cui scrivere i byte
	 */
	public void readWrite(InputStream is, OutputStream os, long buffSize) {
		byte[] buff = new byte[(int) buffSize];
		try {
			int nRead = is.read(buff);
			if (nRead > 0) {
				os.write(buff, 0, nRead);
			}
		} catch (IOException e) {
			main.printError("I/O Error");
		}
	}

	/**
	 * Metodo esecutivo dell'oggetto per eseguire come un thread
	 */
	public abstract void run();
}

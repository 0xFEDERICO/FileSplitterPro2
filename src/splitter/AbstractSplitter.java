package splitter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gui.MainPanel;
import core.Header;
import core.ISplitter;

/**
 * @author federico
 * Classe astratta per splittare un file che implementa l'interfaccia ISplitter e Runnable.
 * Tutti i figli di questa classe saranno oggetti che potranno entrare nella coda dei jobs.
 */
public abstract class AbstractSplitter implements ISplitter, Runnable {
	private String sourceFile, destFolder;
	private int inc;
	protected String filePath, fileName;
	protected long fileLen;
	protected Header h;
	protected final long MAX_SIZE = 64 * 1024; // 64KB
	protected MainPanel main;

	/**
	 * Costruttore della classe AbstractSplitter che inizializza gli attributi e
	 * raccoglie le informazioni base sul file passato come input.
	 * 
	 * @param sf   path del file sorgente
	 * @param df   path della cartella di destinazione
	 * @param main oggetto MainPanel usato per stampare gli errori
	 */
	public AbstractSplitter(String sf, String df, MainPanel main) {
		this.sourceFile = sf;
		this.destFolder = df;
		this.main = main;
		File input = new File(sourceFile);
		this.fileLen = input.length();
		this.fileName = input.getName();
		this.filePath = input.getPath();
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
	 * Getter del path del file da dividere
	 * 
	 * @return stringa del path assoluto
	 */
	public String getSourceFilePath() {
		return sourceFile;
	}

	/**
	 * Getter del path della cartella in cui dividere il file
	 * 
	 * @return stringa del path assoluto
	 */
	public String getDestFolderPath() {
		return destFolder;
	}

	/**
	 * Metodo che si occupa di bufferizzare con un buffer di lunghezza MAX_SIZE le
	 * scritture su file.
	 * 
	 * @param bytesPerSplit dimensione dei byte da bufferizzare
	 * @param is            stream da cui leggere i byte
	 * @param os            stream su cui scrivere i byte
	 */
	void bufferedReadWrite(long bytesPerSplit, InputStream is, OutputStream os) {
		if (bytesPerSplit > MAX_SIZE) {
			long numReads = bytesPerSplit / MAX_SIZE;
			long numRemainingRead = bytesPerSplit % MAX_SIZE;
			for (int y = 0; y < numReads; y++) {
				readWrite(is, os, MAX_SIZE);
			}
			if (numRemainingRead > 0) {
				readWrite(is, os, numRemainingRead);
			}
		} else {
			readWrite(is, os, bytesPerSplit);
		}
	}

	/**
	 * Metodo che legge/scrive una dimensione prefissata di byte su un buffer.
	 * 
	 * @param buffSize dimensione del buffer
	 * @param is       stream da cui leggere i byte
	 * @param os       stream su cui scrivere i byte
	 */
	void readWrite(InputStream is, OutputStream os, long buffSize) {
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

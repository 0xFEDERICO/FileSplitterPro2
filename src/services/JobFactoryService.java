package services;

import core.*;
import splitter.*;
import stitcher.*;
import gui.MainPanel;

/**
 * @author federico
 * Classe che espone il servizio di creazione di un oggetto specializzato nel svolgere
 * il compito richiesto dall'utente in base alle scelte che ha fatto nella gui.
 */
public class JobFactoryService {

	/**
	 * Metodo per convertire i {B,KB,MB,GB} in bytes. Lavora nel modo opposto di
	 * {@link #getDimBytes(long)}
	 */
	public static long getBytesDim(String bytes) {
		if (bytes.equals("B"))
			return 1;
		if (bytes.equals("KB"))
			return 1024;
		if (bytes.equals("MB"))
			return 1024 * 1024;
		if (bytes.equals("GB"))
			return 1024 * 1024 * 1024;
		return 0;
	}

	/**
	 * Metodo per convertire i bytes in {B,KB,MB,GB}. Lavora nel modo opposto di
	 * {@link #getBytesDim(String)}
	 */
	public static String getDimBytes(long bytes) {
		if (bytes == 1)
			return "B";
		if (bytes == 1024)
			return "KB";
		if (bytes == 1024 * 1024)
			return "MB";
		if (bytes == 1024 * 1024 * 1024)
			return "GB";
		return "";
	}

	/**
	 * Metodo di creazione di un oggetto specializzato per dividere un file in
	 * parti.
	 * 
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param parts    numero di parti in cui dividere il file
	 * @param compress true se verr&agrave; compressato
	 * @param crypt    true se verr&agrave; cifrato
	 * @param password stringa password se verr&agrave; cifrato
	 * @param main     oggetto MainPanel per stampare gli errori come popup grafico
	 * @return oggetto specializzato che implementa l'interfaccia ISplitter ->
	 *         quindi pu&ograve; andare nella coda dei job
	 */
	public static ISplitter retrieveJob(String sf, String df, int parts, boolean compress, boolean crypt,
			String password, MainPanel main) {
		if (crypt && compress)
			return new SizeSplitterCompressCrypt(sf, df, parts, password, main);
		else if (crypt && !compress)
			return new SizeSplitterCrypt(sf, df, parts, password, main);
		else if (!crypt && compress)
			return new SizeSplitterCompress(sf, df, parts, main);
		else
			return new SizeSplitter(sf, df, parts, main);
	}

	/**
	 * Metodo di creazione di un oggetto specializzato per dividere un file per una
	 * data dimensione di byte.
	 * 
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param parts    numero di parti in cui dividere il file
	 * @param bytes    unit&agrave; di grandezza dei byte
	 * @param compress true se verr&agrave; compressato
	 * @param crypt    true se verr&agrave; cifrato
	 * @param password stringa password se verràagrave;agrave; cifrato
	 * @param main     oggetto MainPanel per stampare gli errori come popup grafico
	 * @return oggetto specializzato che implementa l'interfaccia ISplitter ->
	 *         quindi pu&ograve; andare nella coda dei job
	 */
	public static ISplitter retrieveJob(String sf, String df, int parts, String bytes, boolean compress, boolean crypt,
			String password, MainPanel main) {
		if (crypt && compress)
			return new ChunkSizeSplitterCompressCrypt(sf, df, parts, getBytesDim(bytes), password, main);
		else if (crypt && !compress)
			return new ChunkSizeSplitterCrypt(sf, df, parts, getBytesDim(bytes), password, main);
		else if (!crypt && compress)
			return new ChunkSizeSplitterCompress(sf, df, parts, getBytesDim(bytes), main);
		else
			return new ChunkSizeSplitter(sf, df, parts, getBytesDim(bytes), main);
	}

	/**
	 * Metodo di creazione di un oggetto specializzato per riunire le parti in un
	 * unico file.
	 * 
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param totParts numero di parti in cui dividere il file
	 * @param compress true se verr&agrave; compressato
	 * @param crypt    true se verr&agrave; cifrato
	 * @param main     oggetto MainPanel per stampare gli errori come popup grafico
	 * @return oggetto specializzato che implementa l'interfaccia ISplitter ->
	 *         quindi pu&ograve; andare nella coda dei job
	 */
	public static ISplitter retrieveJob(String name, String sf, String df, int part, int totParts, boolean compress,
			boolean crypt, MainPanel main) {
		if (crypt && compress)
			return new StitcherCompressCrypt(name, sf, df, part, totParts, main);
		else if (crypt && !compress)
			return new StitcherCrypt(name, sf, df, part, totParts, main);
		else if (!crypt && compress)
			return new StitcherCompress(name, sf, df, part, totParts, main);
		else
			return new Stitcher(name, sf, df, part, totParts, main);
	}

}

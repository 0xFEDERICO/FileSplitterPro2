package stitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import gui.MainPanel;

/**
 * @author federico
 * Classe per riunire le parti splittate e compresse in un unico file.
 */
public class StitcherCompress extends Stitcher {

	/**
	 * Costruttore della classe StitcherCompress che inizializza gli attributi.
	 * 
	 * @param name     nome del file originale
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param part     numero della parte
	 * @param totParts numero delle parti totali della divisione
	 * @param main     oggetto MainPanel usato per stampare gli errori
	 */
	public StitcherCompress(String name, String sf, String df, int part, int totParts, MainPanel main) {
		super(name, sf, df, part, totParts, main);
	}

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return 1 -> questo oggetto riunisce le parti e decomprime
	 */
	@Override
	public int getExtraOperations() {
		return 1;
	}

	/**
	 * Metodo esecutivo per ricomporre le parti in un unico file e decomprimerlo.
	 */
	@Override
	public void run() {
		super.run();
		String path = super.getDestFolderPath() + File.separator + super.getName();
		try {
			File input = new File(path);
			FileInputStream fis = new FileInputStream(input);
			FileOutputStream fos = new FileOutputStream(path + ".deCompress");
			GZIPInputStream gis = new GZIPInputStream(fis);

			byte[] buff = new byte[(int) super.MAX_SIZE];
			try {
				int nRead = 0;
				while ((nRead = gis.read(buff)) > 0) {
					fos.write(buff, 0, nRead);
				}
			} catch (IOException e) {
				main.printError("I/O Error");
			}
			gis.close();
			fis.close();
			fos.close();

			new File(path).delete();
			new File(path + ".deCompress").renameTo(new File(path));
		} catch (FileNotFoundException e) {
			super.main.printError("File " + path + " not found");
		} catch (IOException e) {
			super.main.printError("I/O Error Decompress");
		}
		super.main.getJobState().setValue(super.main.getJobState().getValue() + getInc());
	}
}

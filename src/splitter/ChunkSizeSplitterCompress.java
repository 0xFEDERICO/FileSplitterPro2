package splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import core.Header;
import gui.MainPanel;
import services.HeaderService;

/**
 * @author federico
 * Classe per comprimere le singole parti splittate per una data dimensione di byte.
 */
public class ChunkSizeSplitterCompress extends ChunkSizeSplitter {

	/**
	 * Costruttore della classe ChunkSizeSplitterCompress che inizializza gli
	 * attributi.
	 * 
	 * @param sf    path del file sorgente
	 * @param df    path della cartella di destinazione
	 * @param parts parte intera in cui splittare il file
	 * @param bytes unit&agrave; di grandezza in cui splittare il file
	 * @param main  oggetto MainPanel usato per stampare gli errori
	 */
	public ChunkSizeSplitterCompress(String sf, String df, int parts, long bytes, MainPanel main) {
		super(sf, df, parts, bytes, main);
	}

	/**
	 * Getter per sapere se il file verr&agrave; compressato
	 * 
	 * @return true -> in questo oggetto &egrave; abilitata
	 */
	@Override
	public boolean isCompress() {
		return true;
	}

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return 1 -> questo oggetto splitta e comprime
	 */
	@Override
	public int getExtraOperations() {
		return 1;
	}

	/**
	 * Metodo esecutivo per comprimere le parti divise per una data dimensione di
	 * bytes.
	 */
	@Override
	public void run() {
		super.run();
		String path = super.getDestFolderPath() + File.separator + super.fileName;
		int totParts = (int) (super.sourceSize / super.bytesPerSplit);
		for (int i = 1; i <= totParts; i++) {
			try {
				File input = new File(path + "." + i);
				FileInputStream fis_Source = new FileInputStream(input);
				FileOutputStream fos_Dest = new FileOutputStream(path + "." + i + ".gzip");

				long skip = 0;
				if (i == 1) {
					fis_Source.skip(skip = ((long) HeaderService.getHeaderDim(fis_Source, main))); // skippo l'header, i
																									// 4
																									// byte dell'intero
																									// sono
																									// gi&agrave; stati letti
																									// da
																									// getHeaderDim()
					HeaderService.setHeader(new Header(totParts, i, true, false, this.fileName), fos_Dest, main);
					skip += HeaderService.getIntDim();
				}

				GZIPOutputStream gos = new GZIPOutputStream(fos_Dest);
				super.bufferedReadWrite(input.length() - skip, fis_Source, gos);
				fis_Source.close();
				gos.close();
				fos_Dest.close();
			} catch (IOException e) {
				main.printError("I/O Error");
			}
			new File(path + "." + i).delete();
			new File(path + "." + i + ".gzip").renameTo(new File(path + "." + i));
		}
		main.getJobState().setValue(main.getJobState().getValue() + this.getInc());
	}

}

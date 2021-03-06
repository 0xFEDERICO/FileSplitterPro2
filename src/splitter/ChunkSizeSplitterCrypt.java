package splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import core.Header;
import gui.MainPanel;
import services.EncryptService;
import services.HeaderService;

/**
 * @author federico 
 * Classe per cifrare le singole parti splittate per una data
 * dimensione di byte.
 */
public class ChunkSizeSplitterCrypt extends ChunkSizeSplitter {
	private String password;

	/**
	 * Costruttore della classe ChunkSizeSplitterCrypt che inizializza gli
	 * attributi.
	 * 
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param parts    parte intera in cui splittare il file
	 * @param bytes    unit&agrave; di grandezza in cui splittare il file
	 * @param password password di cifratura
	 * @param main     oggetto MainPanel usato per stampare gli errori
	 */
	public ChunkSizeSplitterCrypt(String sf, String df, int parts, long bytes, String password, MainPanel main) {
		super(sf, df, parts, bytes, main);
		this.password = password;
	}

	/**
	 * Getter della password con cui il file verr&agrave; cifrato
	 * 
	 * @return stringa password
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * Getter per sapere se il file verr&agrave; cifrato
	 * 
	 * @return true -> in questo oggetto &egrave; abilitata
	 */
	@Override
	public boolean isCrypted() {
		return true;
	}

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return 1 -> questo oggetto splitta e cifra
	 */
	@Override
	public int getExtraOperations() {
		return 1;
	}

	/**
	 * Metodo esecutivo per cifrare le parti divise per una data dimensione di
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
				FileOutputStream fos_Dest = new FileOutputStream(path + "." + i + ".crypt");

				long skip = 0;
				if (i == 1) {
					fis_Source.skip(skip = ((long) HeaderService.getHeaderDim(fis_Source, main))); // skippo l'header, i
																									// 4 byte
																									// dell'intero sono
																									// gi&agrave; stati letti
																									// da getHeaderDim()
					HeaderService.setHeader(new Header(totParts, i, false, true, this.fileName), fos_Dest, main);
					skip += HeaderService.getIntDim();
				}
				Cipher cipher = EncryptService.getCipherCrypt(password, fos_Dest, main); // ricreato causa CBC con IV
																							// sempre diverso
				CipherOutputStream cos = new CipherOutputStream(fos_Dest, cipher);
				super.bufferedReadWrite(input.length() - skip, fis_Source, cos);
				fis_Source.close();
				cos.flush();
				cos.close();
				fos_Dest.close();
			} catch (IOException e) {
				main.printError("I/O Error");
			}
			new File(path + "." + i).delete();
			new File(path + "." + i + ".crypt").renameTo(new File(path + "." + i));
		}
		main.getJobState().setValue(main.getJobState().getValue() + this.getInc());
	}

}

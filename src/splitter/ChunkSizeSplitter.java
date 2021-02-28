package splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import core.Header;
import gui.MainPanel;
import services.HeaderService;

/**
 * @author federico
 * Classe per splittare un file per una data dimensione di byte.
 */
public class ChunkSizeSplitter extends AbstractSplitter {
	private int parts;
	private long bytes;
	protected int data, header;
	protected long sourceSize;
	protected long bytesPerSplit;
	protected long remainingBytes;

	/**
	 * Costruttore della classe ChunkSizeSplitter che inizializza gli attributi e
	 * stabilisce in quante parti dovr&agrave; essere splittato il file partendo dalla
	 * dimensione di byte.
	 * 
	 * @param sf    path del file sorgente
	 * @param df    path della cartella di destinazione
	 * @param parts parte intera in cui splittare il file
	 * @param bytes unit&agrave; di grandezza in cui splittare il file
	 * @param main  oggetto MainPanel usato per stampare gli errori
	 */
	public ChunkSizeSplitter(String sf, String df, int parts, long bytes, MainPanel main) {
		super(sf, df, main);
		this.parts = parts;
		this.bytes = bytes;
		this.sourceSize = super.fileLen;
		this.bytesPerSplit = (parts * bytes);
		this.remainingBytes = sourceSize % (parts * bytes);
	}

	/**
	 * Getter del numero delle parti in cui dividere il file
	 * 
	 * @return numero parti
	 */
	public int getParts() {
		return parts;
	}

	/**
	 * Getter dell'unit&agrave; di grandezza dei byte in cui dividere il file
	 * 
	 * @return unit&agrave; di grandezza in byte
	 */
	public long getBytes() {
		return bytes;
	}

	/**
	 * Getter della password con cui il file verr&agrave; cifrato
	 * 
	 * @return null -> in questo oggetto non &egrave; presente
	 */
	@Override
	public String getPassword() {
		return null;
	}

	/**
	 * Getter per sapere se il file verr&agrave; compressato
	 * 
	 * @return false -> in questo oggetto non &egrave; abilitata
	 */
	@Override
	public boolean isCompress() {
		return false;
	}

	/**
	 * Getter per sapere se il file verr&agrave; cifrato
	 * 
	 * @return false -> in questo oggetto non &egrave; abilitata
	 */
	@Override
	public boolean isCrypted() {
		return false;
	}

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return 0 -> questo oggetto non fa operazioni extra
	 */
	@Override
	public int getExtraOperations() {
		return 0;
	}

	/**
	 * Metodo esecutivo per la tipologia di divisione per una data dimensione in
	 * byte.
	 */
	@Override
	public void run() {
		try {
			String path = super.getDestFolderPath() + File.separator + super.fileName;
			FileInputStream fis = new FileInputStream(super.filePath);
			int totParts = (int) (this.sourceSize / this.bytesPerSplit);
			for (int i = 1; i <= totParts; i++) {
				FileOutputStream fos = new FileOutputStream(path + "." + i);
				if (i == 1)
					HeaderService.setHeader(new Header(totParts, i, false, false, this.fileName), fos, super.main);
				super.bufferedReadWrite(bytesPerSplit, fis, fos);
				if (remainingBytes > 0 && i == totParts)
					readWrite(fis, fos, remainingBytes);
				fos.close();
			}
			fis.close();
		} catch (IOException e) {
			main.printError("I/O Error");
		}

		main.getJobState().setValue(main.getJobState().getValue() + this.getInc());
	}
}

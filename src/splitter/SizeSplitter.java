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
 * Classe per splittare un file in parti.
 */
public class SizeSplitter extends AbstractSplitter {
	private int parts;
	protected int data, header;
	protected long sourceSize;
	protected long bytesPerSplit;
	protected long remainingBytes;

	/**
	 * Costruttore della classe SizeSplitter che inizializza gli attributi e
	 * stabilisce in quante parti dovr&agrave; essere splittato il file.
	 * 
	 * @param sf    path del file sorgente
	 * @param df    path della cartella di destinazione
	 * @param parts parti in cui splittare il file
	 * @param main  oggetto MainPanel usato per stampare gli errori
	 */
	public SizeSplitter(String sf, String df, int parts, MainPanel main) {
		super(sf, df, main);
		this.parts = parts;
		this.sourceSize = super.fileLen;
		this.bytesPerSplit = sourceSize / parts;
		this.remainingBytes = sourceSize % parts;
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
	 * Getter della password con cui il file verr&agrave; cifrato
	 * 
	 * @return null -> in questo oggetto non &egrave; presente
	 */
	@Override
	public String getPassword() {
		return null;
	}

	/**
	 * Getter dell'unit&agrave; di grandezza dei byte in cui dividere il file
	 * 
	 * @return 0 -> non abilitato in questa tipologia di divisione
	 */
	@Override
	public long getBytes() {
		return 0;
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
	 * Metodo esecutivo per la tipologia di divisione in parti.
	 */
	@Override
	public void run() {
		try {
			String path = super.getDestFolderPath() + File.separator + super.fileName;
			FileInputStream fis = new FileInputStream(super.filePath);
			for (int i = 1; i <= getParts(); i++) {
				FileOutputStream fos = new FileOutputStream(path + "." + i);
				if (i == 1)
					HeaderService.setHeader(new Header(getParts(), i, false, false, this.fileName), fos, main);
				super.bufferedReadWrite(bytesPerSplit, fis, fos);
				if (remainingBytes > 0 && i == getParts())
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

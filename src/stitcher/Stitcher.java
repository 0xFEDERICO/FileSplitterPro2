package stitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import gui.MainPanel;
import services.HeaderService;

/**
 * @author federico
 * Classe per riunire le parti splittate in un unico file.
 */
public class Stitcher extends AbstractStitcher {
	protected String sourcePath;
	protected long stitchPoints[];

	/**
	 * Costruttore della classe Stitcher che inizializza gli attributi e mantiene le
	 * informazioni dell'Header raccolte dalla prima parte passata come input.
	 * 
	 * @param name     nome del file originale
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param part     numero della parte
	 * @param totParts numero delle parti totali della divisione
	 * @param main     oggetto MainPanel usato per stampare gli errori
	 */
	public Stitcher(String name, String sf, String df, int part, int totParts, MainPanel main) {
		super(name, sf, df, part, totParts, main);
		this.sourcePath = (new File(super.getSourceFilePath())).getParent();
		this.stitchPoints = new long[totParts];
	}

	/**
	 * Getter della password con cui il file verr&agrave; cifrato
	 * 
	 * @return null -> in questo oggetto non &egrave; presente
	 */
	public String getPassword() {
		return null;
	}

	/**
	 * Getter del numero delle parti in cui dividere il file
	 * 
	 * @return 0 -> non abilitato in questa tipologia di divisione
	 */
	public long getBytes() {
		return 0;
	}

	/**
	 * Getter per sapere se il file verr&agrave; compressato
	 * 
	 * @return false -> in questo oggetto non &egrave; abilitata
	 */
	public boolean isCompress() {
		return false;
	}

	/**
	 * Getter per sapere se il file verr&agrave; cifrato
	 * 
	 * @return false -> in questo oggetto non &egrave; abilitata
	 */
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
	public int getExtraOperations() {
		return 0;
	}

	/**
	 * Metodo esecutivo per ricomporre le parti in un unico file.
	 */
	@Override
	public void run() {
		String currFile = null;
		String path = super.getDestFolderPath() + File.separator + super.getName();
		try {
			FileOutputStream fos = new FileOutputStream(path);
			for (int i = 1; i <= super.getParts(); i++) {
				currFile = this.sourcePath + File.separator + super.getName() + "." + i;
				File cFile = new File(currFile);
				long fileLen = cFile.length();
				FileInputStream fis = new FileInputStream(cFile);
				if (i == 1) {
					long skipData = -1;
					if ((skipData = HeaderService.getHeaderDim(fis, super.main)) != -1) {
						fis.skip(skipData); // skippo l'header, i 4
											// byte dell'intero sono
											// gi&agrave; stati letti da
											// getHeaderDim()
						super.bufferedReadWrite(fileLen - skipData - HeaderService.getIntDim(), fis, fos);
						stitchPoints[i - 1] = fileLen - skipData - HeaderService.getIntDim();
					}
				} else {
					super.bufferedReadWrite(fileLen, fis, fos);
					stitchPoints[i - 1] = fileLen;
				}
				fis.close();
			}
			fos.close();
		} catch (FileNotFoundException e) {
			super.main.printError("File " + currFile + " not found");
		} catch (IOException e) {
			super.main.printError("I/O Error Stitch");
		}
		super.main.getJobState().setValue(super.main.getJobState().getValue() + getInc());
	}
}

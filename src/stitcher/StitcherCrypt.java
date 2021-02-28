package stitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import gui.MainPanel;
import services.EncryptService;
import services.HeaderService;

/**
 * @author federico
 * Classe per riunire le parti splittate e cifrate in un unico file.
 */
public class StitcherCrypt extends Stitcher {

	/**
	 * Costruttore della classe StitcherCrypt che inizializza gli attributi.
	 * 
	 * @param name     nome del file originale
	 * @param sf       path del file sorgente
	 * @param df       path della cartella di destinazione
	 * @param part     numero della parte
	 * @param totParts numero delle parti totali della divisione
	 * @param main     oggetto MainPanel usato per stampare gli errori
	 */
	public StitcherCrypt(String name, String sf, String df, int part, int totParts, MainPanel main) {
		super(name, sf, df, part, totParts, main);
	}

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return 1 -> questo oggetto riunisce le parti e decifra
	 */
	@Override
	public int getExtraOperations() {
		return 1;
	}

	/**
	 * Metodo esecutivo per ricomporre le parti in un unico file e decifrarlo.
	 */
	@Override
	public void run() {
		super.run();
		String path = super.getDestFolderPath() + File.separator + super.getName();
		String password = null;
		Cipher cipher = null;

		while (password == null)
			password = super.main.takePassword(super.getName());

		try {
			while (cipher == null) {
				FileInputStream fis = new FileInputStream(path);
				for (int i = 0; i < super.getParts(); i++) {
					if ((cipher = EncryptService.getCipherDecrypt(password, fis, main)) != null) {
						FileOutputStream fos = new FileOutputStream(path + ".deCrypted", true);
						CipherOutputStream cos = new CipherOutputStream(fos, cipher);
						super.bufferedReadWrite(
								stitchPoints[i] - HeaderService.getIntDim() - EncryptService.getSaltDim(), fis, cos);
						cos.close();
						fos.close();
					} else {
						password = super.main.takePassword(super.getName());
						break;
					}
				}
				fis.close();
			}
			new File(path).delete();
			new File(path + ".deCrypted").renameTo(new File(path));
		} catch (FileNotFoundException e) {
			super.main.printError("File " + path + " not found");
		} catch (IOException e) {
			super.main.printError("I/O Error Decrypt");
		}
		super.main.getJobState().setValue(super.main.getJobState().getValue() + getInc());
	}
}

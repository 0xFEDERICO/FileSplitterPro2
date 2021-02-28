package services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

import core.Header;
import core.ISplitter;
import gui.MainPanel;

/**
 * @author federico 
 * Classe che espone il servizio di controllo dell'Header per scriverlo all'inizio della
 * prima parte splittata e rileggerlo per capire come comportarsi con quel file.
 */
public class HeaderService {

	/**
	 * Getter della dimensione di un intero in Java
	 * 
	 * @return dimensione intero
	 */
	public static int getIntDim() {
		return Integer.SIZE / 8;
	}

	/**
	 * Metodo per deserializzare l'oggetto Header. Lavora nel modo opposto di
	 * {@link #Header2ByteArray(Header, MainPanel)}
	 * 
	 * @param headerBytes byte array contenente l'array da deserializzare
	 * @param main        oggetto MainPanel per stampare gli errori come popup
	 *                    grafico
	 * @return oggetto Header deserializzato
	 */
	public static Header byteArray2Header(byte[] headerBytes, MainPanel main) {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(headerBytes);
		ObjectInput objectInputStream;
		Header h = null;
		try {
			objectInputStream = new ObjectInputStream(byteInputStream);
			h = (Header) objectInputStream.readObject();
		} catch (IOException e) {
			main.printError("Error I/O");
		} catch (ClassNotFoundException e) {
			main.printError("Header is corrupted");
		}
		return h;
	}

	/**
	 * Metodo per serializzare l'header. Lavora nel modo opposto di
	 * {@link #byteArray2Header(byte[], MainPanel)}
	 * 
	 * @param h    oggetto di tipo Header da serializzare
	 * @param main oggetto MainPanel per stampare gli errori come popup grafico
	 * @return byte array contenente l'oggetto serializzato
	 */
	public static byte[] Header2ByteArray(Header h, MainPanel main) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		byte[] hArr = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(h);
			out.flush();
			hArr = bos.toByteArray();
			bos.close();
		} catch (IOException e) {
			main.printError("Error I/O");
		}
		return hArr;
	}

	/**
	 * Getter della dimensione del byte array contenente l'Header.
	 * 
	 * @return lunghezza array
	 */
	public static int getHeaderDimBytes(byte[] header) {
		return header.length;
	}

	/**
	 * Setter dell'oggetto Header all'inizio di ogni parte del file splittato. Prima
	 * di mettere l'Header per&ograve; viene inserito un intero contenente la dimensione
	 * dell'Header {@link #getHeaderDimBytes(byte[])} che sta per essere scritto e
	 * poi l'oggetto Header serializzato con
	 * {@link #Header2ByteArray(Header, MainPanel)}.
	 * 
	 * @param h    oggetto Header da scrivere nella parte splittata
	 * @param dest stream di output dove scrivere l'Header
	 * @param main oggetto MainPanel per stampare gli errori come popup grafico
	 */
	public static void setHeader(Header h, OutputStream dest, MainPanel main) {
		byte[] hBytes = Header2ByteArray(h, main);
		try {
			dest.write(ByteBuffer.allocate(4).putInt(getHeaderDimBytes(hBytes)).array());
			dest.write(hBytes);
		} catch (IOException e) {
			main.printError("I/O Error");
		}
	}

	/**
	 * Getter della dimensione dell'Header contenuta nell'intero antecedente.
	 * 
	 * @param source stream di input dove leggere l'intero
	 * @param main   oggetto MainPanel per stampare gli errori come popup grafico
	 * @return dimensione in byte dell'header
	 */
	public static int getHeaderDim(InputStream source, MainPanel main) {
		byte[] intBuff = new byte[getIntDim()];
		try {
			if (source.read(intBuff) != -1)
				return ByteBuffer.wrap(intBuff).getInt();
		} catch (IOException e) {
			main.printError("I/O Error");
		}
		main.printError("Header dim not found");
		return 0;
	}

	/**
	 * Getter per leggere l'Header dalla parte splittata. Per sapere la dimensione
	 * dell'Header legge l'intero nel quale &egrave; contenuta con
	 * {@link #getHeaderDim(InputStream, MainPanel)} poi lo deserializza con il
	 * metodo {@link #byteArray2Header(byte[], MainPanel)}.
	 * 
	 * @param source stream di input dove leggere l'Header
	 * @param main   oggetto MainPanel per stampare gli errori come popup grafico
	 * @return Header letto dalla parte splittata
	 */
	public static Header getHeader(InputStream source, MainPanel main) {
		Header h = null;
		try {
			byte[] headerBuff = new byte[getHeaderDim(source, main)];
			if (source.read(headerBuff) != -1)
				h = byteArray2Header(headerBuff, main);
		} catch (IOException e) {
			main.printError("I/O Error");
		}

		return h;
	}

	/**
	 * Metodo identico a {@link #getHeader(InputStream, MainPanel)} ma con la
	 * differenza che si sa gi&agrave; la dimensione dell'Header da leggere.
	 * 
	 * @param source    stream di input dove leggere l'Header
	 * @param dimHeader dimensione dell'oggetto Header
	 * @param main      oggetto MainPanel per stampare gli errori come popup grafico
	 * @return Header letto dalla parte splittata
	 */
	public static Header getHeader(InputStream source, int dimHeader, MainPanel main) {
		Header h = null;
		try {
			byte[] headerBuff = new byte[(int) dimHeader];
			if (source.read(headerBuff) != -1)
				h = byteArray2Header(headerBuff, main);
		} catch (IOException e) {
			main.printError("I/O Error");
		}

		return h;
	}

	/**
	 * Metodo di servizio per core>QueueJobs.addQueueStitcher() che gestisce la
	 * lettura delle informazioni dentro l'Header e l'aggiunta del Job in coda (o in
	 * una posizione row in caso di modifica) creato con services>JobFactoryService.
	 * 
	 * @param row       -1 per aggiungere in coda; >=0 per mettere l'oggetto nella
	 *                  posizione a seguito di una modifica
	 * @param sf        path della parte sorgente
	 * @param df        path della cartella di destinazione dove ricompattare il
	 *                  file
	 * @param main      oggetto MainPanel per stampare gli errori come popup grafico
	 * @param queueJobs coda dei job
	 * @return array contenente le informazioni estratte e da mettere nella tabella
	 */
	public static Object[] exctractDataFirst(int row, String sf, String df, MainPanel main,
			Vector<ISplitter> queueJobs) {
		Header h = null;
		FileInputStream fis_source = null;
		try {
			fis_source = new FileInputStream(sf);
			int dimHeader = getHeaderDim(fis_source, main);
			if (dimHeader != 0) {
				h = getHeader(fis_source, dimHeader, main);
				fis_source.close();
				if (h == null)
					return null;
				else if (h.getPart() != 1) {
					main.printError("Please add the first part!");
					return null;
				} else {
					if (row == -1)
						queueJobs.add(JobFactoryService.retrieveJob(h.getName(), sf, df, h.getPart(), h.getTotParts(),
								h.isCompress(), h.isEncrypt(), main));
					else
						queueJobs.insertElementAt(JobFactoryService.retrieveJob(h.getName(), sf, df, h.getPart(),
								h.getTotParts(), h.isCompress(), h.isEncrypt(), main), row);
					return new Object[] { h.getPart(), h.getTotParts(), h.isCompress(), h.isEncrypt() };
				}
			} else {
				main.printError("Header is corrupted");
				return null;
			}
		} catch (FileNotFoundException e) {
			main.printError("File " + sf + " not found");
		} catch (NegativeArraySizeException e) {
			main.printError("Header is corrupted");
		} catch (IOException e) {
			main.printError("I/O Error");
		}
		return null;
	}
}

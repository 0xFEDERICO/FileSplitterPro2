package core;

/**
 * @author federico
 * Interfaccia contenente gli elementi in comune delle due operazini fondamentali di FileSplitterPro2:
 * divisione e ricompattazione. Se un oggetto rispetta questa interfaccia allora pu&ograve; essere messo nella
 * coda QueueJobs. I metodi Getter {@link #getSourceFilePath()} {@link #getDestFolderPath()} 
 * {@link #getParts()} {@link #getBytes()} {@link #getPassword()} {@link #isCompress()} {@link #isCrypted()}
 * servono per recuperare le informazioni dell'oggetto splitter in coda prima di modificarlo, vedi gui>TableCange.java
 */
public interface ISplitter {

	/**
	 * Metodo omonimo dell'interfaccia Runnable per i thread
	 */
	public void run();

	/**
	 * Getter della dimensione da incrementare nella progress bar una volta
	 * completata l'esecuzione
	 * 
	 * @return valore incremento
	 */
	public int getInc();

	/**
	 * Setter della dimensione da incrementare nella progress bar una volta
	 * completata l'esecuzione
	 * 
	 * @param inc incremento
	 */
	public void setInc(int inc);

	/**
	 * Getter del numero delle operazioni extra che un oggetto specializzato fa nel
	 * calcolo della stima dell'incremento per la progress bar. Es: cifratura = 1,
	 * compressione = 1, compressione+cifratura = 2
	 * 
	 * @return numero di operazioni extra contando anche quelle di divisione e
	 *         ricompattazione.
	 */
	public int getExtraOperations();

	/**
	 * Getter del path del file da dividere
	 * 
	 * @return stringa del path assoluto
	 */
	public String getSourceFilePath();

	/**
	 * Getter del path della cartella in cui dividere il file
	 * 
	 * @return stringa del path assoluto
	 */
	public String getDestFolderPath();

	/**
	 * Getter del numero delle parti in cui dividere il file
	 * 
	 * @return numero parti
	 */
	public int getParts();

	/**
	 * Getter dell'unit&agrave; di grandezza dei byte in cui dividere il file
	 * 
	 * @return unit&agrave; di grandezza in byte
	 */
	public long getBytes();

	/**
	 * Getter della password con cui il file verr&agrave; cifrato
	 * 
	 * @return stringa password
	 */
	public String getPassword();

	/**
	 * Getter per sapere se il file verr&agrave; compressato
	 * 
	 * @return bool compressione
	 */
	public boolean isCompress();

	/**
	 * Getter per sapere se il file verr&agrave; cifrato
	 * 
	 * @return bool cifratura
	 */
	public boolean isCrypted();
}

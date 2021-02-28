package core;

/**
 * @author federico
 * Classe usata per salvare le informazioni di divisione nel primo file splittato come
 * oggetto serializzato. L'oggetto serializzato funge da header prima della
 * parte dati per ottenere immediatamente le informazioni di divisione senza
 * richiedere input all'utente nel pannello stitcher o conservando altri file
 * che spieghino le operazioni fatte.
 */
public class Header implements java.io.Serializable {
	private static final long serialVersionUID = -7720376217206323341L;
	private int totParts, part;
	private boolean compress, encrypt;
	private String name;

	/**
	 * Costruttore della classe Header che inizializza gli attributi contenenti le
	 * informazioni di divisione.
	 * 
	 * @param totParts parti totali divisione indipendentemente dal metodo usato
	 * @param part     parte corrente del file con questo Header
	 * @param compress il file &egrave; stato compresso?
	 * @param encrypt  il file &egrave; stato cifrato?
	 * @param name     nome del file originale
	 */
	public Header(int totParts, int part, boolean compress, boolean encrypt, String name) {
		super();
		this.totParts = totParts;
		this.part = part;
		this.compress = compress;
		this.encrypt = encrypt;
		this.name = name;
	}

	/**
	 * Getter del numero di parti totali
	 * 
	 * @return attributo totParts
	 */
	public int getTotParts() {
		return totParts;
	}

	/**
	 * Setter del numero delle parti totali
	 * 
	 * @param totParts numero di parti da settare
	 */
	public void setTotParts(int totParts) {
		this.totParts = totParts;
	}

	/**
	 * Getter della parte corrente del file con questo Header
	 * 
	 * @return attributo part
	 */
	public int getPart() {
		return part;
	}

	/**
	 * Setter della parte corrente del file con questo Header
	 * 
	 * @param part numero parte da settare
	 */
	public void setPart(int part) {
		this.part = part;
	}

	/**
	 * Getter per sapere se il file &egrave; stato compresso
	 * 
	 * @return attributo compress
	 */
	public boolean isCompress() {
		return compress;
	}

	/**
	 * Setter per impostare se il file &egrave; stato compresso o no
	 * 
	 * @param compress il file &egrave; stato compresso?
	 */
	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	/**
	 * Getter per sapere se il file &egrave; stato cifrato
	 * 
	 * @return attributo encrypt
	 */
	public boolean isEncrypt() {
		return encrypt;
	}

	/**
	 * Setter per impostare se il file &egrave; stato cifrato o no
	 * 
	 * @param encrypt il file &egrave; stato cifrato?
	 */
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 * Getter del nome del file originale
	 * 
	 * @return attributo name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter del nome del file originale
	 * 
	 * @param name nome del file originale
	 */
	public void setName(String name) {
		this.name = name;
	}
}
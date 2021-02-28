package gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author federico
 * Classe figlia di DocumentFilter per gestire gli input numerici della
 * divisione.
 */
public class InputNumberFilter extends DocumentFilter {
	private MainPanel main;

	/**
	 * Costruttore della classe InputNumberFilter che inizializza l'attributo main.
	 * 
	 * @param main oggetto MainPanel usato per stampare gli errori
	 */
	public InputNumberFilter(MainPanel main) {
		this.main = main;
	}

	/**
	 * Modifico il metodo per rispettare la regex di filtro dell'input.
	 * 
	 * @param fb     FilterBypass che pu&ograve; essere utilizzato per mutare il documento
	 * @param offs   Posizione nel documento
	 * @param length Lunghezza del testo da eliminare
	 * @param str    Testo da inserire, null indica nessun testo da inserire
	 * @param a      AttributeSet che indica gli attributi del testo inserito, null
	 *               &egrave; consentito.
	 */
	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) {
		try {
			String text = fb.getDocument().getText(0, fb.getDocument().getLength());
			text += str;
			if (text.matches("^[0-9]{1,3}$") && Integer.parseInt(text) != 0) {
				super.replace(fb, offs, length, str, a);
			} else {
				main.printError("Please insert a positive integer less than 100");
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modifico il metodo per rispettare la regex di filtro dell'input.
	 * 
	 * @param fb     FilterBypass che pu&ograve; essere utilizzato per mutare il documento
	 * @param offs   Posizione nel documento
	 * @param str    Testo da inserire, null indica nessun testo da inserire
	 * @param a      AttributeSet che indica gli attributi del testo inserito, null
	 *               &egrave; consentito.
	 */
	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) {
		try {
			String text = fb.getDocument().getText(0, fb.getDocument().getLength());
			text += str;
			if (text.matches("^[0-9]{1,3}$") && Integer.parseInt(text) != 0) {
				super.insertString(fb, offs, str, a);
			} else {
				main.printError("Please insert a positive integer less than 100");
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}

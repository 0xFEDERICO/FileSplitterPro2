import java.awt.EventQueue;
import javax.swing.JFrame;
import gui.MainPanel;

/**
 * @author federico Classe contenente il main() di FileSplitterPro2 che crea la
 *         finestra principale e setta il MainPanel come ContentPane.
 */
public class MainClass {

	/**
	 * Metto in coda di esecuzione l'avvio dell'interfaccia grafica.
	 * 
	 * @param args non usati perch&eacute; progetto grafico
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame("FileSplitterPro2"); // Creo la finestra ed imposto il titolo
					frame.setLocation(100, 100); // Imposto la posizione della finestra nello schermo
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Evito che la finestra rimanga in background
																			// alla chiusura
					frame.setContentPane(new MainPanel(frame)); // Setto il ContentPane personalizzato
					frame.setResizable(false); // Rimuovo il resize della finestra
					frame.pack(); // Compatto il risultato finale
					frame.setVisible(true); // Rendo la finestra visibile dopo aver aggiunto tutti i componenti
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

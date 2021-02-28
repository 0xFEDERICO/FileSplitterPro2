package services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import gui.MainPanel;

/**
 * @author federico
 * Classe che espone il servizio di cifratura ovvero due funzioni statiche che restituiscono un 
 * cipher per cifrare ed uno per decifrare lo stream di byte.
 */
public class EncryptService {
	private static final int SALT_DIM = 12;

	/**
	 * Metodo privato statico che genera una chiave univoca partendo dalla password
	 * e dal sale.
	 * 
	 * @param password password di cifratura
	 * @param salt     sale dell'algoritmo AES 128 bit
	 */
	private static SecretKey generateSecretKey(String password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // AES-128
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
		return new SecretKeySpec(key, "AES");
	}

	/**
	 * Getter della dimensione del sale
	 * 
	 * @return dimensione sale
	 */
	public static int getSaltDim() {
		return SALT_DIM;
	}

	/**
	 * Metodo privato statico che data una password, genera l'hash md5 e prende i
	 * primi SALT_DIM bytes come sale di cifratura.
	 * 
	 * @param password stringa password su cui calcolare il sale
	 * @return byte array di dimensione SALT_DIM
	 */
	private static byte[] getMd5Salt(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			byte[] salt = new byte[SALT_DIM];
			byte[] hashTextBytes = hashtext.getBytes();
			for (int i = 0; i < SALT_DIM; i++)
				salt[i] = hashTextBytes[i];
			return salt;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Metodo statico che data una password scrive il suo sale nello stream di
	 * output e genera un oggetto cipher per cifrare lo stream di byte.
	 * 
	 * @param password stringa password
	 * @param dest     stream di output dove tenere traccia del sale
	 * @param main     oggetto MainPanel per stampare gli errori come popup grafico
	 * @return oggetto cipher per cifrare
	 */
	public static Cipher getCipherCrypt(String password, OutputStream dest, MainPanel main) {
		Cipher cipher = null;
		try {
			byte[] salt = getMd5Salt(password);
			SecretKey secretKey = generateSecretKey(password, salt);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, salt); // AES-128
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
			ByteBuffer saltBuffer = ByteBuffer.allocate(HeaderService.getIntDim() + salt.length);
			saltBuffer.putInt(salt.length);
			saltBuffer.put(salt);
			dest.write(saltBuffer.array());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
				| InvalidKeySpecException | InvalidKeyException e) {
			main.printError("Cipher Error");
		} catch (IOException e) {
			main.printError("I/O Error");
		}
		return cipher;
	}

	/**
	 * Metodo statico che legge il precedente sale nello stream di input, se lo
	 * trova lo confronta con quello generato partendo dalla password inserita di
	 * decifratura e, se coincidono, genera un oggetto cipher per decifrare lo
	 * stream di byte.
	 * 
	 * @param password stringa password
	 * @param source   stream di output dove leggere il sale
	 * @param main     oggetto MainPanel per stampare gli errori come popup grafico
	 * @return oggetto cipher per cifrare
	 */
	public static Cipher getCipherDecrypt(String password, InputStream source, MainPanel main) {
		Cipher cipher = null;
		try {
			/** Primo intero */
			byte[] intLen = new byte[HeaderService.getIntDim()];
			try {
				source.read(intLen);
			} catch (IOException e) {
				main.printError("I/O Error");
			}

			/** controllo dimensione sale */
			int sl = ByteBuffer.wrap(intLen).getInt();
			if (sl != SALT_DIM)
				main.printError("Salt dimension is wrong");

			/** byte array di (sl) byte --> sale */
			byte[] originalSalt = new byte[sl];
			try {
				source.read(originalSalt);
			} catch (IOException e) {
				main.printError("I/O Error");
			}

			/** nuovo byte array su questa password */
			byte[] newSalt = getMd5Salt(password);

			if (Arrays.equals(originalSalt, newSalt)) {
				SecretKey secretKey = generateSecretKey(password, originalSalt);
				GCMParameterSpec parameterSpec = new GCMParameterSpec(128, originalSalt); // AES-128
				cipher = Cipher.getInstance("AES/GCM/NoPadding");
				cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
			} else {
				main.printError("Wrong password!!!");
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
				| InvalidKeyException | InvalidKeySpecException e) {
			main.printError("Cipher Error");
		}
		return cipher;
	}
}
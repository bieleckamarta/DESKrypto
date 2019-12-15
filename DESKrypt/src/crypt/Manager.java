package crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Manager {
	private File selectedFile;
    private List<Integer> key;
    private byte[] fileData;
    public static List<Integer> bitsArray = new ArrayList<>();
    
    private List<Integer> reverseArray(List<Integer> list) {

        List<Integer> reversedArray = new ArrayList<>();

        for(int i = list.size()-1; i >=0; i--) {
            reversedArray.add(list.get(i));
        }

        return reversedArray;
    }
    
    void setKey(String keyToSet) {
        key = new ArrayList<>();
        key.clear();
        String keyValue = keyToSet;
        byte[] keyBitsValue = keyValue.getBytes();

        List<Integer> tmpKeyBinary = new ArrayList<>();
        for (byte b : keyBitsValue) {
            getBits(b, tmpKeyBinary);
        }

        if (tmpKeyBinary.size() < 64) {
            do {
                tmpKeyBinary.add(1);
            } while (tmpKeyBinary.size() < 64);

        } else if (tmpKeyBinary.size() > 64) {
           do {
               tmpKeyBinary.remove(64);
           } while (tmpKeyBinary.size() > 64);
        }

        key = tmpKeyBinary;
    }
  
    
    void getBits(byte b, List list) {
        List<Integer> tmpList = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            tmpList.add((b & (1 << i)) == 0 ? 0 : 1);
        }
        tmpList = reverseArray(tmpList);
        list.addAll(tmpList);
    }
    
    private static byte[] encodeToByteArray(List<Integer> bits) {
        byte[] results = new byte[(bits.size() + 7) / 8];
        int byteValue = 0;
        int index;
        for (index = 0; index < bits.size(); index++) {

            byteValue = (byteValue << 1) | bits.get(index);

            if (index %8 == 7) {
                results[index / 8] = (byte) byteValue;
            }
        }

        if (index % 8 != 0) {
            results[index / 8] = (byte) ((byte) byteValue << (8 - (index % 8)));
        }

        return results;
    }
    
    public byte[] encrypt1(String keyToSet) {
    	setKey(keyToSet);

        Permutations permutationTables = new Permutations();

        Des encrypt = new Des(bitsArray, key);
        //KEY PERMUTATION
        encrypt.permute(encrypt.getUserKey(), encrypt.getPermutedUserKey(), permutationTables.getKeyPermutationArray());

        //SPLIT KEY INTO TWO ARRAYS
        encrypt.splitIntoTwoParts(encrypt.getPermutedUserKey(), encrypt.getLeftHalfPermutedKey(), encrypt.getRightHalfPermutedKey());

        //MOVE LEFT PART BITES LEFT
        encrypt.shiftLeft(encrypt.getLeftHalfPermutedKey(), permutationTables.getNumberOfShiftsArray());

        //MOVE RIGHT PART BITES LEFT
        encrypt.shiftLeft(encrypt.getRightHalfPermutedKey(), permutationTables.getNumberOfShiftsArray());

        //CONCATENATE TWO PARTS OF SHIFTED KEY
        encrypt.concatenateAndPermuteKeys(encrypt.getConcatenatedAndPermutedKeys(), encrypt.getLeftHalfPermutedKey(), encrypt.getRightHalfPermutedKey(), permutationTables.getConcatenatedKeyPermutationArray());//

        //ENCRYPTING PLAIN TEXT
        while (encrypt.getAllBitesFromFile().size() != 0) {
            encrypt.get64bitesBlock();
            encrypt.getPermutedSixtyFourBitesBlock().clear();
            encrypt.getLeftPlainTextBlock().clear();
            encrypt.getRightHalfPermutedKey().clear();

            //INITIAL PERMUTATION
            encrypt.permute(encrypt.getSixtyFourBitesBlock(), encrypt.getPermutedSixtyFourBitesBlock(), permutationTables.getFirstPermutationArray());
            //SPLIT PERMUTED PLAIN TEXT INTO TWO ARRAYS
            encrypt.splitIntoTwoParts(encrypt.getPermutedSixtyFourBitesBlock(), encrypt.getLeftPlainTextBlock(), encrypt.getRightPlainTextBlock());
            //MAIN ACTION
            encrypt.roundsEncrypt(encrypt.getLeftPlainTextBlock(), encrypt.getRightPlainTextBlock(), encrypt.getConcatenatedAndPermutedKeys(), permutationTables.getExpadedRightBlockOfPlainText());
        }

        //ADD METADATA BLOCK
        encrypt.addMetadataBlock();
        
        return encodeToByteArray(encrypt.getEncryptedText());
    }
    
    public boolean saveToFile1(byte [] text) {
    	JFileChooser fileChooser = new JFileChooser();

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            System.out.println(selectedFile.getPath());

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fileChooser.getSelectedFile().getAbsoluteFile());
                fos.write(text);
                fos.flush();
                fos.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return true; 
        } else return false;
    }
    
    public byte[] decrypt1(String keyToSet) {
    	setKey(keyToSet);

        Permutations permutationTables = new Permutations();
        Des decrypt = new Des(bitsArray, key);

        decrypt.readMetadata();
        //KEY PERMUTATION
        decrypt.permute(decrypt.getUserKey(), decrypt.getPermutedUserKey(), permutationTables.getKeyPermutationArray());

        //SPLIT KEY INTO TWO ARRAYS
        decrypt.splitIntoTwoParts(decrypt.getPermutedUserKey(), decrypt.getLeftHalfPermutedKey(), decrypt.getRightHalfPermutedKey());

        //MOVE LEFT PART BITES LEFT
        decrypt.shiftLeft(decrypt.getLeftHalfPermutedKey(), permutationTables.getNumberOfShiftsArray());

        //MOVE RIGHT PART BITES LEFT
        decrypt.shiftLeft(decrypt.getRightHalfPermutedKey(), permutationTables.getNumberOfShiftsArray());

        //CONCATENATE TWO PARTS OF SHIFTED KEY
        decrypt.concatenateAndPermuteKeys(decrypt.getConcatenatedAndPermutedKeys(), decrypt.getLeftHalfPermutedKey(), decrypt.getRightHalfPermutedKey(), permutationTables.getConcatenatedKeyPermutationArray());//

        //decryptING PLAIN TEXT
        while (decrypt.getAllBitesFromFile().size() != 0) {
            decrypt.get64bitesBlock();
            decrypt.getPermutedSixtyFourBitesBlock().clear();
            decrypt.getLeftPlainTextBlock().clear();
            decrypt.getRightHalfPermutedKey().clear();

            //INITIAL PERMUTATION
            decrypt.permute(decrypt.getSixtyFourBitesBlock(), decrypt.getPermutedSixtyFourBitesBlock(), permutationTables.getFirstPermutationArray());

            //SPLIT PERMUTED PLAIN TEXT INTO TWO ARRAYS
            decrypt.splitIntoTwoParts(decrypt.getPermutedSixtyFourBitesBlock(), decrypt.getLeftPlainTextBlock(), decrypt.getRightPlainTextBlock());

            //MAIN ACTION
            decrypt.roundsDecrypt(decrypt.getLeftPlainTextBlock(), decrypt.getRightPlainTextBlock(), decrypt.getConcatenatedAndPermutedKeys(), permutationTables.getExpadedRightBlockOfPlainText());

        }

        decrypt.removeItems();
        
        return encodeToByteArray(decrypt.getEncryptedText());
    }



    public boolean addFile() {
    	  bitsArray.clear();

          JFileChooser fileChooser = new JFileChooser();

          int returnValue = fileChooser.showOpenDialog(null);

          if (returnValue == JFileChooser.APPROVE_OPTION) {
              selectedFile = fileChooser.getSelectedFile();

              System.out.println(selectedFile.getPath());

              fileData = new byte[(int) selectedFile.length()];

              try {
                  FileInputStream in = new FileInputStream(selectedFile);
                  in.read(fileData);
                  in.close();
              } catch (IOException e1) {
                  e1.printStackTrace();
              }
              for(byte b : fileData) {
                  getBits(b, bitsArray);
              }
              return true;
          } else return false;

          
    }

}



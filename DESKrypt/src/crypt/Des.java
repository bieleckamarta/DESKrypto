package crypt;
import java.util.ArrayList;
import java.util.List;

public class Des {
    private List<Integer> allBitesFromFile = new ArrayList<Integer>();

    //64 bit block of plain text
    private List<Integer> sixtyFourBitesBlock = new ArrayList<Integer>(64);
    private List<Integer> permutedSixtyFourBitesBlock = new ArrayList<Integer>(64);
    private List<Integer> leftPlainTextBlock = new ArrayList<Integer>(32);
    private List<Integer> rightPlainTextBlock = new ArrayList<Integer>(32);
    private int addedBites;

    //KEY
    private List<Integer> userKey = new ArrayList<Integer>(64);
    private List<Integer> permutedUserKey = new ArrayList<Integer>(56);
    private ArrayList<ArrayList<Integer>> leftKeyPart = new ArrayList<ArrayList<Integer>>(17);
    private ArrayList<ArrayList<Integer>> rightKeyPart = new ArrayList<ArrayList<Integer>>(17);
    private ArrayList<ArrayList<Integer>> concatenatedKeys = new ArrayList<ArrayList<Integer>>(56);
    private ArrayList<ArrayList<Integer>> concatenatedAndPermutedKeys = new ArrayList<ArrayList<Integer>>(48);

    //RESAULT
    private List<Integer> encryptedText = new ArrayList<>();

    //PERMUTATION ARRAYS
    Permutations permutationTables = new Permutations();

    //Constructor
    public Des(List<Integer> BlockBites, List<Integer> key) {
        this.allBitesFromFile = BlockBites;
        this.userKey = key;
        System.out.println(this.userKey.size() + " " +this.userKey);
    }

    //getters
    public int getPlainTextSize() {
        return this.allBitesFromFile.size();
    }

    public List<Integer> getAllBitesFromFile() {
        return this.allBitesFromFile;
    }

    public List<Integer> getSixtyFourBitesBlock() {
        return this.sixtyFourBitesBlock;
    }

    public List<Integer> getPermutedSixtyFourBitesBlock() {
        return this.permutedSixtyFourBitesBlock;
    }

    public List<Integer> getUserKey() {
        return this.userKey;
    }

    public List<Integer> getPermutedUserKey() {
        return this.permutedUserKey;
    }

    public List<Integer> getLeftPlainTextBlock(){ return this.leftPlainTextBlock; }

    public List<Integer> getRightPlainTextBlock() { return this.rightPlainTextBlock; }

    public ArrayList<ArrayList<Integer>> getLeftHalfPermutedKey() { return this.leftKeyPart; }

    public ArrayList<ArrayList<Integer>> getRightHalfPermutedKey() { return this.rightKeyPart; }

    public ArrayList<ArrayList<Integer>> getConcatenatedAndPermutedKeys() { return this.concatenatedAndPermutedKeys; }

    public List<Integer> getEncryptedText() { return this.encryptedText; }

    //GET 64 BITES FROM ALL FILE BITES
    public void get64bitesBlock() {
        this.sixtyFourBitesBlock.clear();

        if(this.allBitesFromFile.size() >= 64) {
            for(int i = 0; i < 64; i++)
                this.sixtyFourBitesBlock.add(this.allBitesFromFile.get(i));

            this.deleteBitesFromallBitesFromFile(64);
        }
        else {
            for(int i = 0; i < this.allBitesFromFile.size(); i++)
                this.sixtyFourBitesBlock.add(this.allBitesFromFile.get(i));

            this.addedBites = 64 - this.allBitesFromFile.size();

            for(int i = 0; i < this.addedBites; i++)
                this.sixtyFourBitesBlock.add(1);

            this.deleteBitesFromallBitesFromFile(this.allBitesFromFile.size());
        }
    }

    //DELETE FIRST 64 BITES FROM ALL FILE BITES
    public void deleteBitesFromallBitesFromFile(int bitsQuantity) {
        for (int i = 0; i < bitsQuantity; i++)
            this.allBitesFromFile.remove(0);
    }

    //PERMUTATIONS
    public void permute(List<Integer> bitesToPermutation, List<Integer> permutationResault, Integer[] permutationArray) {
        for(int i = 0; i < permutationArray.length; i++) {
            permutationResault.add(bitesToPermutation.get(permutationArray[i]));
        }
    }

    public void permute(ArrayList<ArrayList<Integer>> bitesToPermutation, ArrayList<ArrayList<Integer>> permutationResault, Integer[] permutationArray) {

        for(int i = 0; i < bitesToPermutation.size(); i++) {
            List<Integer> tmpPermutation = new ArrayList<Integer>();

            for(int j = 0; j < permutationArray.length; j++) {
                tmpPermutation.add(bitesToPermutation.get(i).get(permutationArray[j]));
            }

            permutationResault.add((ArrayList<Integer>) tmpPermutation);
        }
    }

    public void splitIntoTwoParts(List<Integer> bitesToSplit, List<Integer> leftPart, List<Integer> rightPart) {
        leftPart.clear();
        rightPart.clear();

        for(int i = 0; i < bitesToSplit.size(); i++) {
            if(i < bitesToSplit.size()/2) leftPart.add(bitesToSplit.get(i));
            else rightPart.add(bitesToSplit.get(i));
        }
    }

    public void splitIntoTwoParts(List<Integer> permutedBites, ArrayList<ArrayList<Integer>> left, ArrayList<ArrayList<Integer>> right) {
        List<Integer> tmpLeft = new ArrayList<Integer>();
        List<Integer> tmpRight = new ArrayList<Integer>();

        for(int i = 0; i < permutedBites.size(); i++) {
            if(i < permutedBites.size()/2) tmpLeft.add(permutedBites.get(i));
            else tmpRight.add(permutedBites.get(i));
        }

        left.add((ArrayList<Integer>) tmpLeft);
        right.add((ArrayList<Integer>) tmpRight);
    }

    public void shiftLeft(ArrayList<ArrayList<Integer>> halfOfPermutedKey, Integer[] shiftArray) {
        List<Integer> tmp = new ArrayList<Integer>();

        List<Integer> tmpBites = new ArrayList<Integer>();

        List<Integer> previousKey = new ArrayList<Integer>();
        previousKey = halfOfPermutedKey.get(0);

        ArrayList<ArrayList<Integer>> subKeys =  new ArrayList<ArrayList<Integer>>(17);

        int counter = 0;
        do {
            List<Integer> shifted = new ArrayList<Integer>();

            for(int i = 0; i <= 27; i++) {

                if(shiftArray[counter] == 1 && i + 1 == 28) {
                    shifted.add(previousKey.get(0));
                }
                else if(shiftArray[counter] == 1){
                    shifted.add(previousKey.get(i+1));
                }
                else if(shiftArray[counter] == 2 && i + 2 == 28) {
                    shifted.add(previousKey.get(0));
                    shifted.add(previousKey.get(1));
                    break;
                } else if(shiftArray[counter] == 2) {
                    shifted.add(previousKey.get(i + 2));
                }
            }

            halfOfPermutedKey.add((ArrayList<Integer>) shifted);
            previousKey = shifted;
            counter++;
        }while (halfOfPermutedKey.size() < 17);
    }

    private List<Integer> concatenate(List<Integer> leftPart, List<Integer> rightPart) {
        List<Integer> concatenated = new ArrayList<>();

        concatenated.addAll(leftPart);
        concatenated.addAll(rightPart);

        return concatenated;
    }

    public void concatenateAndPermuteKeys(ArrayList<ArrayList<Integer>> concatenatedAndPermutedKeys, ArrayList<ArrayList<Integer>> leftKeyPart, ArrayList<ArrayList<Integer>> rightKeyPart, Integer[] permutatinoArray) {

        for(int i = 0; i < leftKeyPart.size(); i++) {
            List<Integer> tmpConcatenated = new ArrayList<Integer>(64);
            List<Integer> tmpPermuted = new ArrayList<Integer>(48);

            for(int g = 0; g < 28; g++) { tmpConcatenated.add(leftKeyPart.get(i).get(g));}
            for(int g = 0; g < 28; g++) { tmpConcatenated.add(rightKeyPart.get(i).get(g));}

            for (int j = 0; j < permutatinoArray.length; j++) {
                tmpPermuted.add(tmpConcatenated.get(permutatinoArray[j]));
            }
            concatenatedAndPermutedKeys.add((ArrayList<Integer>) tmpPermuted);
        }
    }

    private List<Integer> permute(List<Integer> permutationBites, Integer[] permutationArray) {
        List<Integer> tmp = new ArrayList<Integer>(48);

        for(int i = 0; i < permutationArray.length; i++) {
            tmp.add(permutationBites.get(permutationArray[i]));
        }

        return tmp;
    }

    private int getNumberFromSbox(List<Integer> bites, int startIndex, Integer[] sBox) {

        int row = bites.get(startIndex+5) * 1 + bites.get(startIndex) * 2;
        int column = bites.get(startIndex + 4) * 1 + bites.get(startIndex + 3) * 2 + bites.get(startIndex + 2) * 4 + bites.get(startIndex + 1) * 8;

        ArrayList<ArrayList<Integer>> sBoxInColumnAndRowView = new ArrayList<ArrayList<Integer>>(64);

        for(int i = 0; i < 4; i++) {
            List<Integer> tmpRow = new ArrayList<>();

            for(int j = i*16; j < (i+1) * 16;j++ ) {
                tmpRow.add(sBox[j]);
            }
            sBoxInColumnAndRowView.add((ArrayList<Integer>) tmpRow);
        }

        return sBoxInColumnAndRowView.get(row).get(column);
    }

    private List<Integer> XOR(List<Integer> key, List<Integer> text) {
        List<Integer> xored = new ArrayList<Integer>(48);

        if(key.size() == text.size()) {
            for(int i = 0; i < text.size(); i++)
                xored.add((key.get(i)+text.get(i))%2);
        }

        return xored;
    }

    public List<Integer> f(List<Integer> dataBlock, List<Integer> key, Integer[] permutationArray) {

        List<Integer> tmpDataBlock = new ArrayList<Integer>();
        List<Integer> resault = new ArrayList<Integer>();

        tmpDataBlock = XOR(this.permute(dataBlock, permutationArray), key);

        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 0, permutationTables.getS1()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 6, permutationTables.getS2()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 12, permutationTables.getS3()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 18, permutationTables.getS4()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 24, permutationTables.getS5()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 30, permutationTables.getS6()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 36, permutationTables.getS7()), 4));
        resault.addAll(integarToBinary(this.getNumberFromSbox(tmpDataBlock, 41, permutationTables.getS8()), 4));

        return this.permute(resault, this.permutationTables.getP());
    }

    public void roundsEncrypt(List<Integer> leftPlainTextHalf, List<Integer> rightPlainTextHalf, ArrayList<ArrayList<Integer>> keys, Integer[] permtutationArray) {
        ArrayList<ArrayList<Integer>> leftHalf = new ArrayList<>();
        ArrayList<ArrayList<Integer>> rightHalf = new ArrayList<>();
        List<Integer> tmp = new ArrayList<>();

        leftHalf.add((ArrayList<Integer>) leftPlainTextHalf);
        rightHalf.add((ArrayList<Integer>) rightPlainTextHalf);

        for(int i = 1; i <= 16; i++) {
            leftHalf.add(rightHalf.get(i-1));
            tmp = XOR(leftHalf.get(i-1), f(rightHalf.get(i-1), keys.get(i), permtutationArray));
            rightHalf.add((ArrayList<Integer>) tmp);
        }

        List<Integer> concatenated = new ArrayList<>();
        concatenated.clear();
        concatenated.addAll(concatenate(rightHalf.get(rightHalf.size()-1), leftHalf.get(leftHalf.size()-1)));
        System.out.println(concatenated);
        this.encryptedText.addAll(permute(concatenated, this.permutationTables.getEndPermutationArray()));
    }

    public void roundsDecrypt(List<Integer> leftPlainTextHalf, List<Integer> rightPlainTextHalf, ArrayList<ArrayList<Integer>> keys, Integer[] permtutationArray) {

        ArrayList<ArrayList<Integer>> leftHalf = new ArrayList<>();
        ArrayList<ArrayList<Integer>> rightHalf = new ArrayList<>();
        List<Integer> tmp = new ArrayList<>();

        leftHalf.add((ArrayList<Integer>) leftPlainTextHalf);
        rightHalf.add((ArrayList<Integer>) rightPlainTextHalf);

        for(int i = 1; i <= 16; i++) {
            leftHalf.add(rightHalf.get(i-1));
            tmp = XOR(leftHalf.get(i-1), f(rightHalf.get(i-1), keys.get(17-i), permtutationArray));
            rightHalf.add((ArrayList<Integer>) tmp);
        }

        List<Integer> concatenated = new ArrayList<>();
        concatenated.clear();
        concatenated.addAll(concatenate(rightHalf.get(rightHalf.size()-1), leftHalf.get(leftHalf.size()-1)));
        this.encryptedText.addAll(permute(concatenated, this.permutationTables.getEndPermutationArray()));

        System.out.println(encryptedText.size() + " - size of decrypted plain text");
    }


    //-----------------------------------------------------------------------------------------------------------------

    public void addMetadataBlock() {
        List<Integer> tmpIntegarBites = new ArrayList<>();
        List<Integer> tmpMetadataBlock = new ArrayList<>();
        tmpIntegarBites = this.integarToBinary(8, 8);

        for(int i = 0; i < 56; i++) {
            tmpMetadataBlock.add(1);
        }
        tmpMetadataBlock.addAll(tmpIntegarBites);
        System.out.println(tmpMetadataBlock + " " + tmpMetadataBlock.size());

        encryptedText.addAll(tmpMetadataBlock);
    }

    public void readMetadata() {
        List<Integer> test = new ArrayList<>();
        for(int i = allBitesFromFile.size()-8; i < allBitesFromFile.size(); i++) {
            test.add(allBitesFromFile.get(i));
        }

        this.addedBites = binaryToInteger(test);
        System.out.println(test + " " + this.addedBites);

        for(int i = 0; i < 64; i++) {
            this.allBitesFromFile.remove(this.allBitesFromFile.size()-1);
        }
    }

    public void removeItems() {

        System.out.println(this.addedBites + " - remove items");
        for(int i = 0; i < this.addedBites; i++)
            encryptedText.remove(encryptedText.size()-1);
    }

    private List<Integer> reverseArray(List<Integer> list) {

        List<Integer> reversedArray = new ArrayList<>();

        for(int i = list.size()-1; i >=0; i--) {
            reversedArray.add(list.get(i));
        }

        return reversedArray;
    }

    private int binaryToInteger(List<Integer> bites) {

        int resault =  bites.get(7) * 1 + bites.get(6) * 2 + bites.get(5) * 4 + bites.get(4) * 8 + bites.get(3) * 16 + bites.get(2) * 32 + bites.get(1) * 64 + bites.get(0) * 128;

        return resault;
    }

    private List<Integer> integarToBinary(int number, int howManyBites) {
        List<Integer> resault = new ArrayList<>();

        do {
            if(number % 2 == 0) resault.add(0);
            else resault.add(1);

            number = number/2;

        } while (number >= 1);

        if(howManyBites > resault.size()) {
            for(int i = resault.size(); i < howManyBites; i++)
                resault.add(0);
        }

        return reverseArray(resault);
    }
}
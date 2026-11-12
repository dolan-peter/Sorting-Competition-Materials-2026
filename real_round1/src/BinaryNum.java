public class BinaryNum {
    // String value that gets generated from DataGenerator.java
    public String binaryString;

    // Integer values calculated once (in GroupPipeDream.calculateValues) and stored here,
    // so the comparator never has to look at the characters again
    public int value;
    public int numberOfOnes;
    public int length;

    // constructor to create a BinaryNum object with the given binary string and its associated values
    public BinaryNum(String binaryString, int value, int numberOfOnes, int length) {
        this.binaryString = binaryString;
        this.value = value;
        this.numberOfOnes = numberOfOnes;
        this.length = length;
    }
    
}

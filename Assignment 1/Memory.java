import java.util.Arrays;
public class Memory{
    private int[] memArray;
    public Memory() {
        memArray = new int[1024];
        Arrays.fill( memArray, 0 );
    }
    
    public int getValue(int index) {
        if( index >= memArray.length || index < 0 ) { // out of bounds
            throw new MemIndexOutOfBoundsException( "Program is attempting to read a non-existing memory address: "+index);
        }
        return memArray[index];
    }

    /* Method to set value in memory array at specified index */
    public void setValue(int index, int value) {
        if ( index >= memArray.length || index < 0 ) { // out of bounds
            throw new MemIndexOutOfBoundsException( "Program is attempting to write at the non-existing memory address: "+index);
        }
        memArray[index] = value;
    }

    public int getSize() {
        return memArray.length;
    }
}

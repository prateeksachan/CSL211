import java.util.Arrays;

public class RegisterSet {
    private int[] registers;
    private int[] flags;
    /* Constructor */
    public RegisterSet() {
        registers = new int[16];
        Arrays.fill( registers, 0 );
        flags = new int[2];
        Arrays.fill( flags, 0 );
        registers[14] = 1023;
    } // end constructor
    
    /* Set value of specified register */
    public void setValue(int index, int value) {
        if( index > 15 ) {
            throw new RegisterIllegalArgumentException( "Program attempting to write non-existing register-"+index);
        }
        registers[index] = value;
    }

    /* Get value of specified register */
    public int getValue(int index) {
        if(index < 0 || index > 15) {
            throw new RegisterIllegalArgumentException( "Program attempting to read non-existing register-"+index);
        }
        return registers[index];
    }
    
    /* Set value of specified flag */
    public void setFlag(int index, int value) {
        if(index > 2) {
            throw new RegisterIllegalArgumentException( "Program attempting to write non-existing register-"+index); 
        }
        flags[index] = value;
    }

    /* Get value of specified flag */
    public int getFlag(int index) {
        if(index < 0 || index > 2) {
            throw new RegisterIllegalArgumentException( "Program attempting to read non-existing register-"+index);
        }
        return flags[index];
    }

    /* Reset */
    public void reset(){
        Arrays.fill( registers, 0 );
        Arrays.fill( flags, 0 );
    }
}

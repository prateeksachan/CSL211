public class ALU {
    public static int doOp(int opcode, int op1, int op2) {
        switch(opcode) {
            case 1: // add
                return op1 + op2;
            case 2: // sub
                return op1 - op2;
            case 3: // mul
                return op1 * op2;
            case 4: // div
                return op1 / op2; 
            case 5: // mod
                return op1 % op2;
            case 6: // and
                return op1 & op2;
            case 7: // or
                return op1 | op2;
            case 8: // lsl
                return op1 << op2;
            case 9: // lsr
                return op1 >>> op2;
            case 10: // asr
                return op1 >> op2;
            case 11: // asr
                if (op1==op2)
                    return 0;
                else if (op1>op2)
                    return 1;
                else
                    return -1;
            default: // error
                throw new ALUIllegalArgumentException( "Opcode does not match any existing operation in the ALU op-table." );
        }
    }
}

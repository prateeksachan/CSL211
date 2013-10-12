import java.util.*;
public class CPU {
    private RegisterSet regs = new RegisterSet();
    private Memory mem;
    private String[] pmem;
    private int size;

    public CPU(ArrayList program) {
        size=program.size();
        mem = new Memory();
        pmem = new String[size];
        for(int i=0; i < size; i++) {
            pmem[i]=""+program.get(i);
        }
    }

    public void execute() {
        int mp=getPointer("22", 0); //run main label
        runProgram(mp);
    }

    public void runProgram(int mp) {
        String curr; // current instruction
        int opcode=0; //opcode
        int reg[] = new int[3]; //array for regA, regB/imm, regC/imm
        int instrType; // type of instruction ALU, ALU/C and LOAD STORE, BRANCH, or MISC
        int isEncode=0;
        while(mp<size) {
            curr = pmem[mp];
            mp++;
            curr=curr.trim();
            if (curr=="" || curr.length()==0) { //if encountered blank line, move to next
                continue;
            }
            if (curr.contains("encode")) {
                isEncode=1;
                continue;
            }
            if (isEncode==1) {
                isEncode=0;
                printEncode(curr);
            }
            if (curr.charAt(0)=='.' || curr.charAt(2)=='.') { //if instruction is on the same line as label
                continue;
            }
            String[] token = curr.split(" ");
            if (token[0].charAt(0)=='o' || token[0].charAt(0)=='u' || token[0].charAt(0)=='h') {
                curr="";
                for (int i=1;i<token.length;i++)
                    curr+=token[i]+" ";
                token = curr.split(" ");
            }
            if (token[0].charAt(0)!='.')
                opcode = Integer.parseInt(token[0]);
            for (int i=1;i<token.length;i++) {
                if (token[i].equals("sp")) 
                    token[i] = "r14";
                if (token[i].equals("ra"))
                    token[i] = "r15";
            }

            if (opcode==0) { //do-nothing
            }
            else if (opcode==23) { // print macro
                if (token[1].charAt(0)=='r')
                    System.out.println(regs.getValue(Integer.parseInt(token[1].substring(1))));
            }

            else if (opcode >=1 && opcode <=10) { //means regA, regB, imm OR regA, regB, regC
                reg[0] = Integer.parseInt(token[1].substring(1));
                if (token.length==4) {
                    reg[1] = Integer.parseInt(token[2].substring(1));
                    if (token[3].charAt(0)=='r') {
                        reg[2] = Integer.parseInt(token[3].substring(1));
                        regs.setValue( reg[0], ALU.doOp( opcode, regs.getValue(reg[1]), regs.getValue(reg[2]) ) );
                    } else {
                        reg[2] = convertToDec(token[3]); //convert it to decimal format
                        regs.setValue( reg[0], ALU.doOp( opcode, regs.getValue(reg[1]), reg[2] ) );
                    }
                }
                else if(token.length==3) {
                    if (token[2].charAt(0)=='r') {
                        reg[1] = Integer.parseInt(token[2].substring(1));
                        regs.setValue( reg[0], ALU.doOp( opcode, regs.getValue(reg[0]), regs.getValue(reg[1]) ) );
                    } else {
                        reg[1] = convertToDec(token[2]); //convert it to decimal format
                        regs.setValue( reg[0], ALU.doOp( opcode, regs.getValue(reg[0]), reg[1] ) );
                    }
                }
            }

            else if (opcode<14) { //means regA, lit OR regA, regB
                reg[0] = Integer.parseInt(token[1].substring(1));
                if (token[2].charAt(0)=='r') {
                    reg[1] = regs.getValue(Integer.parseInt(token[2].substring(1)));
                } else {
                    reg[1] = convertToDec(token[2]);
                }
                if (opcode == 11) { //cmp
                    int x=ALU.doOp(opcode, regs.getValue(reg[0]), reg[1]);
                    if (x==0) { //if-equal
                        regs.setFlag(0,1);
                        regs.setFlag(1,0);
                    } else if (x==1) { //if-greater
                        regs.setFlag(0,0);
                        regs.setFlag(1,1);
                    } else { //if-lesser
                        regs.setFlag(0,0);
                        regs.setFlag(1,0);
                    }
                } else if (opcode == 12) { //mov
                    regs.setValue(reg[0], reg[1]);
                } else if (opcode == 13) { //not
                    regs.setValue(reg[0], ~reg[1]);
                }
            }

            else if (opcode<16) { //load & set
                reg[0] = Integer.parseInt(token[1].substring(1));
                String[] subToken = token[2].split("\\[");
                int offset=0;
                if (!subToken[0].equals(""))
                    offset=Integer.parseInt(subToken[0]);//final-offset
                else offset=0;
                String[] subToken1 = subToken[1].split("\\]");
                if (subToken1[0].equals("sp"))
                    subToken1[0] = "r14";
                if (subToken1[0].equals("ra"))
                    subToken1[0] = "r15";
                reg[1] = Integer.parseInt(subToken1[0].substring(1));//final-reg
                if (opcode==14) { //ld
                    regs.setValue(reg[0], mem.getValue(offset/4+regs.getValue(reg[1])));
                } else if (opcode==15) { //st
                    mem.setValue(offset/4+regs.getValue(reg[1]), regs.getValue(reg[0]));
                }
            }

            else if (opcode==17) {//beq
                if (regs.getFlag(0)==1) {
                    runProgram(getPointer(token[1], 1));
                    return;
                }
            }

            else if (opcode==18) {//bqt
                if (regs.getFlag(1)==1) {
                    runProgram(getPointer(token[1], 1));
                    return;
                }
            }

            else if (opcode==19) {//b
                runProgram(getPointer(token[1], 1));
                return;
            }

            else if (opcode==20) {//call
                regs.setValue(15, mp);
                runProgram(getPointer(token[1], 1));
                return;
            }

            else if (opcode==21) {//ret
                mp=regs.getValue(15);
            }
        }
    }


    //finds and returns the instruction pointer for label
    public int getPointer(String s, int flag) {
        int ip=0;
        String curr;
        if (flag==0)//main selection
            s="22";
        else
            s+=":";
        while( ip<size ) {
            curr = pmem[ip];
            if (curr.charAt(0) != '0') {
                String[] token = curr.split(" ");
                for (int i=0;i<token.length;i++) {
                    if (token[i].equals(s)) {
                        return ip+1;
                    }
                }
            }
            ip++;
        }
        return -1;
    }

    //take a lit and convert it to Dec
    public int convertToDec(String s) {
        int num=0;
        if (s.length()>1) {
            if (s.charAt(0)=='0' && s.charAt(1)=='x')
                num = hexToDec(s.substring(2));
            else if (s.charAt(0)=='0')
                num = octToDec(s.substring(1));
            else
                num = Integer.parseInt(s);
        }
        else num = Integer.parseInt(s);
        return num;
    }

    //convert a Hex num to Dec
    public int hexToDec(String s) {
        int k=0;
        int sum=0;
        int x=0;
        for (int i=s.length()-1; i>=0;i--) {
            switch(s.charAt(i)) {
                case 'A':
                    x=10;
                    break;
                case 'B':
                    x=11;
                    break;
                case 'C':
                    x=12;
                    break;
                case 'D':
                    x=13;
                    break;
                case 'E':
                    x=14;
                    break;
                case 'F':
                    x=15;
                    break;
                default:
                    x=Integer.parseInt(""+s.charAt(i));
                    break;
            }
            sum+=x*Math.pow(16,k);
            k++;
        }
        return sum;
    }

    //convert an Oct num to Dec
    public int octToDec(String s) {
        int k=0;
        int sum=0;
        int x=0;
        for (int i=s.length()-1; i>=0;i--) {
            x=Integer.parseInt(""+s.charAt(i));
            sum+=x*Math.pow(8,k);
            k++;
        }
        return sum;
    }

    public static void printEncode(String a) {
        a = a.replaceAll(":", " ");
        a = a.replaceAll("\\s+", " ");  
        a = a.replaceAll("sp", "r14");  
        a = a.replaceAll("ra", "r15");  
        String tokens[] = a.split(" ");
        int size = tokens.length;
        for (int i=0;i<size;i++) {
        }
        String a1="";
        a1 = getBinaryOpcode(tokens[1]);
        String regA = "";
        String regB = "";
        String regC = "";
        int regs=0;
        String I="0";
        String modifier = "00";
        for (int i=2;i<size;i++) { //check if 2regs+1 imm or 3 regs+0 imm
            if (tokens[i].equals("sp"))
                tokens[i]="r14";
            if (tokens[i].equals("ra"))
                tokens[i]="r15";
        }
        for (int i=2;i<size;i++) { //check if 2regs+1 imm or 3 regs+0 imm
            if (tokens[i].charAt(0)=='r')
                regs++;
        }
        if (size == 5) { //3-immediate
            regA = changeToBinary(Integer.parseInt(tokens[2].substring(1,tokens[2].length())), 1); //reg1
            regB = changeToBinary(Integer.parseInt(tokens[3].substring(1,tokens[3].length())), 1); //reg2
            if (regs==3) { //if there are 3 regs
                regC = changeToBinary(Integer.parseInt(tokens[4].substring(1,tokens[4].length())), 1); //reg3
                System.out.println("0x"+changeToHex(a1+I+regA+regB+regC));
            } else {
                I="1"; //immediate bit
                if (tokens[0].contains("u"))
                    modifier="01";
                else if (tokens[0].contains("h"))
                    modifier="10";
                regC = tokens[4]; //immediate
                if (regC.startsWith("0x"))
                    regC = changeToDecimal(regC, 16); // hex
                else if (regC.startsWith("0"))
                    regC = changeToDecimal(regC, 8);// oct
                else regC = changeToBinary(Integer.parseInt(regC), 0);
                //System.out.println(regC);
                if (regC.length()>16)
                    regC = regC.substring(0,16);
                regC = appendZero(16, regC, 0);
                System.out.println("0x"+changeToHex(a1+I+regA+regB+modifier+regC));
            }
        }

        else if (size == 2) { //nop & ret
           System.out.println("0x"+changeToHex(a1));
        }
        
        else if (size == 4 && (tokens[1].contains("14") || tokens[1].contains("15"))) { //ld & st
            //System.out.println("Y");
            regA = changeToBinary(Integer.parseInt(tokens[2].substring(1,tokens[2].length())), 1); //reg1
            I = "1";
            int temp=0;
            for (int i=0; i<tokens[3].length()-1;i++) {
                if (tokens[3].charAt(i)=='[')
                    break;
                temp++;
            }
            regB =  changeToBinary(Integer.parseInt(tokens[3].substring(temp+2,tokens[3].length()-1)), 1); //reg2
            regC = tokens[3].substring(0,temp);
            if (regC.startsWith("0x"))
                regC = changeToDecimal(regC, 16); // hex
            else if (regC.startsWith("0"))
                regC = changeToDecimal(regC, 8);// oct
            else regC = changeToBinary(Integer.parseInt(regC), 0);
            if (regC.length()>18)
                regC = regC.substring(0,18);
            regC = appendZero(18, regC, 0);
            System.out.println("0x"+changeToHex(a1+I+regA+regB+regC));
        }

        else if (size == 4) { //cmp, not & mov
            regA = changeToBinary(Integer.parseInt(tokens[2].substring(1,tokens[2].length())), 1); //reg1
            if (regs==2) {
                regB = changeToBinary(Integer.parseInt(tokens[3].substring(1,tokens[2].length())), 1); //reg1
                if (tokens[1].contains("11")) {
                    System.out.println("0x"+changeToHex(a1+I+"0000"+regA+regB));
                } else if (tokens[1].contains("12") || tokens[1].contains("13")) {
                    System.out.println("0x"+changeToHex(a1+I+regA+"0000"+regB));
                }
            } else if (regs==1) {
                I = "1";
                if (tokens[0].contains("u"))
                    modifier="01";
                else if (tokens[0].contains("h"))
                    modifier="10";
                regB = tokens[3];
                if (regB.startsWith("0x"))
                    regB = changeToDecimal(regB, 16); // hex
                else if (regB.startsWith("0"))
                    regB = changeToDecimal(regB, 8);// oct
                else regB = changeToBinary(Integer.parseInt(regB), 0);
                if (regB.length()>16)
                    regB = regB.substring(0,16);
                regB = appendZero(16, regB, 0);
                if (tokens[1].contains("11")) {
                    System.out.println("0x"+changeToHex(a1+I+"0000"+regA+modifier+regB));
                } else if (tokens[1].contains("12") || tokens[1].contains("13")) {
                    System.out.println("0x"+changeToHex(a1+I+regA+"0000"+modifier+regB));
                }
            }
        }
    }

    public static String appendZero(int limit, String s, int flag) {
        int l = s.length();
        int diff = limit - l;
        while (diff>0) {
            if (flag==0)
                s="0"+s;
            else s+="0";
            diff--;
        }
        return s;
    }

    public static String changeToHex(String s) {
        String hex = "";
        s = appendZero(32, s, 1);
        for (int i=0; i<s.length();i+=4) {
            int temp=Integer.parseInt(""+s.charAt(i))*8+
                    Integer.parseInt(""+s.charAt(i+1))*4+
                    Integer.parseInt(""+s.charAt(i+2))*2+
                    Integer.parseInt(""+s.charAt(i+3))*1;
            hex+=getHex(temp);
        }
        return hex;
    }

    public static String getHex(int n) {
        switch(n) {
            case 15: return "f";
            case 14: return "e";
            case 13: return "d";
            case 12: return "c";
            case 11: return "b";
            case 10: return "a";
            default: return ""+n;
        }
    }

    public static String changeToBinary(int n, int flag) {
        String s="";
        while (n>=1) {
            s=n%2+s;
            n/=2;
        }
        if (flag==1)
            s = appendZero(4, s, 0);
        return s;
    }

    public static String changeToDecimal(String s, int base) {
        int sum=0;
        int i=s.length();
        int j=0;
        if (base<10) {
            while(i>=1) {
                sum+=Integer.parseInt(""+s.charAt(i-1))*Math.pow(base,j);
                i--;
                j++;
            }
        } else {
            s=s.substring(2,s.length());
            i=i-2;
            while (i>=1) {
                int k=0;
                switch(s.charAt(i-1)) {
                    case 'A': k=10;
                    case 'a': k=10;
                    case 'B': k=11;
                    case 'b': k=11;
                    case 'C': k=12;
                    case 'c': k=12;
                    case 'D': k=13;
                    case 'd': k=13;
                    case 'E': k=14;
                    case 'e': k=14;
                    case 'F': k=15;
                    case 'f': k=15;
                    default: k=Integer.parseInt(""+s.charAt(i-1));
                }
                sum+=k*Math.pow(base,j);
                i--;
                j++;
            }
        }
        return changeToBinary(sum, 0);
    }

    public static String getBinaryOpcode(String a) {
        switch(a) {
            case "1": return "00000";
            case "2": return "00001";
            case "3": return "00010";
            case "4": return "00011";
            case "5": return "00100";
            case "11": return "00101";
            case "6": return "00110";
            case "7": return "00111";
            case "13": return "01000";
            case "12": return "01001";
            case "8": return "01010";
            case "9": return "01011";
            case "10": return "01100";
            case "16": return "01101";
            case "14": return "01110";
            case "15": return "01111";
            case "17": return "10000";
            case "18": return "10001";
            case "19": return "10010";
            case "20": return "10011";
            case "21": return "10100";
            default: return "s";
        }
    }
}

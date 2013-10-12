// file: Parser.java

public class Parser{
    public static String parse(String a) {
        int x=0;
        int y=0;
        a = a.trim();
        a = a.replaceAll(",", " ");
        a = a.replaceAll("\\s+", " ");
        a = a.replaceAll("\\s+\\[\\s+", "[").replaceAll("\\s+\\]","]");
        //System.out.println(a);
        //System.exit(0);
        String[] allToken = a.split(" ");
        int modifier = 0;
        if (allToken.length>1) {
            if (allToken[0].charAt(allToken[0].length()-1) == 'u') {
                allToken[0] = allToken[0].substring(0,allToken[0].length()-1);
                modifier = -1;
            }
            else if (allToken[0].charAt(allToken[0].length()-1) == 'h') {
                allToken[0] = allToken[0].substring(0,allToken[0].length()-1);
                modifier = 1;
            }
        }
        allToken[0] = opcode(allToken[0]);
        a = allToken[0]+" ";
        for (int i=1;i<allToken.length;i++) {
            a+=allToken[i]+" ";
        }
        //System.out.println("this: "+a);
        //check for hex
        String[] tokenArrayHex = a.split(" 0x");
        int n=tokenArrayHex.length;
        int flag=0;
        if (n>1) {
            flag=1;
            for (int i=1;i<n;i++) {
                tokenArrayHex[i] = tokenArrayHex[i].replaceAll(" ","");
            }
            String tokenArrayIniHex[] = tokenArrayHex[0].split(" ");
            a=tokenArrayHex[0]+" 0x";
            //System.out.println("this: "+a);
            for (int i=1;i<n;i++) {
                a+=tokenArrayHex[i];
            }
            //System.out.println("now: "+a);
            String tokenArrayHexMod[] = a.split(" 0x");
            if (tokenArrayHexMod[1].length()>0) {
                a=tokenArrayHexMod[0]+" 0x";
                if (modifier==1) { //h-mod
                    y=0;
                    if (tokenArrayHexMod[1].length()-4<0) {
                        for (int i=0;i<4-tokenArrayHexMod[1].length();i++)
                            a+="0";
                        y=tokenArrayHexMod[1].length();
                    }
                    else y=4;
                    for (int i=0;i<y;i++)
                        a+=tokenArrayHexMod[1].charAt(i);
                    a+="0000";
                    a="h "+a;
                }
                else if (modifier == -1) { //u-mod
                    a+="0000";
                    y=0;
                    if (tokenArrayHexMod[1].length()-4<0) {
                        for (int i=0;i<4-tokenArrayHexMod[1].length();i++)
                            a+="0";
                        y=0;
                    }
                    else y=tokenArrayHexMod[1].length()-4;
                    for (int i=y;i<tokenArrayHexMod[1].length();i++)
                        a+=tokenArrayHexMod[1].charAt(i);
                    a="u "+a;
                } else a+=tokenArrayHexMod[1]; //normal
            }
            return a;
        }

        //check for oct
        String[] tokenArrayOct = a.split(" 0");
        int m=tokenArrayOct.length;
        x=0;
        String oct;
        if (m>1 && flag==0) {
            for (int i=0;i<a.length();i++) {
                if (a.charAt(i)==' ' && a.charAt(i+1)=='0') {
                    x=i+1;
                    break;
                }
            }
            oct = a.substring(x+1, a.length());
            oct = oct.replaceAll(" ","");
            a=tokenArrayOct[0]+" 0";
            if (oct.length()>0) {
                if (modifier==1) { //h-mod
                    y=0;
                    if (oct.length()-4<0) {
                        for (int i=0;i<4-oct.length();i++)
                            a+="0";
                        y=oct.length();
                    }
                    else y=4;
                    for (int i=0;i<y;i++)
                        a+=oct.charAt(i);
                    a+="0000";
                    a="h "+a;
                }
                else if (modifier == -1) { //u-mod
                    a+="0000";
                    y=0;
                    if (oct.length()-4<0) {
                        for (int i=0;i<4-oct.length();i++)
                            a+="0";
                        y=0;
                    }
                    else y=oct.length()-4;
                    for (int i=y;i<oct.length();i++)
                        a+=oct.charAt(i);
                    a="u "+a;
                } else a+=oct; //normal
            }
            else a+=oct;
            //System.out.println(a);
            //System.exit(0);
            return a;
        }

        a=allToken[0]+" ";
        for (int i=1;i<allToken.length;i++) {
            a+=allToken[i]+" ";
        }
        return "o "+a;
    }
    
    public static String opcode(String s) {
        switch (s) {
            case "add": return "1";
            case "sub": return "2";
            case "mul": return "3";
            case "div": return "4";
            case "mod": return "5";
            case "and" : return "6";
            case "or": return "7";
            case "lsl": return "8";
            case "lsr": return "9";
            case "asr": return "10";
            case "cmp": return "11";
            case "mov": return "12";
            case "not": return "13";
            case "ld" : return "14";
            case "st" : return "15";
            case "nop" : return "16";
            case "beq" : return "17";
            case "bgt" : return "18";
            case "b" : return "19";
            case "call" : return "20";
            case "ret" : return "21";
            case ".main:" : return "22";
            case ".print" : return "23";
            default: return s;        
        }
    }
}

// This class holds helper functions for the calculator

public class Utils {

    // returns if the string is a number or A-F
    public static boolean isNum(String str) {
        if (str != null && !str.equals("") && ((str.charAt(0) >= 48 && str.charAt(0) <= 57) 
        		|| (str.charAt(0) >= 65 && str.charAt(0) <= 70))) {
            return true;
        } else {
            return false;
        }
    }

    // converts decimal string to binary string
    public static String convertToBinary(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String bin = Long.toBinaryString(decVal);
            return bin;
        } else {
            return "0";
        }
    }

    // converts decimal string to hex string
    public static String convertToHex(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String hex = Long.toHexString(decVal);
            return hex.toUpperCase();
        } else {
            return "0";
        }
    }

    // converts decimal string to octal string
    public static String convertToOct(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String oct = Long.toOctalString(decVal);
            return oct;
        } else {
            return "0";
        }
    }

    // converts binary string to decimal string
    public static String convertFromBinary(String str) {
        // System.out.println("    convertFromBinary str: " + str);
        return String.valueOf(Long.parseLong(str,2));
    }

    // converts hex string to decimal string
    public static String convertFromHex(String str) {
        return String.valueOf(Long.decode("0x"+ str));
    }

    // converts octal string to decimal string
    public static String convertFromOct(String str) {
        return String.valueOf(Long.parseLong(str,8));
    }

    // converts userEnteredTotalString to target base
    public static String convertToType(String str, char base) {
        String tokenizedStr = insertBlanks(str);
        String convertedStr = "";
        System.out.println("    base in convert to type is: " + base);
        for (String token: tokenizedStr.split(" ")) {
            if (isNum(token)) {
                //System.out.println(token + " is a num!");
                if (base == 'b') {
                    convertedStr = convertedStr + convertToBinary(token);
                } else if (base == 'h') {
                    convertedStr = convertedStr + convertToHex(token);
                } else if (base == 'o') {
                    convertedStr = convertedStr + convertToOct(token);
                } else if (base == 'd') {
                    convertedStr = convertedStr + token;
                    System.out.println("    inside else in convert to type");
                }
            } else {
            	convertedStr = convertedStr + token;
            }
        }
        System.out.println("convert to type function outputs: " + convertedStr);
        return convertedStr;
    }

    // converts string from any base to decimal
    public static String convertToDec(String str, char fromBase) {
        String tokenizedStr = insertBlanks(str);
        String convertedStr = "";
        System.out.println("    base in convert to type is: " + fromBase);
        for (String token: tokenizedStr.split(" ")) {
            if (isNum(token)) {
                if (fromBase == 'b') {
                    convertedStr = convertedStr + convertFromBinary(token);
                } else if (fromBase == 'h') {
                	System.out.println("    abouta do convert from hex");
                    convertedStr = convertedStr + convertFromHex(token);
                } else if (fromBase == 'o') {
                    convertedStr = convertedStr + convertFromOct(token);
                } else if (fromBase == 'd') {
                    convertedStr = convertedStr + token;
                    System.out.println("    inside else in convert to type");
                }
            } else {
            	System.out.println("    Convert to dec else");
            	convertedStr = convertedStr + token;
            }
        }
        System.out.println("    convert to decimal outputs: " + convertedStr);
        return convertedStr;
    }

    // inserts blanks around operators in string
    public static String insertBlanks(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')' ||
                    s.charAt(i) == '+' || s.charAt(i) == '-' ||
                    s.charAt(i) == '*' || s.charAt(i) == '/' ||
                    s.charAt(i) == '%') {
                result += " " + s.charAt(i) + " ";
            } else {
                result += s.charAt(i);
            }
        }
        return result;
    } // end insertBlanks

    // return if there are 0 or matched parantheses
    public static boolean checkMatchedOpenClosedParan(String str) {
        int countOpenParan = 0;
        int countClosedParan = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ')') {
                countOpenParan++;
            } else if (str.charAt(i) == '(') {
                countClosedParan++;
            }
        }

        if (countOpenParan == countClosedParan) {
            return true;
        } else {
            return false;
        }
    } // end checkMatchedOpenClosedParan
} // end Utils class
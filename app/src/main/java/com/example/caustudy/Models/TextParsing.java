package com.example.caustudy.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TextParsing {


    public TextParsing() {

    }

    public static List<String> toStringArray(String input) {
        List<String> ret = new ArrayList<String>();
        int head = 0;
        int tail = 0;
        boolean reading = false;
        String buf = "";
        Log.d("origin input",input);
        // 파싱 이상하게됨.
        while (head < input.length()) {
            if ( Character.isDigit(input.charAt(head) ) ) {
                if (reading == false) {
                    reading = true;
                    Log.d("buf",Character.toString((input.charAt(head))));
                    buf = buf.concat(Character.toString(input.charAt(head)));
                } else {
                    buf = buf.concat(Character.toString(input.charAt(head)));
                }
            } else if (reading) {
                ret.add(buf);
                reading = false;
                buf = "";
                while (head<input.length()) {
                    if (input.charAt(head) != ',') {
                        head +=1;
                        break;
                    }
                }
            }
            head += 1;
        }
        return ret;
    }



}

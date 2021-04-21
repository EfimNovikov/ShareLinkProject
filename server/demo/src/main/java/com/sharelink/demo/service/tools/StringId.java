package com.sharelink.demo.service.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class StringId {

    private static final int max_decimals = 4;

    public static String parseStringId(int n){
        char[] tempChars = Integer.toString(n).toCharArray();
        String finalString = Integer.toString(n);
        for (int i = 0; i<max_decimals-tempChars.length;i++){
            finalString = "0"+finalString;
        }
        return finalString;
    }
}

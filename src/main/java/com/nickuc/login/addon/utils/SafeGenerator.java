/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.login.addon.utils;

import com.nickuc.login.addon.Constants;
import org.apache.commons.codec.binary.Base64;

import java.security.SecureRandom;

public class SafeGenerator {

    private static final char[]
            LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray(),
            NUMBERS = "0123456789".toCharArray(),
            SYMBOLS = "^!@#$%&*".toCharArray();

    public static String generatePassword() {
        int length = Constants.DEFAULT_PASSWORD_LENGTH;
        if (length < 6 || length > 15) {
            length = 12;
        }
        return generatePassword(length);
    }

    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        char[] ALL = new char[LETTERS.length + NUMBERS.length + SYMBOLS.length];
        mix(ALL, LETTERS, NUMBERS, SYMBOLS);

        for (int i = 0; i < length; i++) {
            builder.append(ALL[(int)(random.nextDouble() * ALL.length)]);
        }
        for (int i = 0; i < length; i++) {
            char c = builder.charAt(i);

            boolean numbers = false;
            boolean symbols = false;
            for (char number : NUMBERS) {
                if (c == number) {
                    numbers = true;
                    break;
                }
            }
            if (numbers) {
                for (char symbol : SYMBOLS) {
                    if (c == symbol) {
                        symbols = true;
                        break;
                    }
                }
            }
            if (!symbols) {
                int restrictedPos = (int) (Math.random() * length);
                builder.setCharAt(restrictedPos, NUMBERS[(int)(random.nextDouble() * NUMBERS.length)]);
                while (true) {
                    int position = (int) (Math.random() * length);
                    if (position == restrictedPos) {
                        continue;
                    }
                    builder.setCharAt(position, SYMBOLS[(int)(random.nextDouble() * SYMBOLS.length)]);
                    break;
                }
            }
        }
        return builder.toString();
    }

    public static String generateUserCryptKey() {
        int length = Constants.USER_CRYPT_KEY_LENGTH;
        if (length <= 0 || length % 4 != 0) {
            length = 192;
        }

        // 6 bits = 1 char
        int bytesLength = (length * 6) / 8;
        byte[] bytes = new byte[bytesLength];

        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
    }

    private static void mix(char[] buf, char[]... chars) {
        int index = 0;
        for (char[] charArray : chars) {
            for (char c : charArray) {
                buf[index++] = c;
            }
        }
    }

}

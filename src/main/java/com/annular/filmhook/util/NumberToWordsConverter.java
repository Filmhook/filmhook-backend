package com.annular.filmhook.util;

public class NumberToWordsConverter {

    private static final String[] units = {
        "", "One", "Two", "Three", "Four", "Five", "Six",
        "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
        "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] scales = {
        "", "Thousand", "Lakh", "Crore"
    };

    public static String convertToIndianCurrency(double amount) {
        long rupees = (long) amount;
        long paise = Math.round((amount - rupees) * 100);

        StringBuilder result = new StringBuilder();
        if (rupees > 0) {
            result.append(convert(rupees)).append(" Rupees");
        }
        if (paise > 0) {
            if (rupees > 0) result.append(" ");
            result.append(convert(paise)).append(" Paise");
        }
        if (result.length() == 0) {
            result.append("Zero Rupees");
        }
        result.append(" Only");
        return result.toString();
    }

    public static String convert(long number) {
        if (number == 0) return "Zero";

        String[] parts = new String[4];
        parts[0] = convertNumber((int) (number % 1000)); // units
        number /= 1000;

        parts[1] = convertNumber((int) (number % 100)); // thousands
        number /= 100;

        parts[2] = convertNumber((int) (number % 100)); // lakhs
        number /= 100;

        parts[3] = convertNumber((int) (number));       // crores

        StringBuilder word = new StringBuilder();

        for (int i = 3; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                if (word.length() > 0) word.append(" ");
                word.append(parts[i]).append(" ").append(scales[i]);
            }
        }

        return word.toString().trim().replaceAll("\\s+", " ");
    }

    private static String convertNumber(int number) {
        if (number == 0) return "";

        if (number < 20) {
            return units[number];
        } else if (number < 100) {
            return tens[number / 10] + (number % 10 != 0 ? " " + units[number % 10] : "");
        } else {
            return units[number / 100] + " Hundred" +
                   (number % 100 != 0 ? " " + convertNumber(number % 100) : "");
        }
    }
}


package fr.maxlego08.items.api.utils;

public class Between {

    public static int between(int value, int min, int max) {
        return value > max ? max : Math.max(value, min);
    }

}

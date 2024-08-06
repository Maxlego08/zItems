package fr.maxlego08.items.api.utils;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ArrayHelper {

    public static <T> T[][] removeEmptyRowsAndCols(T[][] array) {
        Set<Integer> nonEmptyRows = new HashSet<>();
        Set<Integer> nonEmptyCols = new HashSet<>();

        // Determine non-empty rows and columns in a single pass
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] != null ) {
                    nonEmptyRows.add(i);
                    nonEmptyCols.add(j);
                }
            }
        }

        // Create the new array without empty rows and columns
        T[][] resizedArray = (T[][]) new Object[nonEmptyRows.size()][nonEmptyCols.size()];

        int newRow = 0;
        for (int i : nonEmptyRows) {
            int newCol = 0;
            for (int j : nonEmptyCols) {
                resizedArray[newRow][newCol] = array[i][j];
                newCol++;
            }
            newRow++;
        }

        return resizedArray;
    }

}

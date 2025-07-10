package fr.maxlego08.items.zcore.utils.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pagination<T> {

    public List<T> paginateReverse(List<T> list, int inventorySize, int page) {
        List<T> currentList = new ArrayList<>();
        if (page == 0) page = 1;
        int idStart = list.size() - 1 - ((page - 1) * inventorySize);
        int idEnd = idStart - inventorySize;
        if (idEnd < list.size() - inventorySize && list.size() < inventorySize * page) idEnd = -1;
        for (int a = idStart; a != idEnd; a--)
            currentList.add(list.get(a));
        return currentList;
    }

    public List<T> paginate(List<T> list, int size, int page) {
        List<T> currentList = new ArrayList<>();
        if (page <= 0) page = 1;
        int idStart = ((page - 1)) * size;
        int idEnd = idStart + size;
        if (idEnd > list.size()) idEnd = list.size();
        for (int a = idStart; a != idEnd; a++)
            currentList.add(list.get(a));
        return currentList;
    }

    public List<T> paginateReverse(Map<?, T> map, int size, int page) {
        return paginateReverse(new ArrayList<>(map.values()), size, page);
    }

    public List<T> paginate(Map<?, T> map, int inventorySize, int page) {
        return paginate(new ArrayList<>(map.values()), inventorySize, page);
    }

}

package fr.maxlego08.items.api.configurations.state;

import fr.maxlego08.items.api.Item;

public interface ItemSlot {

        int slot();

        Item item();

        int amount();

    }
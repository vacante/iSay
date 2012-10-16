package com.patrickanker.isay;

import com.patrickanker.lib.config.PropertyConfiguration;

public class ItemAliasManager {

    private final PropertyConfiguration itemConfig = new PropertyConfiguration("/iSay/items");

    public ItemAliasManager()
    {
        this.itemConfig.load();

        if (this.itemConfig.getAllEntries().isEmpty()) {
            this.itemConfig.setString("337", "/helpop");
            this.itemConfig.setString("318", "/broadcast");
        }
    }

    public void shutDown()
    {
        this.itemConfig.save();
    }

    public String getAliasForItem(int type)
    {
        String foo = Integer.toString(type);

        if (this.itemConfig.hasEntry(foo)) {
            return this.itemConfig.getString(foo);
        }
        return null;
    }
}
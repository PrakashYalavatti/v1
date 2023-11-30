package com.simtech.app1.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class RVParentItem implements Serializable {

    // Declaration of the variables
    private String ParentItemTitle;
    private ArrayList<RVChildItem> ChildItemList;

    // Constructor of the class
    // to initialize the variables
    public RVParentItem(
            String ParentItemTitle,
            ArrayList<RVChildItem> ChildItemList)
    {

        this.ParentItemTitle = ParentItemTitle;
        this.ChildItemList = ChildItemList;
    }

    // Getter and Setter methods
    // for each parameter
    public String getParentItemTitle()
    {
        return ParentItemTitle;
    }

    public void setParentItemTitle(
            String parentItemTitle)
    {
        ParentItemTitle = parentItemTitle;
    }

    public ArrayList<RVChildItem> getChildItemList()
    {
        return ChildItemList;
    }

    public void setChildItemList(
            ArrayList<RVChildItem> childItemList)
    {
        ChildItemList = childItemList;
    }
}
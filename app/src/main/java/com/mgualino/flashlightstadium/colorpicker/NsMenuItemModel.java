package com.mgualino.flashlightstadium.colorpicker;

/**
 * Created by mgualino on 25/7/15.
 */
public class NsMenuItemModel {

    public int title;
    //public int iconRes;
    public int _id;
    public int colorSquare;
    //public int counter;
    public boolean isHeader;

    public NsMenuItemModel(int title, int colorSquare,boolean header, int id) {
        this.title = title;
        this.colorSquare=colorSquare;
        this.isHeader=header;
        this._id=id;
        //this.counter=counter;
    }


    public NsMenuItemModel(int title, int colorSquare,int id) {
        this(title,colorSquare,false,id);
    }


}

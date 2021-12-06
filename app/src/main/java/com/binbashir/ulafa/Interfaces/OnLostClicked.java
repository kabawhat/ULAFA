package com.binbashir.ulafa.Interfaces;

import com.binbashir.ulafa.Model.Lost_Item;

public interface OnLostClicked {
    void onAddImageClicked(String  document_id,String image_url);
    void onDeletePostClicked(Lost_Item lostItem);
    void onViewParentClicked(Lost_Item lostItem);
    void onViewMessageClicked(Lost_Item lostItem);

}

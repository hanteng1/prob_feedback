package com.example.t_hant.pagefold;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t_hant on 1/21/2018.
 */

public class UIData {

    public static final List<Data> Cmd_List = new ArrayList<Data>();

    static {
        UIData.Cmd_List.add(new UIData.Data("Clipboard"));
        UIData.Cmd_List.add(new UIData.Data("Slides"));
        UIData.Cmd_List.add(new UIData.Data("Font"));
        UIData.Cmd_List.add(new UIData.Data("Paragraph"));
        UIData.Cmd_List.add(new UIData.Data("Drawing"));
        UIData.Cmd_List.add(new UIData.Data("Editing"));
        UIData.Cmd_List.add(new UIData.Data("Text"));
        UIData.Cmd_List.add(new UIData.Data("Symbols"));
    }


    public static final class Data {
        public final String title;

        private Data(String title)
        {
            this.title = title;
        }

    }

}

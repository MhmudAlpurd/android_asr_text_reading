package com.cogniteye.cogtrgoogleasr.titleRec;

import android.graphics.Point;
import android.util.Log;

import com.google.mlkit.vision.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TitleRecognizer {
    Map<Integer, String> map = new HashMap<>();

    public String recognizeTitleForLabel(Text txt, List<Text.Line> lines){
        //return: text of biggest bounding box for label, and ... for card!
        String textOfBiggestBox = null;
        map.clear();


        for (int i=0; i<txt.getTextBlocks().size(); i++){
            for(int j=0; j<txt.getTextBlocks().get(i).getLines().size(); j++){ //lines of one block!
                Point[] cornersOfLine = txt.getTextBlocks().get(i).getLines().get(j).getCornerPoints();
                //cornersOfLine: (top_left:(x_0, y_0), top_right:(x_1, y_0), bottom_right:(x_1, y_1), bottom_left:(x_0, y_1))
                //biggestBoundingBox = (x_1 - x_0) * (y_1 - y_0)
                //biggestHeight = (y_1 - y_0)

                assert cornersOfLine != null;
                if(cornersOfLine.length != 0){
                    Log.d("Text247", "cornersofline: " + Arrays.toString(cornersOfLine));
                    int y_0 = cornersOfLine[0].y ;
                    int y_1 = cornersOfLine[2].y ;

                   // Log.d("Text247", "y_0: " + y_0);
                   // Log.d("Text247", "y_1: " + y_1);

                    int height_length = Math.abs(y_1 - y_0);
                    String txtOfLine = txt.getTextBlocks().get(i).getLines().get(j).getText();

                    Log.d("Text247", "textofline: " + txtOfLine);

                    map.put(height_length, txtOfLine);
                   // Log.d("Text247", "liney: " + y_0);
                   // Log.d("Text247", "linexy: " + Arrays.toString(new Point[]{cornersOfLine[2]}));

                }


            }
}
        Log.d("TESTMAP", map.toString());
        textOfBiggestBox = textOfMaxLength(map);
        return textOfBiggestBox;

    }


    public String recognizeTitleForCard(Text txt, List<Text.Line> lines){
        String text = txt.getText().replaceAll("\\d"," ");
        return text;
    }


    public String textOfMaxLength(Map<Integer, String> map){
        int maxKey = 0;

         Set<Integer> mapKeys =  map.keySet();

         for (int i : mapKeys) {
             if (i > maxKey) {
                 maxKey = i;
             }
         }

        Log.d("TESTMAP", "Values: " + map.values());
        Log.d("TESTMAP", "Keys: " + map.keySet());
        Log.d("TESTMAP", "maxKey: " + maxKey);
        Log.d("TESTMAP", "maxValue: " + map.get(maxKey));
        return map.get(maxKey);

    }
}

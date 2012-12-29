package org.ideaplugins.syncedit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WholeWordIndexFinder {


    private String _searchString;



    public WholeWordIndexFinder(String searchString) {
        this._searchString = searchString;
    }



    public List<IndexWrapper> findIndexesForKeyword(String keyword) {
        String regex = "\\b" + keyword + "\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this._searchString);

        List<IndexWrapper> wrappers = new ArrayList<IndexWrapper>();

        while (matcher.find()) {
            int end = matcher.end();
            int start = matcher.start();
            IndexWrapper wrapper = new IndexWrapper(start, end);
            wrappers.add(wrapper);
        }
        return wrappers;
    }

}

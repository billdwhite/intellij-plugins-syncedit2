package org.ideaplugins.syncedit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WholeWordIndexFinder {


    private String searchString;



    public WholeWordIndexFinder(String searchString) {
        this.searchString = searchString;
    }



    public static void main(String[] args) {
        WholeWordIndexFinder finder = new WholeWordIndexFinder("don’t be evil.being evil is bad");
        List<IndexWrapper> indexes = finder.findIndexesForKeyword("be");
        System.out.println("Indexes found " + indexes.size() + " keyword found at index : " + indexes.get(0).getStart());
    }



    public List<IndexWrapper> findIndexesForKeyword(String keyword) {
        String regex = "\\b" + keyword + "\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchString);

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

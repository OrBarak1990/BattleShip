package managers;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class Saver {
    private List<String> pages;
    private ListIterator<String> itr;
    private boolean lastReplayIsPrev = false, lastReplayIsNext = false;
    private int maxPos, index;
    private final static int MIN_POS = 0;

    public Saver(){
        pages = new LinkedList<>();
    }

    public void addPage(String page){
        pages.add(page);
    }

    public String getFirstPage(){
        itr = pages.listIterator(0);
        index = 0;
        maxPos = pages.size() - 1;
        return itr.next();
    }

    public String getLastPage(){
        itr = pages.listIterator(pages.size() - 1);
        maxPos = pages.size() - 1;
        return itr.next();
    }

    public String getNextPage() throws NoSuchElementException {
        if(maxPos < pages.size() - 1){
            maxPos = pages.size() - 1;
            int currentIndex = itr.nextIndex() - 1;
            itr = pages.listIterator(currentIndex);
        }

        if(itr.nextIndex() > maxPos)
            throw new NoSuchElementException();
        if(lastReplayIsPrev)
            itr.next();
        lastReplayIsNext = true;
        lastReplayIsPrev = false;
        if(itr.nextIndex() > maxPos)
            throw new NoSuchElementException();
        return itr.next();

    }

    public String getPrevPage() throws NoSuchElementException {
        if(maxPos < pages.size() - 1){
            maxPos = pages.size() - 1;
            int currentIndex = itr.nextIndex() - 1;
            itr = pages.listIterator(currentIndex);
        }
        if(itr.previousIndex() < MIN_POS)
            throw new NoSuchElementException();
        if(lastReplayIsNext)
            itr.previous();
        lastReplayIsPrev = true;
        lastReplayIsNext = false;
        if(itr.previousIndex() < MIN_POS)
            throw new NoSuchElementException();
        return itr.previous();
    }
}

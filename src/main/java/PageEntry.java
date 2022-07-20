public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry (String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    @Override
    public int compareTo(PageEntry o) {
        Integer count = this.count;
        return count.compareTo(o.getCount());
    }

    @Override
    public String toString() {
        return "PageEntry{" +
                "pdfName=" + pdfName +
                ", page=" + page +
                ", count=" + count +
                '}';
    }
}



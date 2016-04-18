/**
 * Created by thomasstuckey on 1/30/16.
 */
public class DocumentInfo {
    public Integer docRelevance = 0;
    public Integer docFrequency = 0;

    public DocumentInfo(Integer relevance) {
        this.docRelevance=relevance;
        this.docFrequency++;
    }

}

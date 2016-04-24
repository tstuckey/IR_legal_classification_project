package utilities;

import java.util.HashMap;

/**
 * Created by thomasstuckey on 4/23/16.
 */
public class ClassificationType {
    public HashMap<String, DocumentInfo> featureHash;//key: feature name;
                                                     // value: counts per Document for each feature
    public Boolean hasAnyFeatures = false;

    public ClassificationType() {
        this.featureHash = new HashMap<>();
    }

    public void addFeature(String feature, Integer featureID) {
        if (featureHash.isEmpty()) {
            //add a feature to the Hash
            featureHash.put(feature,new DocumentInfo(featureID));
        }
        if (featureHash.containsKey(feature)) {
            //just increment the DocumentInfo
            featureHash.get(feature).featureCount++;
            hasAnyFeatures = true;
        }else{
            featureHash.put(feature,new DocumentInfo(featureID));
        }
    }

}

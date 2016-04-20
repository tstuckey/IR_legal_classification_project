package utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class PropertiesShell {
    Properties prop = new Properties();
    
	public PropertiesShell(String xmlFileName){
		try {
            prop.loadFromXML(new FileInputStream(xmlFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
            System.err.println("Couldn't find the xml configuration file");
            try {
                System.err.println("Current dir is : " + (new File(".")).getCanonicalPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
		} catch (InvalidPropertiesFormatException f) {
			// TODO Auto-generated catch block
			f.printStackTrace();
		}
		catch (IOException io) {
			// TODO Auto-generated catch block
			io.printStackTrace();
		}
		
	}
	
	public String queryFor(String key){
		return prop.getProperty(key);
	}
	

}

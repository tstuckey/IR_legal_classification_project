package utilities;


import java.util.HashMap;

public class GeneralParsers {

	public GeneralParsers(){

	}
    class AuthenticationStructure{
        String host;
        int port;
        String user;
        String clear_password;
    }

    /**
     *
     * @param xmlFileName
     * @return
     */
    public HashMap getDbParams(String xmlFileName){
		PropertiesShell t_props=new PropertiesShell(xmlFileName);
        HashMap t_map= new HashMap();
        t_map.put("host",t_props.queryFor("host"));
        t_map.put("port",t_props.queryFor("port"));
        t_map.put("usr",t_props.queryFor("usr"));
        t_map.put("passwd",t_props.queryFor("passwd"));
        t_map.put("db",t_props.queryFor("db"));
        t_map.put("topDirectory",t_props.queryFor("topDirectory"));
		return t_map;
	}

	
}

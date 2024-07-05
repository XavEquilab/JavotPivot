package ga.fr.JavotPivot;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import ga.fr.ModelsDB.PylierDB;

public class ConfigThread extends Thread {
    private int _formatDataFromPylierToHyperviseurPattern;

    public ConfigThread() throws Exception{
        getConfigurations();
    }

    // tourne sur son propre thread
    public void run() {
        ThreadsSharedData.Logger.info("Config thread started");
        try {
            while (true) {
                // repeat in 10 minutes
                //Thread.sleep(600000);                
                //ThreadsSharedData.Logger.info("Config thread : 10 minutes as passed by");

                //getConfigurations();
            }            
        } catch (Exception e) {
            ThreadsSharedData.Logger.error("Config thread : " + e.getMessage());
    
            // TODO: handle exception, dès que le restClient a une erreur, on atteri ici donc pas robuste aux erreurs pour l'instant
        }
    }

    private void getConfigurations() throws Exception{
        ThreadsSharedData.PyliersDictionnary = getPyliersDictionnary();
        ThreadsSharedData.Logger.info("Pyliers = " + ThreadsSharedData.PyliersDictionnary);

        getTimearrays();
    }
	private ConcurrentHashMap<Integer, String> getPyliersDictionnary() throws Exception{
		ConcurrentHashMap<Integer, String> pyliersDictionnary = new ConcurrentHashMap<Integer, String>();
		
		// get json pyliers config from hyperviseur API
		PylierDB[] pylierDBs = ThreadsSharedData.HttpClient.get()
			.uri(ThreadsSharedData.HyperviseurApiUrl + "/api/config/pylier")
			.header("Accept", "application/json")
			.retrieve()
			.body(PylierDB[].class);
		
		for (PylierDB pylierDB : pylierDBs) {	
			pyliersDictionnary.put(pylierDB.pylier_id, pylierDB.pylier_url);
		}
		return pyliersDictionnary;
	}

    private static void getTimearrays(){
        String timearrayResponse = ThreadsSharedData.HttpClient.get()
			.uri(ThreadsSharedData.HyperviseurApiUrl + "/api/timearray/all")
			.header("Accept", "application/json")
			.retrieve()
			.body(String.class);

        ThreadsSharedData.Logger.info("timearrayResponse = " + timearrayResponse);
	}
}

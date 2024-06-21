package ga.fr.JavotPivot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class JavotPivotApplication {	
	private static final Logger logger = LoggerFactory.getLogger(JavotPivotApplication.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(JavotPivotApplication.class, args);
		logger.info("Starting");

		String databaseAddress = GetDatabaseAddress(context);
		logger.info("Database address = " + databaseAddress);

		Map<Integer, String> pyliersDictionnary = GetPyliersDictionnary(databaseAddress);

		// Run Command routine every second
		CommandThread commandThread = new CommandThread(databaseAddress, pyliersDictionnary);
		logger.info("Starting Command thread");
		commandThread.start();
	}

	private static String GetDatabaseAddress(ApplicationContext p_context){
        Environment env = p_context.getEnvironment();

		String databaseAddress = env.getProperty("javotPivot.databaseApi.address");
		if(databaseAddress == null ||databaseAddress.isBlank() || databaseAddress.isEmpty()){
			// TODO: recup adresse api BDD par exploration reseau
		}
		return databaseAddress;
	}

	private static Map<Integer, String> GetPyliersDictionnary(String p_databaseAddress){
		Map<Integer, String> pyliersDictionnary = new HashMap<Integer, String>();
		RestClient restClient = RestClient.create();
		try {
			// get json pyliers config from database
			String configPylierResponse = restClient.get()
			.uri(p_databaseAddress + "/api/config/pylier.json")
			//.uri("http://{p_databaseAddress}/Pylier/gtc/appareils.json", p_databaseAddress)
			.header("Accept", "application/json")
			//.accept(APPLICATION_JSON) 
			.retrieve()
			.body(String.class);
			
			logger.info("configPylierResponse = {configPylierResponse}", configPylierResponse);
			// TODO: avoir l'addresse de chaque pyliers en interrogeant la bdd
			// deserialize json
			//CommandeDB[] commandes = new ObjectMapper().readValue(configPylierResponse, CommandeDB[].class);
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
            restClient.delete();
        }
		return pyliersDictionnary;
	}
}

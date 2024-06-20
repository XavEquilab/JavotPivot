package ga.fr.JavotPivot;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class JavotPivotApplication {
    @Value("${javotPivot.databaseApi.address}")
    private static String _databaseAdress;

	public static void main(String[] args) {
		SpringApplication.run(JavotPivotApplication.class, args);

		_databaseAdress = GetDatabaseAddress();
		Map<Integer, String> pyliersDictionnary = GetPyliersDictionnary();

		// Run commande routine every seconds
		CommandThread commandThread = new CommandThread(_databaseAdress, pyliersDictionnary);
		commandThread.start();
	}

	private static String GetDatabaseAddress(){
		if(_databaseAdress.isBlank() || _databaseAdress.isEmpty()){
			// TODO: recup adresse api BDD par exploration reseau
			return null;
		}
		else{
			return _databaseAdress;
		}
	}

	private static Map<Integer, String> GetPyliersDictionnary(){
		Map<Integer, String> pyliersDictionnary = new HashMap<Integer, String>();
		RestClient restClient = RestClient.create();
		try {
			// get json pyliers config from database
			String configPylierResponse = restClient.get()
			.uri("{_databaseAdress}/api/config/pylier.json", _databaseAdress)
			//.uri("http://{_databaseAdress}/Pylier/gtc/appareils.json", _databaseAdress)
			.header("Accept", "application/json")
			//.accept(APPLICATION_JSON) 
			.retrieve()
			.body(String.class);
			
			// TODO: avoir l'addresse de chaque pyliers en interrogeant la bdd
			// deserialize json
			CommandeDB[] commandes = new ObjectMapper().readValue(configPylierResponse, CommandeDB[].class);
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
            restClient.delete();
        }
		return pyliersDictionnary;
	}
}

package ga.fr.JavotPivot;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CommandThread extends Thread {
    private String _databaseAdress;
    private Map<Integer, String> _pyliersDictionnary;
    private List<Integer> _commandsProcessed = new ArrayList<Integer>(); 

    public CommandThread(String p_databaseAddress, Map<Integer, String> p_pyliersDictionnary){
        _databaseAdress = p_databaseAddress;
        _pyliersDictionnary = p_pyliersDictionnary;
    }

    // tourne sur son propre thread
    public void run() {
        RestClient restClient = RestClient.create();
        try {
            while (true) {
                // get json commands from database
                String commandesJson = restClient.get()
                    .uri("{_databaseAdress}/api/commandes.json", _databaseAdress)
                    //.uri("http://{_databaseAdress}/Pylier/gtc/appareils.json", _databaseAdress)
                    .header("Accept", "application/json")
                    //.accept(APPLICATION_JSON) 
                    .retrieve()
                    .body(String.class);

                // deserialize json
                CommandeDB[] commandes = new ObjectMapper().readValue(commandesJson, CommandeDB[].class);

                // TODO: faire une dichotomie par pyliers concernés
                for (CommandeDB commandeDB : commandes) {
                    if(_commandsProcessed.contains(commandeDB.getId())) {
                        continue;
                    } else{
                        _commandsProcessed.add(commandeDB.getId());
                    }
                }
                // TODO: _commandsProcessed

                // send commands to Pylier
                ResponseEntity<Void> pylierResponse = restClient.post()
                    .uri("{_bddAPIAddress}/api/commandes.json", _bddAPIAddress)
                    //.uri("http://{_bddAPIAddress}/Pylier/gtc/appareils.json", _bddAPIAddress)
                    .header("Content-Type", "application/json")
                    //.contentType(APPLICATION_JSON)
                    .body(commandesJson)
                    .retrieve()
                    .toBodilessEntity(); 

                // TODO: /acquittement/{id}
    
                // repeat in 1 second
                Thread.sleep(1000);
            }            
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            restClient.delete();
        }
    }
}

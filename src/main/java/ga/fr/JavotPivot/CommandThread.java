package ga.fr.JavotPivot;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public class CommandThread extends Thread {
	private final Logger logger = LoggerFactory.getLogger(JavotPivotApplication.class);

    private List<Integer> _commandsProcessed = new ArrayList<Integer>(); 

    public CommandThread() {} // si on ne met pas ce constructeur, ça ne compile pas    

    // tourne sur son propre thread
    public void run() {
		logger.info("Command thread started");
        try {
            while (true) {
                logger.info("Command thread : 1 second as passed by");
                    
                // get json commands from database
                String commandesResponse = ThreadsSharedData.HttpClient.get()
                    .uri(ThreadsSharedData.HyperviseurApiUrl + "/api/commandes.json")
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(String.class);

                logger.info("Command thread : commandesResponse = " + commandesResponse);

                ResponseEntity<String> pylierResponse = ThreadsSharedData.HttpClient.post()
                    .uri(ThreadsSharedData.PyliersDictionnary.get(1) + "/gtc/appareils.json")
                    .header("Content-Type", "application/json")
                    .body("[{'ref':1002,'set_reset_soft':1234}]")
                    .retrieve()
                    .toEntity(String.class); 

                logger.info("Command thread : Response status: " + pylierResponse.getStatusCode());
                logger.info("Command thread : Response headers = " + pylierResponse.getHeaders());
                logger.info("Command thread : Contents: " + pylierResponse.getBody());

                return;

/*
                for (CommandeDB commandeDB : commandes) {
                    if(_commandsProcessed.contains(commandeDB.getId())) {
                        continue;
                    } else{
                        //_commandsProcessed.add(commandeDB.getId()); pas mtn
                        // recup que les comandes a traiter
                    }
                            
                    // TODO: faire une dichotomie par pyliers concernés

                }
                // TODO: gérer _commandsProcessed: retirer ce qui n'a pas été actualisé par la bdd

                // send commands to Pylier
                ResponseEntity<Void> pylierResponse = _restClient.post()
                    .uri("{_hyperviseurApiUrl}/api/commandes.json", _hyperviseurApiUrl)
                    //.uri("http://{_bddAPIAddress}/Pylier/gtc/appareils.json", _bddAPIAddress)
                    .header("Content-Type", "application/json")
                    //.contentType(APPLICATION_JSON)
                    .body(commandesResponse)
                    .retrieve()
                    .toBodilessEntity(); 


                // TODO: /acquittement/{id} + ajouter id a _commandsProcessed
    
                // repeat in 1 second
                Thread.sleep(1000);
                */
            }            
        } catch (Exception e) {
		    logger.error("Command thread : " + e.getMessage());

            // TODO: handle exception, dès que le restClient a une erreur, on atteri ici donc pas robuste aux erreurs pour l'instant
        }
    }
}

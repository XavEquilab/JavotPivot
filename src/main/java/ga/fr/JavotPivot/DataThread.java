package ga.fr.JavotPivot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public class DataThread extends Thread {
	private final Logger logger = LoggerFactory.getLogger(JavotPivotApplication.class);

    public DataThread() {}

    // tourne sur son propre thread
    public void run() {
        logger.info("Data thread started");
        try {
            while (true) {
                logger.info("Data thread : 1 second as passed by");
                    
                // get json commands from database
                String dataResponse = ThreadsSharedData.HttpClient.get()
                    .uri(ThreadsSharedData.PyliersDictionnary.get(1) + "/gtc/appareils.json")
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(String.class);
    
                String dataFormated = dataResponse.replace('\'', '\"').replace("ref", "id"); // faire avec regex ou https://stackoverflow.com/questions/1326682/java-replacing-multiple-different-substring-in-a-string-at-once-or-in-the-most
                logger.info("Data thread : dataResponse = " + dataFormated);
    
                return;
                /* 
                ResponseEntity<String> pylierResponse = ThreadsSharedData.HttpClient.post()
                    .uri(ThreadsSharedData.HyperviseurApiUrl + "/api/variables")
                    .header("Content-Type", "application/json")
                    .body(dataFormated)
                    .retrieve()
                    .toEntity(String.class); 

                logger.info("Data thread : Response status: " + pylierResponse.getStatusCode());
                logger.info("Data thread : Response headers = " + pylierResponse.getHeaders());
                logger.info("Data thread : Contents: " + pylierResponse.getBody());

                return;
                // repeat in 1 second
                //Thread.sleep(1000);
                */                
            }            
        } catch (Exception e) {
            logger.error("Data thread : " + e.getMessage());
    
            // TODO: handle exception, dès que le restClient a une erreur, on atteri ici donc pas robuste aux erreurs pour l'instant
        }
    }
}

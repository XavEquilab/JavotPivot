package ga.fr.JavotPivot;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

public class ThreadsSharedData {
    public static final Logger Logger = LoggerFactory.getLogger(JavotPivotApplication.class);

    public static String HyperviseurApiUrl; // constante
    public static int BuildingId; // constante
    public static final RestClient HttpClient = RestClient.create(); // thread safe d'apres la doc mais synchrone donc peut etre qu'il faudra en créer un par thread
    public static ConcurrentHashMap<Integer,String> PyliersDictionnary; // thread safe pour la lecture (sans lockers) et l'ecriture ça va 
}

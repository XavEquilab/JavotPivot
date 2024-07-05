package ga.fr.JavotPivot;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

public class DataThread extends Thread {
    private Pattern _formatDataFromPylierToHyperviseurPattern;

    public DataThread() {
        // Create pattern of the format "(\'|ref)"
        Map<String,String> tokens = new HashMap<String,String>();
        tokens.put("\'", "\"");
        tokens.put("ref", "id");

        String patternString = "(" + StringUtils.join(tokens.keySet(), "|") + ")";
        _formatDataFromPylierToHyperviseurPattern = Pattern.compile(patternString);
    }

    // tourne sur son propre thread
    public void run() {
        ThreadsSharedData.Logger.info("Data thread started");
        try {
            while (true) {
                ThreadsSharedData.Logger.info("Data thread : 1 second as passed by");

                // get json data from pylier
                // TODO: faire ça pour chaque pylier
                String dataResponse = ThreadsSharedData.HttpClient.get()
                .uri(ThreadsSharedData.PyliersDictionnary.get(1) + "/gtc/appareils.json")
                .header("Accept", "application/json")
                .retrieve()
                .body(String.class);
                
                //String dataResponse = "[{'tcsilimit': 254, 'fc_state': 0, 'nightmode': 2, 'consovent': 0.0, 'tbatext': 5555.0, 'text': 5555.0, 'modeprog': 4, 'type': 'mtax_dalac_pae', 'consochaud': 0.0, 'etat': 1, 'co2': 5555, 'ref': 11417, 'tcsi_prog': 20.0, 'tamb': 5555.0, 'vitav': 0, 'tbatint': 5555.0, 'tairneuf': 5555.0, 'puissance': 0, 'vitan': 0, 'consoclim': 0.0, 'vitreelcomp': 0, 'tcsi': 20.0, 'refip': 1, 'fenetre': 2, 'fan': 10, 'vitfan': 2, 'tboard': 29.0, 'on_off': 1, 'inverter_status': 0, 'err': 0, 'cov': 5555.0, 'fault': 959, 'ov_state': 0, 'alarme': 3, 'elecv2': 9, 'hygro': 5555, 'mode': 4, 'tairvicie': 5555.0, 'conso': 0.0, 'icomp': 0.0}]";
                ThreadsSharedData.Logger.info("Data thread : dataResponse = " + dataResponse);

                String dataFormated = formateDataFromPylierToHyperviseur(dataResponse);
    
                ResponseEntity<String> pylierResponse = ThreadsSharedData.HttpClient.post()
                    .uri(ThreadsSharedData.HyperviseurApiUrl + "/api/variables")
                    .header("Content-Type", "application/json")
                    .body(dataFormated)
                    .retrieve()
                    .toEntity(String.class);

                ThreadsSharedData.Logger.info("Data thread : Response status: " + pylierResponse.getStatusCode());
                ThreadsSharedData.Logger.info("Data thread : Response headers = " + pylierResponse.getHeaders());
                ThreadsSharedData.Logger.info("Data thread : Contents: " + pylierResponse.getBody());

                return;
                // repeat in 1 second
                //Thread.sleep(1000);
            }            
        } catch (Exception e) {
            ThreadsSharedData.Logger.error("Data thread : " + e.getMessage());
    
            // TODO: handle exception, dès que le restClient a une erreur, on atteri ici donc pas robuste aux erreurs pour l'instant
        }
    }

    private String formateDataFromPylierToHyperviseur(String p_dataUnformated)
    {
        Map<String,String> tokens = new HashMap<String,String>();
        tokens.put("\'", "\"");
        tokens.put("ref", "id");

        Matcher matcher = _formatDataFromPylierToHyperviseurPattern.matcher(p_dataUnformated);

        StringBuilder sb = new StringBuilder();
        while(matcher.find()) {
            matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}

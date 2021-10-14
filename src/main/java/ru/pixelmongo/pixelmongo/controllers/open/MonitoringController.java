package ru.pixelmongo.pixelmongo.controllers.open;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.services.MonitoringService;

@RestController
@RequestMapping("/open/monitoring")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoring;

    @Autowired
    private DateFormatter df;

    @GetMapping
    public ResultDataMessage<List<MonitoringService.MonitoringResult>> monitoring(Locale loc) {
        return new ResultDataMessage<>(DefaultResult.OK,
                "Last update: "+df.print(new Date(monitoring.getLastUpdateTime()), loc),
                monitoring.getMonitoringList());
    }

    /**
     *
     * Monitoring data required by launcher. Result is XML document.
     *
     * @return
     */
    @GetMapping(value = "/launcher", produces = MediaType.APPLICATION_XML_VALUE)
    public String monitoringXml() {
        List<MonitoringService.MonitoringResult> results = monitoring.getMonitoringList();
        long lastUpdate = monitoring.getLastUpdateTime();
        try {
            return generateLauncherXML(results, lastUpdate);
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String generateLauncherXML(List<MonitoringService.MonitoringResult> results, long lastUpdate)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("root");
        for(MonitoringService.MonitoringResult result : results) {
            Element monitoring = doc.createElement("monitoring");
            Element id = doc.createElement("id");
            id.setTextContent(result.getServerTag());
            monitoring.appendChild(id);
            Element online = doc.createElement("isOnline");
            online.setTextContent(Boolean.toString(result.isOnline()));
            monitoring.appendChild(online);
            Element players = doc.createElement("players");
            Element curPlayers = doc.createElement("online");
            curPlayers.setTextContent(Integer.toString(result.getCurrentPlayers()));
            players.appendChild(curPlayers);
            Element maxPlayers = doc.createElement("max");
            maxPlayers.setTextContent(Integer.toString(result.getMaxPlayers()));
            players.appendChild(maxPlayers);
            monitoring.appendChild(players);
            root.appendChild(monitoring);
        }
        Element updated = doc.createElement("lastUpdate");
        updated.setTextContent(Integer.toString((int)(lastUpdate/1000)));
        root.appendChild(updated);
        doc.appendChild(root);

        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

}

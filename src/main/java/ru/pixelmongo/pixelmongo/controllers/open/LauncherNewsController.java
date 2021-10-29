package ru.pixelmongo.pixelmongo.controllers.open;

import java.io.StringWriter;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.pixelmongo.pixelmongo.model.dto.vk.VKPost;
import ru.pixelmongo.pixelmongo.model.dto.vk.VKWall;
import ru.pixelmongo.pixelmongo.services.VKService;

@RestController
@RequestMapping("/open/launcher/news")
public class LauncherNewsController {

    @Autowired
    private VKService vk;

    @GetMapping(value = "/{category}", produces = MediaType.APPLICATION_XML_VALUE)
    public String getWall(@PathVariable("category") String category) {
        try {
            return printResult(vk.getWall(category));
        } catch (Exception ex) {
            VKService.LOGGER.catching(ex);
            return "<root>Error</root>";
        }
    }

    private String printResult(VKWall wall) throws ParserConfigurationException, TransformerException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("root");

        for(VKPost postData : wall.getItems()) {
            if(postData.getText().isEmpty()) continue;
            Element post = doc.createElement("data");
            Element id = doc.createElement("id");
            id.setTextContent(Integer.toString(postData.getId()));
            post.appendChild(id);
            Element text = doc.createElement("text");
            text.setTextContent(findText(postData, 10));
            post.appendChild(text);
            Element date = doc.createElement("date");
            date.setTextContent(Integer.toString(postData.getDate()));
            post.appendChild(date);
            root.appendChild(post);
        }

        Element updated = doc.createElement("last_update");
        updated.setTextContent(Integer.toString((int)(vk.getLastUpdate()/1000)));
        root.appendChild(updated);

        Element count = doc.createElement("real_count");
        count.setTextContent(Integer.toString(wall.getCount()));
        root.appendChild(count);

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

    private String findText(VKPost post, int deep) {
        if(!post.getText().isEmpty()) return post.getText();
        if(deep <= 0) return "";
        for(VKPost post2 : post.getReposts()) {
            String text = findText(post2, deep-1);
            if(!text.isEmpty()) return text;
        }
        return "";
    }

}

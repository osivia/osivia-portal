package org.osivia.portal.api.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Spring portlet controller abstract super-class.
 * 
 * @author Cédric Krommenhoek
 */
public abstract class PortletController {

    /** Person service. */
    @Autowired
    protected PersonService personService;


    /**
     * Constructor
     */
    public PortletController() {
        super();
    }


    /**
     * Person search resource mapping.
     * 
     * @param request portlet request
     * @param response portlet response
     * @param session session status
     * @throws IOException
     */
    @ResourceMapping("person-search")
    public void personSearch(ResourceRequest request, ResourceResponse response, @RequestParam(value = "filter", required = false) String filter)
            throws PortletException, IOException {
        // JSON objects
        List<JSONObject> objects = new ArrayList<>();
        // Total results
        int total = 0;

        // Persons
        List<Person> persons = this.searchPersons(filter);
        for (Person person : persons) {
            // Search result
            JSONObject object = new JSONObject();
            object.put("id", person.getUid());

            // Display name
            String displayName;
            // Extra
            String extra;

            if (StringUtils.isEmpty(person.getDisplayName())) {
                displayName = person.getUid();
                extra = null;
            } else {
                displayName = person.getDisplayName();

                extra = person.getUid();
                if (StringUtils.isNotBlank(person.getMail()) && !StringUtils.equals(person.getUid(), person.getMail())) {
                    extra += " – " + person.getMail();
                }
            }

            object.put("displayName", displayName);
            object.put("extra", extra);
            object.put("avatar", person.getAvatar().getUrl());

            objects.add(object);
            total++;
        }

        // Results JSON object
        JSONObject results = new JSONObject();
        // Items JSON array
        JSONArray items = new JSONArray();
        items.addAll(objects);
        results.put("items", items);
        results.put("total", total);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }


    /**
     * Search persons.
     * 
     * @param filter search filter
     * @return persons
     * @throws PortletException
     */
    private List<Person> searchPersons(String filter) throws PortletException {
        // Criteria
        Person criteria = this.personService.getEmptyPerson();

        // Stripped filter
        String strippedFilter = StringUtils.strip(StringUtils.trimToEmpty(filter), "*");

        String tokenizedFilter = strippedFilter + "*";
        String tokenizedFilterSubStr;
        if (StringUtils.isEmpty(strippedFilter)) {
            tokenizedFilterSubStr = tokenizedFilter;
        } else {
            tokenizedFilterSubStr = "*" + strippedFilter + "*";
        }

        criteria.setUid(tokenizedFilter);
        criteria.setCn(tokenizedFilter);
        criteria.setSn(tokenizedFilter);
        criteria.setGivenName(tokenizedFilter);
        criteria.setMail(tokenizedFilter);

        criteria.setDisplayName(tokenizedFilterSubStr);

        return this.personService.findByCriteria(criteria);
    }

}

package br.com.jadson.ciworkflowstrader.util;


import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager YAML files
 */
@Component
public class YamlUtil {

    /**
     * This method count the most common words in Yaml file. Not considering the keys
     * @return
     */
    public Map<String, Integer> countMostCommonsWordsInYaml(String wfContent) {

        Map<String, Integer> words = new HashMap<>();

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(new ByteArrayInputStream(wfContent.getBytes()));
        List<Object> values = new ArrayList<>();
        extractedValues(data, values);

        for (Object yamlField : values) {
            if (yamlField != null) {
                String[] tokens = yamlField.toString().replaceAll("\n", " ").split(" ");

                for (String token : tokens) {
                    token = token.toLowerCase();
                    if (token.length() > 2 && token.matches("[a-zA-Z]+")) { // remove keywords and symbols
                        if (words.containsKey(token)) {
                            int counter = words.get(token);
                            counter++;
                            words.put(token, counter);
                        } else {
                            words.put(token, 1);
                        }
                    }
                }
            }

        }

        return words;

    }

    /**
     * This method loop through the data of a yaml file returning the list of values from that file
     *
     * @param yamlData
     * @param values
     * @return
     */
    private List<Object> extractedValues(Map<String, Object> yamlData, List<Object> values) {
        for (Object key: yamlData.keySet()) {

            Object value = yamlData.get(key);
            if(value instanceof Map)
                extractedValues((Map<String, Object>) value, values);
            else
            if(value instanceof List) { // list field (fields with multi values)
                List list = (List) value;
                for (Object element : list){
                    if(element instanceof Map)
                        extractedValues((Map<String, Object>) element, values);
                    else
                        values.add(element);
                }
            }else
                values.add(value);
        }
        return values;
    }

    /**
     * Check is the YAML file contains some of specific words passed by parameter
     * @param yamlData
     * @param wordList
     * @return
     */
    public boolean containsWord(Map<String, Object> yamlData, final List<String> wordList) {
        if(yamlData != null) {
            List<Object> values = extractedValues(yamlData, new ArrayList<>());
            for (Object v : values) {
                if (v != null ) {
                    if(v instanceof String) {
                        String[] vlist = ((String) v).split(" ");
                        for (String vtokens : vlist){
                            if(wordList.contains(vtokens.toLowerCase())){
                                return true;
                            }
                        }
                    }else {
                        if (wordList.contains(v.toString().toLowerCase()))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * CHeck if the YAML file data contains CI events "pull" and "pull_request"
     * @param yamlData
     * @return
     */
    public boolean containsCIEvents(Map<String, Object> yamlData) {

        if(yamlData != null)
            for (Object key: yamlData.keySet()) {

                if(key.toString().equals("on")  || key.toString().equals("true")  )  { // declaration of events
                    Object value = yamlData.get(key);

                    if(value instanceof Map){
                        Map<String, Object> events = (Map<String, Object>) value;
                        for (Object eventsKey: events.keySet()) {
                            if (eventsKey != null && ( eventsKey.toString().equalsIgnoreCase("push") || eventsKey.toString().equalsIgnoreCase("pull_request") ) ){
                                return true;
                            }
                        }
                    }else{
                        // when the branches are not declared
                        if(value instanceof ArrayList) {
                            for (Object valueItem : (ArrayList) value){
                                if (valueItem != null && ( valueItem.toString().equalsIgnoreCase("push") || valueItem.toString().equalsIgnoreCase("pull_request") ) ){
                                    return true;
                                }
                            }
                        }else{
                            // direct the element   on : push
                            if (value != null && ( value.toString().equalsIgnoreCase("push") || value.toString().equalsIgnoreCase("pull_request") ) ){
                                return true;
                            }
                        }
                    }

                }

                Object value = yamlData.get(key);
                if(value instanceof Map) {
                    boolean r = containsCIEvents((Map<String, Object>) value);
                    if (r)
                        return true;
                } else {
                    if (value instanceof List) { // list field (fields with multi values)
                        List list = (List) value;
                        for (Object element : list) {
                            if (element instanceof Map) {
                                boolean r = containsCIEvents((Map<String, Object>) element);
                                if (r)
                                    return true;
                            }
                        }
                    }
                }
            }

        return false;
    }


    /**
     * Checks in the YAML file data contains the "ckeckout" actions
     * @param yamlData
     * @return
     */
    public boolean containsCheckoutAction(Map<String, Object> yamlData) {

        if(yamlData != null)
            for (Object key: yamlData.keySet()) {

                Object value = yamlData.get(key);

                if(value instanceof Map) {
                    boolean r = containsCheckoutAction((Map<String, Object>) value);
                    if(r)
                        return true;
                }else {
                    if (value instanceof List) { // list field (fields with multi values)
                        List list = (List) value;
                        for (Object element : list) {
                            if (element instanceof Map) {
                                boolean r = containsCheckoutAction((Map<String, Object>) element);
                                if(r)
                                    return true;
                            }else{
                                if (value != null && value.toString().startsWith("actions/checkout")) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        if (value != null && value.toString().startsWith("actions/checkout")) {
                            return true;
                        }
                    }
                }
            }

        return false;
    }


    /**
     * CHecks if the workflow name container the identifier passed as a parameter
     * @param yamlData
     * @param identifier
     * @return
     */
    public boolean containsInWorkflowName(Map<String, Object> yamlData, String identifier) {
        if(yamlData != null)
            for (Object key: yamlData.keySet()) {

                if(key.toString().equalsIgnoreCase("name")){
                    Object value = yamlData.get(key);
                    if(value != null) {
                        String[] tokens = value.toString().split(" ");
                        for (String t : tokens) {
                            if( t.equalsIgnoreCase("ci") )
                                return true;
                        }
                    }else{
                        return false;
                    }
                }

                Object value = yamlData.get(key);

                if(value instanceof Map) {
                    boolean r = containsInWorkflowName((Map<String, Object>) value, identifier);
                    if(r)
                        return r;
                }else {
                    if (value instanceof List) { // list field (fields with multi values)
                        List list = (List) value;
                        for (Object element : list) {
                            if (element instanceof Map) {
                                boolean r = containsInWorkflowName((Map<String, Object>) element, identifier);
                                if(r)
                                    return r;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }

        return false;
    }

    /**
     * https://api.github.com/repos/AdAway/AdAway/actions/workflows?page=1&per_page=10
     *   "name": "Android CI",
     *   "path": ".github/workflows/android-ci.yml",
     * @param fileName
     * @return
     */
    public boolean hasCIInFileName(String fileName) {
        return fileName.equalsIgnoreCase("ci.yml") || fileName.equalsIgnoreCase("ci.yaml")
                || fileName.matches(".*" + "-ci.yml" + "$") || fileName.matches(".*" + "-ci.yaml" + "$") ;
    }


}

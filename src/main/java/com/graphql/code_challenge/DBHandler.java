package com.graphql.code_challenge;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

// Class to handle interaction with mongo database
public class DBHandler {
    private MongoClient client;
    private MongoDatabase mongo;
    private MongoCollection<Document> collection;

    DBHandler(String host, int port, String databaseName, String collectionName, boolean clearDatabase) {
        if (host == null) {
            host = "localhost";
        }
        if (port == 0) {
            port = 27017;
        }
        if (databaseName == null) {
            databaseName = "wholePDV";
        }
        if (collectionName == null) {
            collectionName = "pdvCollection";
        }

        this.client = new MongoClient(host, port);
        this.mongo = client.getDatabase(databaseName);
        this.collection = this.mongo.getCollection(collectionName);

        // Clear database --> used for testing purposes
        if (clearDatabase) {
            clearDatabase();
            this.mongo = client.getDatabase(databaseName);
            this.collection = this.mongo.getCollection(collectionName);
        }

        System.out.println("DBHandler object created");
    }

    public boolean addPDV(Document newPDVDoc) {
        if(verifyPDVDocument(newPDVDoc)) {
            this.collection.insertOne(newPDVDoc);
            return true;
        }

        return false;
    }

    public void clearDatabase() {
        this.collection.drop();
        this.mongo.drop();
    }

    public void closeClient() {
        this.client.close();

        System.out.println("Client connection to database closed");
    }

    public Document findById(String id) {
        return this.collection.find(eq("_id", new ObjectId(id))).first();
    }

    public FindIterable<Document> getDocuments(){
        return this.collection.find();
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public Document getPDV(int id) {
        String idStr = Integer.toString(id);
        return this.getPDV(idStr);
    }

    public Document getPDV(String id) {
        MongoCursor<Document> cursor = this.collection.find().iterator();
        while(cursor.hasNext()) {
            Document currentPDVDoc = cursor.next();

            if(currentPDVDoc.getString("id").equals(id))
                return currentPDVDoc;
        }

        return null;
    }

    public boolean parsePDVJSON(JSONObject jsonObject) {
        try {
            JSONArray pdvsArray = (JSONArray) jsonObject.getJSONArray("pdvs");

            for (int i = 0; i < pdvsArray.length(); i++) {
                // Parse through all the single pdvs in the array
                JSONObject currentPDV = (JSONObject) pdvsArray.get(i);
                Document pdvDoc = new Document();
                pdvDoc.append("id", currentPDV.getString("id"));
                pdvDoc.append("tradingName", currentPDV.getString("tradingName"));
                pdvDoc.append("ownerName", currentPDV.getString("ownerName"));
                pdvDoc.append("document", currentPDV.getString("document"));

                Document addressDoc = parseAddress(currentPDV);
                if (addressDoc == null) {
                    System.out.println("JSON Exception while parsing address object of pdv with id: " + currentPDV.getString("id"));
                    return false;
                }
                pdvDoc.append("address", addressDoc);

                Document coverageAreaDoc = parseCoverageArea(currentPDV);
                if (coverageAreaDoc == null) {
                    System.out.println("JSON Exception while parsing coverage area object of pdv with id: " + currentPDV.getString("id"));
                    return false;
                }
                pdvDoc.append("coverageArea", coverageAreaDoc);

                if(verifyPDVDocument(pdvDoc))
                    this.collection.insertOne(pdvDoc);
            }
        } catch (JSONException e) {
            System.out.println("JSONException while parsing pdv json file.");
        }

        return true;
    }

    private Document parseAddress(JSONObject currentPDV) {
        Document addressDoc = new Document();

        try {
            JSONObject addressObject = currentPDV.getJSONObject("address");
            addressDoc.append("type", addressObject.getString("type"));

            JSONArray coordinatesArray = addressObject.getJSONArray("coordinates");
            List<Double> coordinatesList = new ArrayList<>();
            for (int j = 0; j < coordinatesArray.length(); j++) {
                coordinatesList.add((Double) coordinatesArray.get(j));
            }

            addressDoc.append("coordinates", coordinatesList);

            return addressDoc;
        } catch (JSONException e) {
            return null;
        }
    }

    private Document parseCoverageArea(JSONObject currentPDV) {
        Document coverageAreaDoc = new Document();

        try {
            JSONObject coverageAreaObject = currentPDV.getJSONObject("coverageArea");
            coverageAreaDoc.append("type", coverageAreaObject.getString("type"));

            // Top level: array of multiple polygons with outer layer and inner layers
            JSONArray polygonArray = coverageAreaObject.getJSONArray("coordinates");
            List<List<List<List<Double>>>> multiPolygonList = new ArrayList<>();

            for (int i = 0; i < polygonArray.length(); i++) {
                // Second level:  array of outer and inner layers of current polygon
                JSONArray layersArray = (JSONArray) polygonArray.get(i);
                List<List<List<Double>>> layersList = new ArrayList<>();

                for (int j = 0; j < layersArray.length(); j++) {
                    // Third level: array of vertices of current layer of current polygon
                    JSONArray verticesArray = (JSONArray) layersArray.get(j);
                    List<List<Double>> verticesList = new ArrayList<>();

                    for (int k = 0; k < verticesArray.length(); k++) {
                        // Fourth level: array of double points to represent current vertex
                        JSONArray valuesArray = (JSONArray) verticesArray.get(k);
                        List<Double> valuesList = new ArrayList<>();

                        for (int m = 0; m < valuesArray.length(); m++) {
                            valuesList.add((Double) valuesArray.get(m));
                        }

                        verticesList.add(valuesList);
                    }

                    layersList.add(verticesList);
                }

                multiPolygonList.add(layersList);
            }

            coverageAreaDoc.append("coordinates", multiPolygonList);

            return coverageAreaDoc;
        } catch (JSONException e) {
            return null;
        }
    }

    // Verify that pdvDoc has unique ID and CNPJ so it can be added to the database
    private boolean verifyPDVDocument(Document newPDVDoc) {
        String newId = newPDVDoc.getString("id");
        String newDocument = newPDVDoc.getString("document");

        MongoCursor<Document> cursor = this.collection.find().iterator();
        while(cursor.hasNext()) {
            Document currentPDVDoc = cursor.next();

            if(currentPDVDoc.getString("id").equals(newId) || currentPDVDoc.getString("document").equals(newDocument)) {
                System.out.println("Trying to add new PDV document with id or CNPJ/document number that already exists in the database. New PDV document will be ignored");
                return false;
            }
        }

        return true;
    }
}

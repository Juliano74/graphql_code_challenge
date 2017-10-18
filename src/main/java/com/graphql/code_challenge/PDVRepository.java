package com.graphql.code_challenge;

import com.util.geometry.Point2D;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PDVRepository {
    private final DBHandler dbHandler;

    public PDVRepository(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public PDV findById(String id) {
        return pdv(dbHandler.findById(id));
    }

    public List<PDV> getAllPDVs() {
        List<PDV> allPDVs = new ArrayList<>();
        for (Document doc : dbHandler.getDocuments()) {
           allPDVs.add(pdv(doc));
        }
        return allPDVs;
    }

    public PDV getPDV(String id) {
        return pdv(this.dbHandler.getPDV(id));
    }

    public void savePDV(PDV pdv) {
        Document pdvDoc = new Document();

        pdvDoc.append("id", pdv.getId()).append("tradingName", pdv.getTradingName()).append("ownerName", pdv.getOwnerName()).append("document", pdv.getDocument());

        // Prepare coverage Area Doc
        Document coverageAreaDoc = new Document();
        coverageAreaDoc.append("type", pdv.getCoverageArea().getType()).append("coordinates", pdv.getCoverageArea().getCoordinates());
        pdvDoc.append("coverageArea", coverageAreaDoc);

        // Prepare address document
        Document addressDoc = new Document();
        addressDoc.append("type", pdv.getAddress().getType()).append("coordinates", pdv.getAddress().getCoordinates());
        pdvDoc.append("address", addressDoc);

        dbHandler.addPDV(pdvDoc);
    }

    private PDV pdv(Document doc) {
        Document coverageAreaDoc = (Document) doc.get("coverageArea");
        Document addressDoc = (Document) doc.get("address");

        PDV pdv = new PDV(doc.getString("id"), doc.getString("tradingName"), doc.getString("ownerName"), doc.getString("document"),
                    new CoverageArea(coverageAreaDoc.getString("type"), (List<List<List<List<Double>>>>) coverageAreaDoc.get("coordinates")),
                    new Address(addressDoc.getString("type"), (List<Double>) addressDoc.get("coordinates")));

        return pdv;
    }

    public PDV searchClosestPDV(double lng, double lat) {
        Point2D location = new Point2D(lng, lat);
        List<PDV> sortedPDVs = Geometry.sortPDVsByDistance(getAllPDVs(), lng, lat);

        for(int i = 0; i < sortedPDVs.size(); i++) {
            if(Geometry.isPointInPDV(sortedPDVs.get(i), location))
                return sortedPDVs.get(i);
        }

        return null;    // PDV serving this location not found
    }
}

package com.graphql.code_challenge;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

    private static final com.graphql.code_challenge.PDVRepository pdvRepository;

    static {
        DBHandler dbHandler = new DBHandler("localhost", 27017, "graphql_code_challenge", "pdvs", true);

        // Get JSON Object
        JSONObject jsonObject = JSONHandler.loadJSON("pdvs.json");
        if(jsonObject == null) {
            System.out.println("Invalid JSON object found");
        }

        // Parse JSON Object by passing JSON object to DBHandler
        dbHandler.parsePDVJSON(jsonObject);

        pdvRepository = new com.graphql.code_challenge.PDVRepository(dbHandler);
    }

    public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new com.graphql.code_challenge.Query(pdvRepository), new com.graphql.code_challenge.Mutation(pdvRepository))
                .build()
                .makeExecutableSchema();
    }
}

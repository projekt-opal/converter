package org.diceresearch.elasticsearchwriter.utility;

import com.google.gson.Gson;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.diceresearch.elasticsearchwriter.entity.DataSet;
import org.junit.jupiter.api.Test;

class ModelMapperTest {

    @Test
    void toDataset() {
        Model model = ModelFactory.createDefaultModel();
        model.read("test/model.ttl");
        DataSet dataSet = ModelMapper.toDataset(model);
        Gson gson = new Gson();
        String s = gson.toJson(dataSet);
        System.out.println(s);
    }
}
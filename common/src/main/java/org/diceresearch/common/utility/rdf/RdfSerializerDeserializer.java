package org.diceresearch.common.utility.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public abstract class RdfSerializerDeserializer {
    public static byte[] serialize(Model model) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        model.write(outStream, "TURTLE");
        return outStream.toByteArray();
    }

    public static Model deserialize(byte[] bytes) {
        Model model = ModelFactory.createDefaultModel();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        model.read(stream, null, "TURTLE");
        return model;
    }
}

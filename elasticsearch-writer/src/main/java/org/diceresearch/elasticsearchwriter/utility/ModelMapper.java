package org.diceresearch.elasticsearchwriter.utility;


import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD4;
import org.dice_research.opal.common.vocabulary.Dqv;
import org.dice_research.opal.common.vocabulary.Opal;
import org.diceresearch.elasticsearchwriter.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class ModelMapper {
    public static DataSet toDataset(Model model) {
        DataSet dataSet = null;
        try {
            log.trace("called: toDataset {}", StructuredArguments.kv("model", model));
            Resource dataSetUri = getDataSetUri(model);
            assert dataSetUri != null;

            dataSet = new DataSet();

            dataSet.setUri(dataSetUri.getURI());
            setOriginalUrls(dataSet, model, dataSetUri);
            setTitle(dataSet, model, dataSetUri);
            setDescription(dataSet, model, dataSetUri);
            setLandingPage(dataSet, model, dataSetUri);
            setLanguagePage(dataSet, model, dataSetUri);
            setKeywords(dataSet, model, dataSetUri);
            setIssued(dataSet, model, dataSetUri);
            setModified(dataSet, model, dataSetUri);
            setLicense(dataSet, model, dataSetUri);
            setThemes(dataSet, model, dataSetUri);
            setQualityMetrics(dataSet, model, dataSetUri);
            setPublisher(dataSet, model, dataSetUri);
            setCreator(dataSet, model, dataSetUri);
            setSpatial(dataSet, model, dataSetUri);
            setContactPoint(dataSet, model, dataSetUri);
            setDistributions(dataSet, model, dataSetUri);
            setAccrualPeriodicity(dataSet, model, dataSetUri);
            setDcatIdentifierPeriodicity(dataSet, model, dataSetUri);
            setTemporal(dataSet, model, dataSetUri);

            return dataSet;
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("Finished toDataSet, returned {}", StructuredArguments.kv("dataSet", dataSet));
        return dataSet;
    }

    private static void setTemporal(DataSet dataSet, Model model, Resource dataSetUri) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.temporal);
        if (nodeIterator.hasNext()) {
            try {
                RDFNode temporalNode = nodeIterator.nextNode();
                log.trace("setTemporal, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("temporal", temporalNode.toString()));

                Resource resource = temporalNode.asResource();
                Temporal temporal = new Temporal();
                NodeIterator startDateIterator = model.listObjectsOfProperty(resource, DCAT.startDate);
                if (startDateIterator.hasNext()) {
                    RDFNode startDateNode = startDateIterator.nextNode();
                    log.trace("setTemporal, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("startDate", startDateNode.toString()));
                    temporal.setStartDate(startDateNode.asLiteral().getString());
                }
                NodeIterator endDateIterator = model.listObjectsOfProperty(resource, DCAT.endDate);
                if (endDateIterator.hasNext()) {
                    RDFNode endDateNode = endDateIterator.nextNode();
                    log.trace("setTemporal, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("endDate", endDateNode.toString()));

                    temporal.setEndDate(endDateNode.asLiteral().getString());
                }
                dataSet.setTemporal(temporal);
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setTemporal, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setDcatIdentifierPeriodicity(DataSet dataSet, Model model, Resource dataSetUri) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.identifier);
        if (nodeIterator.hasNext()) {
            try {
                RDFNode identifierNode = nodeIterator.nextNode();
                log.trace("setDcatIdentifierPeriodicity, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("identifier", identifierNode.toString()));
                dataSet.setDcatIdentifier(identifierNode.asLiteral().getString());
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setDcatIdentifierPeriodicity, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setAccrualPeriodicity(DataSet dataSet, Model model, Resource dataSetUri) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.accrualPeriodicity);
        if (nodeIterator.hasNext()) {
            try {
                RDFNode accrualPeriodicityNode = nodeIterator.nextNode();
                log.trace("setAccrualPeriodicity, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("accrualPeriodicity", accrualPeriodicityNode.toString()));
                dataSet.setAccrualPeriodicity(accrualPeriodicityNode.asResource().getURI());
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setAccrualPeriodicity, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setDistributions(DataSet dataSet, Model model, Resource dataSetUri) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCAT.distribution);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode distributionNode = nodeIterator.nextNode();
                log.trace("setDistributions, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("distribution", distributionNode.toString()));

                Resource resource = distributionNode.asResource();
                if (dataSet.getDistributions() == null)
                    dataSet.setDistributions(new ArrayList<>());

                Distribution distribution = new Distribution();
                distribution.setUri(resource.getURI());

                NodeIterator originalUrlsIterator = model.listObjectsOfProperty(resource, Opal.PROP_ORIGINAL_URI);
                while (originalUrlsIterator.hasNext()) {
                    if (distribution.getOriginalUrls() == null)
                        distribution.setOriginalUrls(new ArrayList<>());
                    RDFNode originalUrlNode = originalUrlsIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("originalUrl", originalUrlNode.toString()));

                    distribution.getOriginalUrls().add(originalUrlNode.asLiteral().getString());
                }

                NodeIterator titleIterator = model.listObjectsOfProperty(resource, DCTerms.title);
                if (titleIterator.hasNext()) {
                    RDFNode titleNode = titleIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("title", titleNode.toString()));
                    distribution.setTitle(titleNode.asLiteral().getString());
                }

                NodeIterator descriptionIterator = model.listObjectsOfProperty(resource, DCTerms.description);
                if (descriptionIterator.hasNext()) {
                    RDFNode descriptionNode = descriptionIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("description", descriptionNode.toString()));

                    distribution.setDescription(descriptionNode.asLiteral().getString());
                }

                NodeIterator issuedIterator = model.listObjectsOfProperty(resource, DCTerms.issued);
                if (issuedIterator.hasNext()) {
                    RDFNode issuedNode = issuedIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("issued", issuedNode.toString()));

                    distribution.setIssued(issuedNode.asLiteral().getString());
                }

                NodeIterator modifiedIterator = model.listObjectsOfProperty(resource, DCTerms.modified);
                if (modifiedIterator.hasNext()) {
                    RDFNode modifiedNode = modifiedIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("modified", modifiedNode.toString()));

                    distribution.setModified(modifiedNode.asLiteral().getString());
                }

                NodeIterator licenseIterator = model.listObjectsOfProperty(resource, DCTerms.license);
                if (licenseIterator.hasNext()) {
                    RDFNode licenseNode = licenseIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("license", licenseNode.toString()));

                    Resource license = licenseNode.asResource();
                    distribution.setLicense(new License());
                    distribution.getLicense().setUri(license.getURI());
                    // TODO: 2/17/20 Add Name
                }

                NodeIterator accessUrlIterator = model.listObjectsOfProperty(resource, DCAT.accessURL);
                if (accessUrlIterator.hasNext()) {
                    RDFNode accessUrlNode = accessUrlIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("accessUrl", accessUrlNode.toString()));

                    distribution.setAccessUrl(accessUrlNode.asResource().getURI());
                }

                NodeIterator downloadUrlIterator = model.listObjectsOfProperty(resource, DCAT.downloadURL);
                if (downloadUrlIterator.hasNext()) {
                    RDFNode downloadUrlNode = downloadUrlIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("downloadUrl", downloadUrlNode.toString()));

                    distribution.setDownloadUrl(downloadUrlNode.asResource().getURI());
                }

                NodeIterator formatIterator = model.listObjectsOfProperty(resource, DCTerms.format);
                if (formatIterator.hasNext()) { //todo catFish should catch this
                    RDFNode formatNode = formatIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("format", formatNode.toString()));

                    if (formatNode.isLiteral())
                        distribution.setFormat(formatNode.asLiteral().getString());
                }

                NodeIterator byteSizeIterator = model.listObjectsOfProperty(resource, DCAT.byteSize);
                if (byteSizeIterator.hasNext()) {
                    RDFNode byteSizeNode = byteSizeIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("byteSize", byteSizeNode.toString()));
                    distribution.setByteSize(byteSizeNode.asLiteral().getLong());
                }

                NodeIterator rightsIterator = model.listObjectsOfProperty(resource, DCTerms.rights);
                while (rightsIterator.hasNext()) {
                    if (distribution.getRights() == null)
                        distribution.setRights(new ArrayList<>());
                    RDFNode rightsNode = rightsIterator.nextNode();
                    log.trace("setDistributions, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("rights", rightsNode.toString()));

                    distribution.getRights().add(rightsNode.asResource().getURI());
                }

                log.trace("setDistributions, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("distribution", distribution));

                dataSet.getDistributions().add(distribution);

            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setDistributions, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setContactPoint(DataSet dataSet, Model model, Resource dataSetUri) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCAT.contactPoint);
        if (nodeIterator.hasNext()) {
            try {
                RDFNode contactPointNode = nodeIterator.nextNode();
                log.trace("setContactPoint, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("contactPoint", contactPointNode.toString()));
                Resource resource = contactPointNode.asResource();

                NodeIterator fnIterator = model.listObjectsOfProperty(resource, VCARD4.fn);
                if (fnIterator.hasNext()) {
                    RDFNode fnNode = fnIterator.nextNode();
                    log.trace("setContactPoint, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("fn", fnNode.toString()));
                    if (fnNode.isLiteral()) {
                        dataSet.setContactPoint(new ContactPoint());
                        dataSet.getContactPoint().setName(fnNode.asLiteral().getString());
                    }
                }

                NodeIterator emailIterator = model.listObjectsOfProperty(resource, VCARD4.hasEmail);
                if (emailIterator.hasNext()) {
                    RDFNode hasEmailNode = emailIterator.nextNode();
                    log.trace("setContactPoint, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("hasEmail", hasEmailNode.toString()));
                    if (hasEmailNode.isLiteral()) {
                        if (dataSet.getContactPoint() == null)
                            dataSet.setContactPoint(new ContactPoint());
                        dataSet.getContactPoint().setEmail(hasEmailNode.asLiteral().getString());
                    }
                }

                String address = getAddress(model, resource);
                if (address != null) {
                    if (dataSet.getContactPoint() == null)
                        dataSet.setContactPoint(new ContactPoint());
                    dataSet.getContactPoint().setPhone(address);
                }

                String phoneNumber = getPhoneNumber(model, resource);
                if (phoneNumber != null) {
                    if (dataSet.getContactPoint() == null)
                        dataSet.setContactPoint(new ContactPoint());
                    dataSet.getContactPoint().setPhone(phoneNumber);
                }

            } catch (NoSuchElementException e) {
                log.error("", e);
            }

        }
        log.trace("setContactPoint, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static String getAddress(Model model, Resource resource) {
        String address = null;
        try {
            NodeIterator addressIterator = model.listObjectsOfProperty(resource, VCARD4.hasAddress);
            if (addressIterator.hasNext()) {
                RDFNode hasAddressNode = addressIterator.nextNode();
                log.trace("getAddress, {}",
                        StructuredArguments.kv("hasAddress", hasAddressNode.toString()));
                Resource addressResource = hasAddressNode.asResource();
                NodeIterator valueIterator = model.listObjectsOfProperty(addressResource, VCARD4.street_address);
                if (valueIterator.hasNext()) {
                    RDFNode streetAddressNode = valueIterator.nextNode();
                    log.trace("getAddress, {}",
                            StructuredArguments.kv("street_address", streetAddressNode.toString()));
                    address = streetAddressNode.asLiteral().getString();
                }
            }
        } catch (NoSuchElementException e) {
            log.error("", e);
        }
        log.trace("getAddress returned, {}", StructuredArguments.kv("address", address));
        return address;
    }

    private static String getPhoneNumber(Model model, Resource resource) {
        String phoneNumber = null;
        try {
            NodeIterator phoneIterator = model.listObjectsOfProperty(resource, VCARD4.hasTelephone);
            if (phoneIterator.hasNext()) {
                RDFNode node = phoneIterator.nextNode();
                log.trace("getPhoneNumber, {}",
                        StructuredArguments.kv("hasTelephone", node.toString()));

                Resource phoneResource = node.asResource();
                NodeIterator valueIterator = model.listObjectsOfProperty(phoneResource, VCARD4.hasValue);
                if (valueIterator.hasNext()) {
                    RDFNode valueNode = valueIterator.nextNode();
                    log.trace("getPhoneNumber, {}",
                            StructuredArguments.kv("hasValue", valueNode.toString()));
                    phoneNumber = valueNode.asLiteral().getString();
                }
            }
        } catch (NoSuchElementException e) {
            log.error("", e);
        }
        log.trace("getPhoneNumber returned, {}", StructuredArguments.kv("phoneNumber", phoneNumber));

        return phoneNumber;
    }

    private static void setSpatial(DataSet dataSet, Model model, Resource dataSetUri) {
        model.listObjectsOfProperty(dataSetUri, DCTerms.spatial);
        // TODO: 2/17/20 Later
    }

    private static void setCreator(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.creator);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setCreator, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("creator", node.toString()));

                Creator creator = new Creator();
                Resource resource = node.asResource();
                creator.setUri(resource.getURI());
                dataSet.setCreator(creator);

                NodeIterator nameIterator = model.listObjectsOfProperty(resource, FOAF.name);
                if (nameIterator.hasNext()) {
                    RDFNode nameNode = nameIterator.nextNode();
                    log.trace("setCreator, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("name", nameNode.toString()));
                    dataSet.getCreator().setName(nameNode.asLiteral().getString());
                }
                NodeIterator mboxIterator = model.listObjectsOfProperty(resource, FOAF.mbox);
                if (mboxIterator.hasNext()) {
                    RDFNode mboxNode = mboxIterator.nextNode();
                    log.trace("setCreator, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("mbox", mboxNode.toString()));
                    dataSet.getCreator().setEmail(mboxNode.asResource().getURI());
                }
                NodeIterator homePageIterator = model.listObjectsOfProperty(resource, FOAF.homepage);
                if (homePageIterator.hasNext()) {
                    RDFNode homePageNode = homePageIterator.nextNode();
                    log.trace("setCreator, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("homepage", homePageNode.toString()));
                    dataSet.getCreator().setWebsite(homePageNode.asResource().getURI());
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("setCreator, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setPublisher(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.publisher);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setPublisher, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("publisher", node.toString()));


                Publisher publisher = new Publisher();
                Resource resource = node.asResource();
                publisher.setUri(resource.getURI());
                dataSet.setPublisher(publisher);

                NodeIterator nameIterator = model.listObjectsOfProperty(resource, FOAF.name);
                if (nameIterator.hasNext()) {
                    RDFNode nameNode = nameIterator.nextNode();
                    log.trace("setPublisher, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("name", nameNode.toString()));
                    dataSet.getPublisher().setName(nameNode.asLiteral().getString());
                }
                NodeIterator mboxIterator = model.listObjectsOfProperty(resource, FOAF.mbox);
                if (mboxIterator.hasNext()) {
                    RDFNode mboxNode = mboxIterator.nextNode();
                    log.trace("setPublisher, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("mbox", mboxNode.toString()));
                    dataSet.getPublisher().setEmail(mboxNode.asResource().getURI());
                }
                NodeIterator homePageIterator = model.listObjectsOfProperty(resource, FOAF.homepage);
                if (homePageIterator.hasNext()) {
                    RDFNode homePageNode = homePageIterator.nextNode();
                    log.trace("setPublisher, {}, {}",
                            StructuredArguments.kv("dataSetUri", dataSetUri),
                            StructuredArguments.kv("homepage", homePageNode.toString()));
                    dataSet.getPublisher().setWebsite(homePageNode.asResource().getURI()); // TODO: 2/17/20 asResource or asLiteral
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("setPublisher, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setQualityMetrics(DataSet dataSet, Model model, Resource dataSetUri) {
        List<QualityMetrics> qualityMetrics = new ArrayList<>();

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, Dqv.HAS_QUALITY_MEASUREMENT);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setQualityMetrics, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("has_quality_measurement", node.toString()));
                Resource measurement = node.asResource();

                NodeIterator measurementIterator = model.listObjectsOfProperty(measurement, Dqv.IS_MEASUREMENT_OF);
                RDFNode measurementNode = measurementIterator.nextNode();
                log.trace("setQualityMetrics, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("is_measurement_of", measurementNode.toString()));
                String isMeasurementOf = measurementNode.asResource().getURI();

                NodeIterator valueIterator = model.listObjectsOfProperty(measurement, Dqv.HAS_VALUE);
                RDFNode valueNode = valueIterator.nextNode();
                log.trace("setQualityMetrics, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("has_value", valueNode.toString()));
                int value = valueNode.asLiteral().getInt();
                qualityMetrics.add(new QualityMetrics(isMeasurementOf, value));
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        dataSet.setHasQualityMeasurements(qualityMetrics);

        log.trace("setQualityMetrics, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setThemes(DataSet dataSet, Model model, Resource dataSetUri) {

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCAT.theme);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.next();
                log.trace("setThemes, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("theme", node.toString()));
                if (dataSet.getThemes() == null)
                    dataSet.setThemes(new ArrayList<>());
                dataSet.getThemes().add(node.asResource().getURI());
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setThemes, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setLicense(DataSet dataSet, Model model, Resource dataSetUri) {

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.license);
        if (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setLicense, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("license", node.toString()));
                License license = new License();
                license.setUri(node.asResource().getURI());
                // TODO: 2/17/20 Name
                if (dataSet.getLicenses() == null)
                    dataSet.setLicenses(new ArrayList<>());
                dataSet.getLicenses().add(license);
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }
        log.trace("setLicense, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setModified(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.modified);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setModified, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("modified", node.toString()));
                dataSet.setModified(node.asLiteral().getString());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("setModified, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setIssued(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.issued);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setIssued, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("issued", node.toString()));
                dataSet.setIssued(node.asLiteral().getString());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("setIssued, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static void setKeywords(DataSet dataSet, Model model, Resource dataSetUri) {
        List<String> keywords = new ArrayList<>();
        List<String> keywords_de = new ArrayList<>();

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCAT.keyword);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setKeywords, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("keyword", node.toString()));
                String language = node.asLiteral().getLanguage();
                if (language == null || language.isEmpty() || language.equals("en"))
                    keywords.add(node.asLiteral().getString());
                else if (language.equals("de"))
                    keywords_de.add(node.asLiteral().getString());
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }

        dataSet.setKeywords(keywords);
        dataSet.setKeywords_de(keywords_de);

        log.trace("setKeywords, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setLanguagePage(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.language);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setLanguagePage, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("language", node.toString()));
                dataSet.setLanguage(node.isLiteral() ?
                        node.asLiteral().getString() :
                        node.asResource().getURI());
            }
        } catch (NoSuchElementException e) {
            log.error("", e);
        }
        log.trace("setLanguagePage, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setLandingPage(DataSet dataSet, Model model, Resource dataSetUri) {
        try {
            NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCAT.landingPage);
            if (nodeIterator.hasNext()) {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setLandingPage, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("landingPage", node.toString()));
                dataSet.setLandingPage(node.isLiteral() ? node.asLiteral().getString() : node.asResource().getURI());
            }
        } catch (NoSuchElementException e) {
            log.error("", e);
        }
        log.trace("setLandingPage, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setDescription(DataSet dataSet, Model model, Resource dataSetUri) {

        String description = null, description_de = null;

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.description);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setDescription, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("description", node.toString()));
                if (node.isLiteral()) {
                    String language = node.asLiteral().getLanguage();
                    if (language == null || language.isEmpty() || language.equals("en"))
                        description = node.asLiteral().getString();
                    else if (language.equals("de"))
                        description_de = node.asLiteral().getString();
                }
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }

        dataSet.setDescription(description);
        dataSet.setDescription_de(description_de);

        log.trace("setDescription, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setTitle(DataSet dataSet, Model model, Resource dataSetUri) {

        String title = null, title_de = null;

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, DCTerms.title);
        while (nodeIterator.hasNext()) {
            try {
                RDFNode node = nodeIterator.nextNode();
                log.trace("setTitle, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("title", node.toString()));
                if (node.isLiteral()) {
                    String language = node.asLiteral().getLanguage();
                    if (language == null || language.isEmpty() || language.equals("en"))
                        title = node.asLiteral().getString();
                    else if (language.equals("de"))
                        title_de = node.asLiteral().getString();
                }
            } catch (NoSuchElementException e) {
                log.error("", e);
            }
        }

        dataSet.setTitle(title);
        dataSet.setTitle_de(title_de);
        log.trace("setTitle, {}", StructuredArguments.kv("dataSet", dataSet));

    }

    private static void setOriginalUrls(DataSet dataSet, Model model, Resource dataSetUri) {

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSetUri, Opal.PROP_ORIGINAL_URI);
        while (nodeIterator.hasNext()) {
            try {
                if (dataSet.getOriginalUrls() == null) dataSet.setOriginalUrls(new ArrayList<>());
                RDFNode node = nodeIterator.nextNode();
                log.trace("setOriginalUrls, {}, {}",
                        StructuredArguments.kv("dataSetUri", dataSetUri),
                        StructuredArguments.kv("originalUrl", node.toString()));
                dataSet.getOriginalUrls().add(node.asResource().getURI());
            } catch (NoSuchElementException ex) {
                log.error("", ex);
            }
        }
        log.trace("setOriginalUrls, {}", StructuredArguments.kv("dataSet", dataSet));
    }

    private static Resource getDataSetUri(Model model) {
        Resource dataSetUri = null;
        try {
            ResIterator resIterator = model.listSubjectsWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext())
                dataSetUri = resIterator.nextResource();
        } catch (Exception e) {
            log.error("", e);
        }
        log.trace("getDataSetUri returned: {}", StructuredArguments.kv("dataSetUri", dataSetUri));
        return dataSetUri;
    }
}

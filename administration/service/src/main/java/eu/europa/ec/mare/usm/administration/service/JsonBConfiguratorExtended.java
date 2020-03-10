package eu.europa.ec.mare.usm.administration.service;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;

import javax.json.bind.config.BinaryDataStrategy;

public class JsonBConfiguratorExtended extends JsonBConfigurator {


    public JsonBConfiguratorExtended() {
        super();
        withConfig();
    }

    public void withConfig() {
        config.withBinaryDataStrategy(BinaryDataStrategy.BASE_64);
    }
}

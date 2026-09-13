package com.roberto.orbit.util;

import java.io.File;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

public final class OrekitConfig {

    private OrekitConfig() {
    }

    public static void inicializar() {

    	System.out.println("Directorio de trabajo = "
    	        + System.getProperty("user.dir"));
    	
        File orekitData = new File("orekit-data");

        if (!orekitData.isDirectory()) {
            throw new IllegalStateException(
                "No se encuentra la carpeta orekit-data."
            );
        }

        DataProvidersManager manager =
            DataContext.getDefault().getDataProvidersManager();

        manager.clearProviders();
        manager.addProvider(new DirectoryCrawler(orekitData));
    }
}
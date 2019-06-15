package webfx.tool.buildtool.sourcegenerators;

import webfx.tool.buildtool.ProjectModule;
import webfx.tool.buildtool.util.textfile.TextFileReaderWriter;
import webfx.tool.buildtool.util.reusablestream.ReusableStream;

import java.nio.file.Files;

/**
 * @author Bruno Salmon
 */
final class GwtServiceLoaderSuperSourceGenerator {

    private final static String TEMPLATE =
            "// Generated by WebFx\n" +
            "package java.util;\n" +
            "\n" +
            "import java.util.Iterator;\n" +
            "import java.util.logging.Logger;\n" +
            "import webfx.platform.shared.util.function.Factory;\n" +
            "\n" +
            "public class ServiceLoader<S> implements Iterable<S> {\n" +
            "\n" +
            "    public static <S> ServiceLoader<S> load(Class<S> serviceClass) {\n" +
            "        switch (serviceClass.getName()) {\n" +
            "${generatedCasesCode}" +
            "            // UNKNOWN SPI\n" +
            "            default:\n" +
            "               Logger.getLogger(ServiceLoader.class.getName()).warning(\"Unknown \" + serviceClass + \" SPI - returning no provider\");\n" +
            "               return new ServiceLoader<S>();\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    private final Factory[] factories;\n" +
            "\n" +
            "    public ServiceLoader(Factory... factories) {\n" +
            "        this.factories = factories;\n" +
            "    }\n" +
            "\n" +
            "    public Iterator<S> iterator() {\n" +
            "        return new Iterator<S>() {\n" +
            "            int index = 0;\n" +
            "            @Override\n" +
            "            public boolean hasNext() {\n" +
            "                return index < factories.length;\n" +
            "            }\n" +
            "\n" +
            "            @Override\n" +
            "            public S next() {\n" +
            "                return (S) factories[index++].create();\n" +
            "            }\n" +
            "        };\n" +
            "    }\n" +
            "}";

    static void generateServiceLoaderSuperSource(ProjectModule module) {
        //GwtFilesGenerator.logSection("Generating " + module.getName() + " module java.util.ServiceLoader.java super source for GWT");
        StringBuilder sb = new StringBuilder();
        module.getExecutableProviders()
                .stream().sorted()
                .forEach(providers -> {
                    String spiClassName = providers.getSpiClassName();
                    ReusableStream<String> providerClassNames = providers.getProviderClassNames();
                    if (spiClassName.equals("webfx.platform.shared.services.resource.spi.impl.gwt.GwtResourceBundle")) {
                        if (Files.exists(GwtEmbedResourcesBundleSourceGenerator.getJavaFilePath(module)))
                            providerClassNames = ReusableStream.concat(
                                    providerClassNames,
                                    ReusableStream.of(GwtEmbedResourcesBundleSourceGenerator.getProviderClassName(module))
                            );
                    }
                    sb.append("            case \"").append(spiClassName).append("\": return new ServiceLoader<S>(");
                    int initialLength = sb.length();
                    providerClassNames.forEach(providerClassName -> {
                        if (sb.length() > initialLength)
                            sb.append(", ");
                        sb.append(getProviderConstructorReference(providerClassName));
                    });
                    sb.append(");\n");
                });
        TextFileReaderWriter.writeTextFileIfNewOrModified(TEMPLATE.replace("${generatedCasesCode}", sb), module.getResourcesDirectory().resolve("super/java/util/ServiceLoader.java"));
    }

    private static String getProviderConstructorReference(String providerClassName) {
        return providerClassName.replace('$', '.')
                + (providerClassName.equals("webfx.platform.shared.services.json.spi.impl.gwt.GwtJsonObject") ? "::create" : "::new");
    }

}

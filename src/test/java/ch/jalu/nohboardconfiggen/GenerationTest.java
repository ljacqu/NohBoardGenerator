package ch.jalu.nohboardconfiggen;

import ch.jalu.nohboardconfiggen.config.NohbConfiguration;
import ch.jalu.nohboardconfiggen.config.NohboardConfigExporter;
import ch.jalu.nohboardconfiggen.config.NohboardConfigGenerator;
import ch.jalu.nohboardconfiggen.definition.KeyboardConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests parsing of a config file and the creation of the NohBoard JSON.
 */
public class GenerationTest {

    private final DefinitionParser parser = new DefinitionParser();
    private final NohboardConfigGenerator generator = new NohboardConfigGenerator();
    private final NohboardConfigExporter exporter = new NohboardConfigExporter();

    @Test
    void test_tr1_simple() {
        checkFileGeneratesExpectedJson("tr1_simple.txt", "./expected/tr1_simple_exp.json");
    }

    private void checkFileGeneratesExpectedJson(String pathToFile, String pathToFileWithExpectedResult) {
        // given
        Path file = getResourceFile(pathToFile);

        // when
        KeyboardConfig config = parser.parseConfig(file);
        NohbConfiguration nohbConfig = generator.generate(config);
        String json = exporter.toJson(nohbConfig);

        // then
        String expected = readFile(getResourceFile(pathToFileWithExpectedResult));
        assertThat(json.replace("\r\n", "\n"), equalTo(expected.replace("\r\n", "\n")));
    }

    private Path getResourceFile(String path) {
        try {
            URL url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                throw new IllegalStateException("File '" + path + "' does not exist");
            }
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to get file '" + path + "'", e);
        }
    }

    private String readFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read from '" + path + "'", e);
        }
    }
}

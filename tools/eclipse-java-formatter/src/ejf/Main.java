package ejf;

import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Properties;

public class Main {

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.err.println("usage: eclipse-formatter <style.xml> <file1.java> <file2.java> ...");
            System.exit(1);
        }
        boolean someFailed = false;
        final Formatter fm = new Formatter(args[0]);
        for (final String f : Arrays.copyOfRange(args, 1, args.length))
            try {
                write(f, fm.format(read(f)));
            } catch (Exception e) {
                System.err.println(e.toString() + ": " + f);
                someFailed = true;
            }
        if (someFailed) System.exit(2);
    }

    private static String read(final String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private static void write(final String path, final String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
    }

    private static class Formatter {
        private final CodeFormatter formatter;

        public Formatter(final String xmlFile) {
            formatter = new DefaultCodeFormatter(props(xmlFile));
        }

        public String format(final String input) throws BadLocationException {
            final IDocument doc = new Document();
            doc.set(input);
            final TextEdit edit = formatter.format(
                    CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
                    input, 0, input.length(), 0, "\n");
            if (edit == null) throw new RuntimeException("formatting failed");
            edit.apply(doc);
            return doc.get();
        }

        private static Properties props(final String xmlFile) {
            return new Properties();
        }
    }


}

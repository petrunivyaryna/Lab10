package ucu.edu;

import java.time.Duration;
import java.time.Instant;

public class TimedDocument extends AbstractDecorator {

    public TimedDocument(Document document) {
        super(document);
    }

    @Override
    public String parse() {
        Instant start = Instant.now();
        String result = super.parse();
        Instant end = Instant.now();
        long elapsedTime = Duration.between(start, end).toMillis();
        System.out.println("Parsing took " + elapsedTime + " milliseconds.");
        return result;
    }
}

import org.junit.Test;
import utils.Util;

import java.sql.Date;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class TestUtil {

    @Test
    public void testGetDiffYears() {
        assertEquals(0, Util.getDiffYears(Date.from(Instant.now()), Date.from(Instant.now())));
    }
}
